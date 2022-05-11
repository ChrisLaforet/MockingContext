package com.chrislaforetsoftware.mockingcontext.ioc;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.MockingContextAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.MockitoAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.SpringAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.exception.CannotInstantiateClassException;
import com.chrislaforetsoftware.mockingcontext.exception.ReflectionFailedException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyInjector {

	private final Set<String> packagesToExplore;
	private final Object testClassInstance;

	private final InjectableLookup injectableLookup;

	private final Set<Pending> pendingInjectables = new HashSet<>();

	private final List<IAnnotationScanner> sourceScanners = createSourceScanners();

	private DependencyInjector(Object testClassInstance, Set<String> packagesToExplore, InjectableLookup injectableLookup) {
		this.testClassInstance = testClassInstance;
		this.packagesToExplore = packagesToExplore;
		this.injectableLookup = injectableLookup;
	}

	private List<IAnnotationScanner> createSourceScanners() {
		final List<IAnnotationScanner> scanners = new ArrayList<>();
		scanners.add(new MockitoAnnotationScanner());
		scanners.add(new SpringAnnotationScanner());
		scanners.add(new MockingContextAnnotationScanner());
		return scanners;
	}

	public static void discoverAndInjectDependencies(Object testClassInstance,
													 Set<String> packagesToExplore,
													 InjectableLookup injectableLookup) {
		final DependencyInjector dependencyInjector = new DependencyInjector(testClassInstance, packagesToExplore, injectableLookup);
		dependencyInjector.discoverAndInject();
	}

	public void discoverAndInject() {
		extractSourceAnnotations();

		discoverInjectables();
		injectPendingInjectables();
	}

	private void extractSourceAnnotations() {
		if (this.testClassInstance == null) {
			return;
		}

		for (Field field : this.testClassInstance.getClass().getDeclaredFields()) {
			for (IAnnotationScanner scanner : sourceScanners) {
				if (scanner.isAnnotatedAsSource(field)) {
					field.setAccessible(true);
					try {
						injectableLookup.add(new Injectable(field.getType().getName(), field.get(this.testClassInstance)));
						break;
					} catch (Exception ex) {
						throw new ReflectionFailedException(ex);
					}
				}
			}
		}
	}

	private void discoverInjectables() {
		for (String pkg : this.packagesToExplore) {
			for (Class<?> theClass : PathScanner.getAllClassesInPackage(pkg)) {
				discoverInjectableTargets(theClass);
			}
		}
	}

	private void injectPendingInjectables() {
		while (!pendingInjectables.isEmpty()) {
			int remaining = pendingInjectables.size();
			attemptToInitializePendingInjectables();
			if (pendingInjectables.size() == remaining) {
				throw new CannotInstantiateClassException(pendingInjectables.stream().map(Pending::getClassName).collect(Collectors.joining(", ")));
			}
		}
	}

	private void discoverInjectableTargets(Class<?> theClass) {
		for (IAnnotationScanner scanner : sourceScanners) {
			if (scanner.isAnnotatedAsTarget(theClass)) {
				if (attemptToInitializeInjectable(theClass)) {
					attemptToInitializePendingInjectables();
				}
				break;
			}
		}
	}

	private boolean attemptToInitializeInjectable(Class<?> theClass) {
		final Optional<ClassComponents> classComponents = ClassScanner.decomposeClass(theClass);
		if (!classComponents.isPresent()) {
			return false;
		}

		if (createInjectedInstance(classComponents.get())) {
			return true;
		}
		trackPendingInjectable(classComponents.get());
		return false;
	}

	private void attemptToInitializePendingInjectables() {
		pendingInjectables.removeIf(pending -> !pending.isPending() && attemptToInitializePendingInjectable(pending));
	}

	private boolean attemptToInitializePendingInjectable(Pending pending) {
		return createInjectedInstance(pending.getClassComponents());
	}

	private boolean createInjectedInstance(ClassComponents classComponents) {
		for (Class<?> neededClass: classComponents.getClasses()) {
			if (!injectableLookup.find(neededClass.getName()).isPresent()) {
				return false;
			}
		}
		injectableLookup.add(classComponents.instantiateClassWith(injectableLookup));

		this.pendingInjectables.forEach(pending ->
				pending.checkAndCancelWaitingFor(classComponents.getClass().getName()));
		return true;
	}

	private void trackPendingInjectable(ClassComponents classComponents) {
		Pending pending = new Pending(classComponents);
		for (Class<?> neededClass: classComponents.getClasses()) {
			pending.addPendingDependency(neededClass.getName());
		}
		this.pendingInjectables.add(pending);
	}
}
