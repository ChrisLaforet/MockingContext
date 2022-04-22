package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.ioc.DIContext;
import com.chrislaforetsoftware.mockingcontext.ioc.Injectable;
import com.chrislaforetsoftware.mockingcontext.ioc.PathScanner;

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
    private final Set<String> packagesToExplore = new HashSet<>();
    private Package testClassPackage;

    private final Map<String, Injectable> injectables = new HashMap<>();
    private DIContext context;

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

    List<String> getPackagesToExplore() {
        return new ArrayList<>(packagesToExplore);
    }

    String getTestClassName() {
        return this.testClass.getName();
    }

    public void addInjectable(Class<?> theClassToMatch, Object instance) {
        addInjectable(theClassToMatch.getName(), instance);
    }

    public void addInjectable(String className, Object instance) {
        Injectable injectable = new Injectable(className, instance);
        injectables.put(injectable.getClassName(), injectable);
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

        context = DIContext.createContextForPackage(testClassPackage.getName());

        // TODO: discover all of the injectables

        // TODO: start creating new objects and add to the injectables

        // TODO: inject injectables on creation of objects

        return this;
    }
}
