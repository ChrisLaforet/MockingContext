package com.chrislaforetsoftware.mockingcontext.ioc;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.MockingContextAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.MockitoAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.SpringAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.exception.ReflectionFailedException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
		// TODO: discover all of the injectables

		// TODO: an injectable may have to be pending awaiting its dependencies to be initialized

		// TODO: start creating new objects and add to the injectables

		// TODO: inject injectables on creation of objects
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

	private void discoverInjectableTargets(Class<?> theClass) {
		for (IAnnotationScanner scanner : sourceScanners) {
			if (scanner.isAnnotatedAsTarget(theClass)) {
				attemptToInitializeInjectable(theClass);

// TODO: do something here
//					final Injectable injectable = new Injectable(memberClass.getName(), memberClass);
//					injectables.put(injectable.getClassName(), injectable);
				break;
			}
		}
	}

	private void attemptToInitializeInjectable(Class<?> theClass) {
		final Optional<ClassComponents> classComponents = ClassScanner.decomposeClass(theClass);
		if (!classComponents.isPresent()) {
			return;
		}

		// determine if all autowired fields can be satisfied
		// then create instance
		// update Pending with new instance
		if (createPendingInjectable(classComponents.get())) {

			// check if any pendings can now initialize

			return;
		}
		trackPendingInjectable(classComponents.get());
	}

	private boolean createPendingInjectable(ClassComponents classComponents) {
		for (Class<?> neededClass: classComponents.getClasses()) {
			if (!injectableLookup.find(neededClass.getName()).isPresent()) {
				return false;
			}
		}

		injectableLookup.add(classComponents.instantiateClassWith(injectableLookup));

// TODO: Inject and create and add to injectables
		return true;
	}

	private void trackPendingInjectable(ClassComponents classComponents) {
		Pending pending = new Pending(classComponents.getTheClass().getName());
		for (Class<?> neededClass: classComponents.getClasses()) {
			pending.addPendingDependency(neededClass.getName());
		}
		this.pendingInjectables.add(pending);
	}

	private void injectInjectables() {

	}
}
