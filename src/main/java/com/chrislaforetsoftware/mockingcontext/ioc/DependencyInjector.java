package com.chrislaforetsoftware.mockingcontext.ioc;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.MockitoAnnotationScanner;

import java.lang.reflect.Field;
import java.util.*;

public class DependencyInjector {

	private final Set<String> packagesToExplore;
	private final Object testClassInstance;

	private final Map<String, Injectable> injectables;
	private final Set<Pending> pendingInjectables = new HashSet<>();

	static class Pending {
		private final String className;
		private final Set<String> pendingDependencies = new HashSet<>();

		public Pending(String className) {
			this.className = className;
		}

		public void addPendingDependency(String className) {
			this.pendingDependencies.add(className);
		}

		public boolean isWaitingFor(String className) {
			if (this.pendingDependencies.contains(className)) {
				this.pendingDependencies.remove(className);
				return true;
			}
			return false;
		}

		public boolean isPending() {
			return !this.pendingDependencies.isEmpty();
		}
	}

	private final List<IAnnotationScanner> sourceScanners = createSourceScanners();

	private DependencyInjector(Object testClassInstance, Set<String> packagesToExplore, Map<String, Injectable> injectables) {
		this.testClassInstance = testClassInstance;
		this.packagesToExplore = packagesToExplore;
		this.injectables = injectables;
	}

	private List<IAnnotationScanner> createSourceScanners() {
		final List<IAnnotationScanner> sourceScanners = new ArrayList<>();
		sourceScanners.add(new MockitoAnnotationScanner());
		return sourceScanners;
	}

	public static void discoverAndInjectDependencies(Object testClassInstance,
													 Set<String> packagesToExplore,
													 Map<String, Injectable> injectables) throws Exception{
		final DependencyInjector dependencyInjector = new DependencyInjector(testClassInstance, packagesToExplore, injectables);
		dependencyInjector.discoverAndInject();
	}


	public void discoverAndInject() throws Exception {
		extractSourceAnnotations();
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


}
