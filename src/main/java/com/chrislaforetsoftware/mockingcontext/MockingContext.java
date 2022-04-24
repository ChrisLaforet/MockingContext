package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.MockitoAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.ioc.DIContext;
import com.chrislaforetsoftware.mockingcontext.ioc.Injectable;
import com.chrislaforetsoftware.mockingcontext.ioc.PathScanner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MockingContext {

    public static final int CALLER_OFFSET = 2;

    private static final MockingContext instance = new MockingContext();

    private Class<?> testClass;

    private Object testClassInstance;

    private final Set<String> packagesToExplore = new HashSet<>();
    private Package testClassPackage;

    private final Map<String, Injectable> injectables = new HashMap<>();

    private MockingContext() {}

    public static MockingContext getInstance() throws Exception {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length >= (CALLER_OFFSET + 1)) {
            // caller is stackTraceElement[2]
            return instance.setTestClass(Class.forName(stackTraceElements[CALLER_OFFSET].getClassName()));
        }
        return instance;
    }

    private MockingContext setTestClass(Class<?> testClass) throws Exception {
        this.testClass = testClass;
        testClassPackage = testClass.getPackage();
        addPackageToExplore(testClassPackage);
        return this;
    }

    public MockingContext setTestClassInstance(Object testClassInstance) {
        this.testClassInstance = testClassInstance;
        return this;
    }

    List<String> getPackagesToExplore() {
        return new ArrayList<>(packagesToExplore);
    }

    String getTestClassName() {
        return this.testClass.getName();
    }

    public MockingContext addInjectable(Class<?> theClassToMatch, Object instance) {
        return addInjectable(theClassToMatch.getName(), instance);
    }

    public MockingContext addInjectable(String className, Object instance) {
        Injectable injectable = new Injectable(className, instance);
        injectables.put(injectable.getClassName(), injectable);
        return this;
    }

    public MockingContext addInjectable(Object instance) {
        return addInjectable(instance.getClass(), instance);
    }

    public MockingContext addPackageToExplore(String packageRoot) throws Exception {
        packagesToExplore.addAll(PathScanner.getAllPackages(packageRoot));
        return this;
    }

    public MockingContext addPackageToExplore(Package packageRoot) throws Exception {
        if (packageRoot != null && !packageRoot.getName().isEmpty()) {
            addPackageToExplore(packageRoot.getName());
        }
        return this;
    }

    public MockingContext addPackageToExplore(Class<?> theClass) throws Exception {
        if (theClass.getPackage() != null) {
            return addPackageToExplore(theClass.getPackage());
        }
        return this;
    }

    public MockingContext mockContext() throws Exception {

        // get a context
//        context = new DIContext(packagesToExplore);
        // for each package

//        context = DIContext.createContextForPackage(testClassPackage.getName());

        extractSourceAnnotations();

        // TODO: discover all of the injectables

        // TODO: start creating new objects and add to the injectables

        // TODO: inject injectables on creation of objects

        return this;
    }

    private void extractSourceAnnotations() throws Exception {
        if (this.testClassInstance == null) {
            return;
        }

        final List<IAnnotationScanner> sourceScanners = createSourceScanners();
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

    private List<IAnnotationScanner> createSourceScanners() {
        final List<IAnnotationScanner> sourceScanners = new ArrayList<>();
        sourceScanners.add(new MockitoAnnotationScanner());
        return sourceScanners;
    }

//    private void extractSourceAnnotationsFor(String packageName, List<IAnnotationScanner> sourceScanners) throws Exception {
//        for (Class<?> theClass : PathScanner.getAllClassesInPackage(packageName)) {
//            Class<?>[] o1 = theClass.getClasses();
//            Class<?>[] o2 = theClass.getDeclaredClasses();
//            for (Class<?> memberClass : theClass.getDeclaredClasses()) {
//                for (IAnnotationScanner scanner : sourceScanners) {
//                    if (scanner.isAnnotatedAsSource(memberClass)) {
//                        final Injectable injectable = new Injectable(memberClass.getName(), memberClass);
//                        injectables.put(injectable.getClassName(), injectable);
//                        break;
//                    }
//                }
//            }
//        }
//    }

    Map<String, Injectable> getInjectables() {
        return this.injectables;
    }
}
