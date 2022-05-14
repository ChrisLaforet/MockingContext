package com.chrislaforetsoftware.mockingcontext.ioc;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.MockingContextAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.MockitoAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.SpringAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.exception.CannotInstantiateClassException;
import com.chrislaforetsoftware.mockingcontext.exception.ReflectionFailedException;
import com.chrislaforetsoftware.mockingcontext.match.Pending;
import com.chrislaforetsoftware.mockingcontext.util.Traceable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyInjector extends Traceable {

	private final Set<String> packagesToExplore;
	private final Object testClassInstance;

	private final InjectableLookup injectableLookup;
	private final ClassResolver classResolver;

	private final Set<Pending> pendingInjectables = new HashSet<>();

	private final List<IAnnotationScanner> sourceScanners = createSourceScanners();

	private DependencyInjector(Object testClassInstance, Set<String> packagesToExplore, InjectableLookup injectableLookup, boolean isDebugMode) {
		super(isDebugMode);
		this.classResolver = new ClassResolver(packagesToExplore, isDebugMode);
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
													 InjectableLookup injectableLookup,
													 boolean isDebugMode) {
		final DependencyInjector dependencyInjector = new DependencyInjector(testClassInstance, packagesToExplore, injectableLookup, isDebugMode);
		dependencyInjector.discoverAndInject();
	}

	public void discoverAndInject() {
		discoverInjectablesInTestInstance();
		discoverOtherInjectables();
		reconcileUntrackedInjectables();
		injectPendingInjectables();
	}

	private void discoverInjectablesInTestInstance() {
		trace("Find injectables in test class instance");
		if (this.testClassInstance == null) {
			return;
		}

		for (Field field : this.testClassInstance.getClass().getDeclaredFields()) {
			for (IAnnotationScanner scanner : sourceScanners) {
				if (scanner.isAnnotatedAsSource(field)) {
					field.setAccessible(true);
					try {
						trace(String.format("  Found injectable for %s", InjectableLookup.cleanClassName(field.getType().getName())));
						injectableLookup.addInjectablesFor(field.get(this.testClassInstance));
						break;
					} catch (Exception ex) {
						throw new ReflectionFailedException(ex);
					}
				}
			}
		}
	}

	private void discoverOtherInjectables() {
		for (String pkg : this.packagesToExplore) {
			for (Class<?> theClass : PathScanner.getAllClassesInPackage(pkg)) {
				discoverInjectableTargets(theClass);
			}
		}
	}

	private void reconcileUntrackedInjectables() {
		final Map<String, String> pendingLookup = new HashMap<>();
		pendingInjectables.forEach(pending -> pendingLookup.put(pending.getClassName(), ""));
		injectableLookup.getInjectableClasses().forEach(injectable -> pendingLookup.put(injectable, ""));

		final Queue<String> dependencies = new LinkedList<>(pendingInjectables.stream().map(Pending::getPendingDependencies).flatMap(List::stream).collect(Collectors.toSet()));
		while (!dependencies.isEmpty()) {
			final String className = dependencies.remove();
			if (pendingLookup.containsKey(className)) {
				continue;
			}
			attemptToInitializeInjectable(getClassFromName(className));

			pendingLookup.put(className, "");
		}
	}

	private Class<?> getClassFromName(String className) {
		try {
			return Class.forName(className);
		} catch (Exception ex) {
			throw new ReflectionFailedException(ex);
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
				classResolver.add(theClass);
				if (!isInjectableTargetInitialized(theClass)) {
					if (attemptToInitializeInjectable(theClass)) {
						attemptToInitializePendingInjectables();
					}
					break;
				}
			}
		}
	}

	private boolean isInjectableTargetInitialized(Class<?> theClass) {
		return injectableLookup.find(InjectableLookup.cleanClassName(theClass.getName())).isPresent();
	}

	private boolean attemptToInitializeInjectable(Class<?> theClass) {
		final Optional<ClassComponents> classComponents = ClassScanner.decomposeClass(theClass, isDebugMode());
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
			if (!injectableLookup.find(InjectableLookup.cleanClassName(neededClass.getName())).isPresent()) {
				return false;
			}
		}
		injectableLookup.addInjectablesFor(classComponents.instantiateClassWith(injectableLookup));

		this.pendingInjectables.forEach(pending ->
				pending.checkAndCancelWaitingFor(InjectableLookup.cleanClassName(classComponents.getClass().getName())));
		return true;
	}

	private void trackPendingInjectable(ClassComponents classComponents) {
		Pending pending = new Pending(classComponents);
		for (Class<?> neededClass: classComponents.getClasses()) {
			pending.addPendingDependency(InjectableLookup.cleanClassName(neededClass.getName()));
		}
		this.pendingInjectables.add(pending);
	}
}
