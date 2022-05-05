package com.chrislaforetsoftware.mockingcontext.ioc;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.MockitoAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.SpringAnnotationScanner;

import java.lang.reflect.Field;
import java.util.*;

public class DependencyInjector {

	private final Set<String> packagesToExplore;
	private final Object testClassInstance;

	private final Map<String, Injectable> injectables;

	private final Set<Pending> pendingInjectables = new HashSet<>();

	private final List<IAnnotationScanner> sourceScanners = createSourceScanners();

	private DependencyInjector(Object testClassInstance, Set<String> packagesToExplore, Map<String, Injectable> injectables) {
		this.testClassInstance = testClassInstance;
		this.packagesToExplore = packagesToExplore;
		this.injectables = injectables;
	}

	private List<IAnnotationScanner> createSourceScanners() {
		final List<IAnnotationScanner> scanners = new ArrayList<>();
		scanners.add(new MockitoAnnotationScanner());
		scanners.add(new SpringAnnotationScanner());
		return scanners;
	}

	public static void discoverAndInjectDependencies(Object testClassInstance,
													 Set<String> packagesToExplore,
													 Map<String, Injectable> injectables) throws Exception{
		final DependencyInjector dependencyInjector = new DependencyInjector(testClassInstance, packagesToExplore, injectables);
		dependencyInjector.discoverAndInject();
	}

	public void discoverAndInject() throws Exception {
		extractSourceAnnotations();

		discoverInjectables();
		// TODO: discover all of the injectables

		// TODO: an injectable may have to be pending awaiting its dependencies to be initialized

		// TODO: start creating new objects and add to the injectables

		// TODO: inject injectables on creation of objects
	}

	private void extractSourceAnnotations() throws Exception {
		if (this.testClassInstance == null) {
			return;
		}

		for (Field field : this.testClassInstance.getClass().getDeclaredFields()) {
			for (IAnnotationScanner scanner : sourceScanners) {
				if (scanner.isAnnotatedAsSource(field)) {
					field.setAccessible(true);
					final Injectable injectable = new Injectable(field.getClass().getName(), field.get(this.testClassInstance));
					injectables.put(injectable.getClassName(), injectable);
					break;
				}
			}
		}
		//        for (String packageName : packagesToExplore) {
		//            for (Class<?> theClass : PathScanner.getAllClassesInPackage(packageName)) {
		//                extractSourceAnnotationsFor(packageName, sourceScanners);
		//            }
		//        }
	}

	private void discoverInjectables() throws Exception {
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
		final List<String> neededClasses = ClassScanner.decomposeClass(theClass);
		// determine if all autowired fields can be satisfied
		// then create instance
		// update Pending with new instance
		// check if any pendings can now initialize

		// otherwise add to Pending list
		Pending pending = new Pending(theClass.getName());


		for (String neededClass : neededClasses) {
			// TODO: determine if the neededClass is already in hand
			pending.addPendingDependency(neededClass);
		}
		this.pendingInjectables.add(pending);
	}

	private void injectInjectables() {

	}
}
