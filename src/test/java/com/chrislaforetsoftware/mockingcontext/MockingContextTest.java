package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextAutowired;
import com.chrislaforetsoftware.mockingcontext.exception.CannotInstantiateClassException;
import com.chrislaforetsoftware.mockingcontext.ioc.InjectableLookup;
import com.chrislaforetsoftware.mockingcontext.match.Injectable;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MockingContextTest {

    /** Testing ToDo List ****
     *
     *
     ***********/

    @Mock
    IAnnotationScanner mockScanner;

    AnnotatedClass annotatedClass = new AnnotatedClass();

    @MockingContextAutowired
    IInjectableClass testInjectable;

    @MockingContextAutowired
    DisconnectedClass disconnectedClass;

    @MockingContextAutowired
    IHandler<String, BigDecimal> stringBigDecimalIHandler;

    @MockingContextAutowired
    HandlerClassDependent dependentClass;

    @Test
    public void givenContext_whenGettingInstance_thenInstanceKnowsTestClass() {
        MockingContext instance = MockingContext.createInstance(this);
        assertEquals(MockingContextTest.class.getName(), instance.getTestClassName());
    }

    @Test
    public void givenContext_whenGettingInstance_thenInstanceInsertsPackageToExplore() {
        MockingContext instance = MockingContext.createInstance(this);
        assertFalse(instance.getPackagesToExplore().isEmpty());
        assertEquals(MockingContextTest.class.getPackage().getName(), instance.getPackagesToExplore().get(0));
    }

    @Test
    public void givenContext_whenGettingInstance_thenInstanceInsertsPackageTree() {
        MockingContext instance = MockingContext.createInstance(this);
        final List<String> packages = instance.getPackagesToExplore();
        assertEquals(9, packages.size());
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.match"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.annotation"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.annotation.impl"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.ioc"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.exception"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.ioc.impl"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.util"));
    }

    @Test
    public void givenContextInstance_whenAddingNewPackagePath_thenInstanceInsertsNewPackageTree() {
        MockingContext instance = MockingContext.createInstance(this);
        instance.addPackageToExplore("java.util");
        final List<String> packages = instance.getPackagesToExplore();
        assertTrue(packages.size() > 9);
    }

    @Test
    public void givenContextInstance_whenMockContextCalled_thenInjectablesContainsMockAnnotatedClasses() throws Exception {
        MockingContext instance = prepareFullMockingContext();

        final InjectableLookup injectables = instance.getInjectableLookup();
        Injectable match = injectables.find(IAnnotationScanner.class.getName()).orElseThrow(RuntimeException::new);
        assertEquals(mockScanner, match.getInstance());
    }

    private MockingContext prepareFullMockingContext() {
        MockingContext instance = MockingContext.createInstance(this, true);
        instance.addInjectable(this.annotatedClass);
        instance.mockContext();
        return instance;
    }

    @Test
    public void givenContextInstance_whenManualAddInjectable_thenInjectablesContainsManualInstance() {
        MockingContext instance = MockingContext.createInstance(this, true);
        final String injectable = "Hello world";
        instance.addInjectable(injectable);

        final InjectableLookup injectables = instance.getInjectableLookup();
        assertEquals(1, injectables.size());
        Injectable match = injectables.find(String.class.getName()).orElseThrow(RuntimeException::new);
        assertEquals(injectable, match.getInstance());
        assertEquals(injectable.getClass().getName(), match.getClassName());
    }

    @Test
    public void givenContextInstance_whenMockContextCalled_thenInjectablesContainMockingContextAutowiredClasses() {
        MockingContext instance = MockingContext.createInstance(this, true);
        instance.addInjectable(new AnnotatedClass());
        instance.mockContext();

        final InjectableLookup injectables = instance.getInjectableLookup();
        Injectable match = injectables.find(AnnotatedClass.class.getName()).orElseThrow(RuntimeException::new);
        assertNotEquals(annotatedClass, match.getInstance());
    }

    @Test
    public void givenContextInstanceWithDefaultConstructorAnnotatedClass_whenMockContextCalled_thenAnnotatedClassIsLocatedAndInstantiated() {
        MockingContext instance = prepareFullMockingContext();

        final InjectableLookup injectables = instance.getInjectableLookup();
        Optional<Injectable> match = injectables.find(AnotherAnnotatedClass.class.getName());
        assertTrue(match.isPresent());
    }

    @Test
    public void givenContextInstanceWithConstructorAutowiringAnnotatedClass_whenMockContextCalled_thenAnnotatedClassIsLocatedAndInstantiated() {
        MockingContext instance = prepareFullMockingContext();

        final InjectableLookup injectables = instance.getInjectableLookup();
        Optional<Injectable> match = injectables.find(YetAnotherAnnotatedClass.class.getName());
        assertTrue(match.isPresent());
        final YetAnotherAnnotatedClass injectedClass = (YetAnotherAnnotatedClass)match.get().getInstance();
        assertEquals(mockScanner, injectedClass.getMockScanner());
        assertEquals(annotatedClass, injectedClass.getAnnotatedClass());
    }

    @Ignore
    @Test(expected = CannotInstantiateClassException.class)
    public void givenContextInstanceWithMissingInjectables_whenMockContextCalled_thenThrowsCannotInstantiateClassException() {
        MockingContext instance = MockingContext.createInstance(this);
        instance.setTestClassInstance(this);
        instance.mockContext();
        final InjectableLookup injectables = instance.getInjectableLookup();
    }

    @Test
    public void givenContextInstanceWithInterface_whenMockContextCalled_thenInterfaceIsInjectedWithConcreteImplementer() {
        MockingContext instance = MockingContext.createInstance(this, true);
        instance.addInjectable(this.annotatedClass);
        instance.mockContext();

        final InjectableLookup injectables = instance.getInjectableLookup();
        Optional<Injectable> injectableClassMatch = injectables.find(InjectableClass.class.getName());
        assertTrue(injectableClassMatch.isPresent());
        final InjectableClass injectedClass = (InjectableClass)injectableClassMatch.get().getInstance();

        Optional<Injectable> injectableTargetMatch = injectables.find(InjectableTarget.class.getName());
        assertTrue(injectableTargetMatch.isPresent());
        final InjectableTarget injectableTarget = (InjectableTarget)injectableTargetMatch.get().getInstance();

        assertEquals(injectableTarget.injectableClass, injectedClass);
    }

    @Test
    public void givenContextInstanceWithInterface_whenMockContextCalled_thenBaseClassIsInjectedWithConcreteInstance() {
        MockingContext instance = MockingContext.createInstance(this, true);
        instance.addInjectable(this.annotatedClass);
        instance.mockContext();

        final InjectableLookup injectables = instance.getInjectableLookup();
        Optional<Injectable> injectableClassMatch = injectables.find(ExtendingClass.class.getName());
        assertTrue(injectableClassMatch.isPresent());
        final ExtendingClass extendingClass = (ExtendingClass)injectableClassMatch.get().getInstance();

        Optional<Injectable> injectableTargetMatch = injectables.find(ExtendedTarget.class.getName());
        assertTrue(injectableTargetMatch.isPresent());
        final ExtendedTarget extendedTarget = (ExtendedTarget)injectableTargetMatch.get().getInstance();

        assertEquals(extendedTarget.injectableClass, extendingClass);
    }

    @Test
    public void givenTestWithAnnotatedInjectableInterface_whenMockContextCalled_thenInjectableIsFulfilled() {
        MockingContext instance = MockingContext.createInstance(this, true);
        instance.addInjectable(this.annotatedClass);
        instance.mockContext();

        assertNotNull(testInjectable);
    }

    @Test
    public void givenTestWithAnnotatedInjectableClass_whenMockContextCalled_thenInjectableIsFulfilled() {
        MockingContext instance = MockingContext.createInstance(this, true);
        instance.addInjectable(this.annotatedClass);
        instance.mockContext();

        assertNotNull(disconnectedClass);
    }

    @Test
    public void givenTestWithAnnotatedInjectableGenericInterface_whenMockContextCalled_thenInjectableIsFulfilled() {
        MockingContext instance = MockingContext.createInstance(this, true);
        instance.addInjectable(this.annotatedClass);
        instance.mockContext();

        assertNotNull(stringBigDecimalIHandler);
    }

    @Test
    public void givenTestWithAnnotatedGenericInterfaceDependentClass_whenMockContextCalled_thenInjectableIsFulfilled() {
        MockingContext instance = MockingContext.createInstance(this, true);
        instance.addInjectable(this.annotatedClass);
        instance.mockContext();

        assertNotNull(dependentClass);
        assertEquals(stringBigDecimalIHandler, dependentClass.stringBigDecimalIHandler);
    }

}