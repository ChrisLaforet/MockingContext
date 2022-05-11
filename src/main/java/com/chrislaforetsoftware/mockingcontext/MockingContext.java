package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.MockitoAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.ioc.DependencyInjector;
import com.chrislaforetsoftware.mockingcontext.ioc.Injectable;
import com.chrislaforetsoftware.mockingcontext.ioc.InjectableLookup;
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

//    private static final MockingContext instance = new MockingContext();

    private Class<?> testClass;

    private Object testClassInstance;

    private final Set<String> packagesToExplore = new HashSet<>();
    private Package testClassPackage;

    private final InjectableLookup injectableLookup = new InjectableLookup();

    private MockingContext() {}

    public static MockingContext createInstance() throws Exception {
        final MockingContext instance = new MockingContext();
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

    InjectableLookup getInjectableLookup() {
        return injectableLookup;
    }

    public MockingContext addInjectable(Class<?> theClassToMatch, Object instance) {
        return addInjectable(theClassToMatch.getName(), instance);
    }

    public MockingContext addInjectable(String className, Object instance) {
        injectableLookup.add(new Injectable(className, instance));
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
        DependencyInjector.discoverAndInjectDependencies(testClassInstance, packagesToExplore, injectableLookup);
        return this;
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
}
