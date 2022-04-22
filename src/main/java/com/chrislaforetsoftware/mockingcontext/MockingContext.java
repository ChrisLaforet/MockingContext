package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.ioc.DIContext;
import com.chrislaforetsoftware.mockingcontext.ioc.Injectable;

import java.util.*;
import java.util.stream.Collectors;


public class MockingContext {

    private static final MockingContext instance = new MockingContext();

    private Class<?> testClass;
    private final Set<String> packagesToExplore = new HashSet<>();
    private Package testClassPackage;

    private final Map<String, Injectable> injectables = new HashMap<>();
    private DIContext context;

    private MockingContext() {}

    public static MockingContext getInstance() throws ClassNotFoundException {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length > 3) {
            // caller is stackTraceElement[2]
            return instance.setTestClass(Class.forName(stackTraceElements[2].getClassName()));
        }
        return instance;
    }

    private MockingContext setTestClass(Class<?> testClass) {
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

    public MockingContext addPackageToExplore(Package pkg) {
        if (pkg != null && !pkg.getName().isEmpty()) {
            packagesToExplore.add(pkg.getName());
        }
        return this;
    }

    public MockingContext addPackageToExplore(Class<?> theClass) {
        if (theClass.getPackage() != null) {
            return addPackageToExplore(theClass.getPackage());
        }
        return this;
    }

    public MockingContext mockContext() throws Exception {

        // get a context
        context = new DIContext();
        // for each package

        context = DIContext.createContextForPackage(testClassPackage.getName());

        // TODO: discover all of the injectables

        // TODO: start creating new objects and add to the injectables

        // TODO: inject injectables on creation of objects

        return this;
    }
}
