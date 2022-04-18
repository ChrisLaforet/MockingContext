package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.ioc.DIContext;

import java.util.HashSet;
import java.util.Set;

public class MockingContext {

    private static final MockingContext instance = new MockingContext();

    private Class<?> testClass;
    private Set<String> packagesToExplore = new HashSet<>();
    private DIContext context;

    private MockingContext() {}

    public static MockingContext getInstance() {
        return instance;
    }

    public MockingContext setTestClass(Class<?> testClass) {
        this.testClass = testClass;
        return this;
    }

    public MockingContext addPackageToExplore(Class<?> clazz) {
        if (clazz.getPackage() != null) {
            packagesToExplore.add(clazz.getPackage().getName());
        }
        return this;
    }

    public MockingContext mockContext() throws Exception {
        // TODO: get the package
        context = DIContext.createContextForPackage(null);

        // TODO: discover all of the injectables

        // TODO: start creating new objects and add to the injectables

        // TODO: inject injectables on creation of objects

        return this;
    }
}
