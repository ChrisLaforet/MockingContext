package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.exception.ReflectionFailedException;
import com.chrislaforetsoftware.mockingcontext.ioc.DependencyInjector;
import com.chrislaforetsoftware.mockingcontext.match.Injectable;
import com.chrislaforetsoftware.mockingcontext.ioc.InjectableLookup;
import com.chrislaforetsoftware.mockingcontext.ioc.PathScanner;
import com.chrislaforetsoftware.mockingcontext.util.Traceable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MockingContext extends Traceable {

    private Object testClassInstance;

    private final Set<String> packagesToExplore = new HashSet<>();

    @SuppressWarnings("FieldCanBeLocal")
    private Package testClassPackage;

    private final InjectableLookup injectableLookup = new InjectableLookup();

    private MockingContext(boolean isDebugMode) {
        super(isDebugMode);
        trace("Initialized in debug tracing mode");
    }

    public static MockingContext createInstance(Object testClassInstance) {
        return createInstance(testClassInstance, false);
    }

    public static MockingContext createInstance(Object testClassInstance, boolean isDebugMode) {
        final MockingContext instance = new MockingContext(isDebugMode);
        instance.setTestClassInstance(testClassInstance);
        return instance;
    }


    @SuppressWarnings("UnusedReturnValue")
    public MockingContext setTestClassInstance(Object testClassInstance) {
        this.testClassInstance = testClassInstance;
        trace(String.format("Setting test class instance - %s", testClassInstance.getClass().getName()));

        testClassPackage = testClassInstance.getClass().getPackage();
        addPackageToExplore(testClassPackage);
        return this;
    }

    List<String> getPackagesToExplore() {
        return new ArrayList<>(packagesToExplore);
    }

    String getTestClassName() {
        return this.testClassInstance.getClass().getName();
    }

    InjectableLookup getInjectableLookup() {
        return this.injectableLookup;
    }

    public MockingContext addInjectable(Class<?> theClassToMatch, Object instance) {
        return addInjectable(theClassToMatch.getName(), instance);
    }

    public MockingContext addInjectable(String className, Object instance) {
        injectableLookup.add(new Injectable(className, instance));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public MockingContext addInjectable(Object instance) {
        return addInjectable(instance.getClass(), instance);
    }

    @SuppressWarnings("UnusedReturnValue")
    public MockingContext addPackageToExplore(String packageRoot) {
        try {
            trace(String.format("Set package to explore - %s", packageRoot));
            final Set<String> allPackages = PathScanner.getAllPackages(packageRoot);
            packagesToExplore.addAll(allPackages);
            if (isDebugMode()) {
                allPackages.forEach(packageName -> trace(String.format("  Includes package: %s", packageName)));
            }
            return this;
        } catch (Exception ex) {
            throw new ReflectionFailedException(ex);
        }
    }

    public MockingContext addPackageToExplore(Package packageRoot) {
        if (packageRoot != null && !packageRoot.getName().isEmpty()) {
            addPackageToExplore(packageRoot.getName());
        }
        return this;
    }

    public MockingContext addPackageToExplore(Class<?> theClass) {
        if (theClass.getPackage() != null) {
            return addPackageToExplore(theClass.getPackage());
        }
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public MockingContext mockContext() {
        trace("Mocking context started");
        DependencyInjector.discoverAndInjectDependencies(testClassInstance, packagesToExplore, injectableLookup, isDebugMode());
        trace("Mocking context completed");
        return this;
    }
}
