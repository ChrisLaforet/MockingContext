package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.ioc.DIContext;
import com.chrislaforetsoftware.mockingcontext.ioc.Injectable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MockingContext {

    private static final MockingContext instance = new MockingContext();

    private Class<?> testClass;
    private Set<String> packagesToExplore = new HashSet<>();
    private Map<String, Injectable> injectables = new HashMap<>();
    private DIContext context;

    private MockingContext() {}

    public static MockingContext getInstance() {
        return instance;
    }

    public MockingContext setTestClass(Class<?> testClass) {
        this.testClass = testClass;
        return this;
    }

    public void addInjectable(Class<?> theClassToMatch, Object instance) {
        addInjectable(theClassToMatch.getName(), instance);
    }

    public void addInjectable(String className, Object instance) {
        Injectable injectable = new Injectable(className, instance);
        injectables.put(injectable.getClassName(), injectable);
    }

    public MockingContext addPackageToExplore(Class<?> theClass) {
        if (theClass.getPackage() != null) {
            packagesToExplore.add(theClass.getPackage().getName());
        }
        return this;
    }

    public MockingContext mockContext() throws Exception {
        if (testClass == null) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            if (stackTraceElements.length > 3) {
                // caller is stackTraceElement[2]
                setTestClass(Class.forName(stackTraceElements[2].getClassName()));
            }
        }

        // TODO: get the package
        context = DIContext.createContextForPackage(null);

        // TODO: discover all of the injectables

        // TODO: start creating new objects and add to the injectables

        // TODO: inject injectables on creation of objects

        return this;
    }
}
