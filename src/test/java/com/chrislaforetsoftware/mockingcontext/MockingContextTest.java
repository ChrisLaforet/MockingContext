package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.exception.CannotInstantiateClassException;
import com.chrislaforetsoftware.mockingcontext.ioc.Injectable;
import com.chrislaforetsoftware.mockingcontext.ioc.InjectableLookup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MockingContextTest {

    /** Testing ToDo List ****
     *
     * --annotate class with MockingContextComponent should be found
     * -- Find a mocking class with constructor autowiring
     * -- Create instance of mocked class with constructor autowiring
     * -- Circle around until all mocked classes are instantiated
     * -- Throw exception if all mocked classes cannot be instantiated
     *
     ***********/

    @Mock
    IAnnotationScanner mockScanner;

    AnnotatedClass annotatedClass = new AnnotatedClass();

    @Test
    public void givenContext_whenGettingInstance_thenInstanceKnowsTestClass() {
        MockingContext instance = MockingContext.createInstance();
        assertEquals(MockingContextTest.class.getName(), instance.getTestClassName());
    }

    @Test
    public void givenContext_whenGettingInstance_thenInstanceInsertsPackageToExplore() {
        MockingContext instance = MockingContext.createInstance();
        assertFalse(instance.getPackagesToExplore().isEmpty());
        assertEquals(MockingContextTest.class.getPackage().getName(), instance.getPackagesToExplore().get(0));
    }

    @Test
    public void givenContext_whenGettingInstance_thenInstanceInsertsPackageTree() {
        MockingContext instance = MockingContext.createInstance();
        final List<String> packages = instance.getPackagesToExplore();
        assertEquals(8, packages.size());
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.match"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.annotation"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.annotation.impl"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.ioc"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.exception"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.ioc.impl"));
    }

//    @Test
//    public void givenContextInstance_whenAddingNewPackagePath_thenInstanceInsertsNewPackageTree() {
//        MockingContext instance = MockingContext.getInstance();
//        instance.addPackageToExplore("java.util");
//        final List<String> packages = instance.getPackagesToExplore();
//        assertEquals(4, packages.size());
//    }

    @Test
    public void givenContextInstance_whenMockContextCalled_thenInjectablesContainsMockAnnotatedClasses() throws Exception {
        MockingContext instance = prepareFullMockingContext();

        final InjectableLookup injectables = instance.getInjectableLookup();
        Injectable match = injectables.find(IAnnotationScanner.class.getName()).orElseThrow(RuntimeException::new);
        assertEquals(mockScanner, match.getInstance());
    }

    private MockingContext prepareFullMockingContext() {
        MockingContext instance = MockingContext.createInstance();
        instance.setTestClassInstance(this);
        instance.addInjectable(this.annotatedClass);
        instance.mockContext();
        return instance;
    }

    @Test
    public void givenContextInstance_whenManualAddInjectable_thenInjectablesContainsManualInstance() {
        MockingContext instance = MockingContext.createInstance();
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
        MockingContext instance = prepareFullMockingContext();

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

    @Test(expected = CannotInstantiateClassException.class)
    public void givenContextInstanceWithMissingInjectables_whenMockContextCalled_thenThrowsCannotInstantiateClassException() {
        MockingContext instance = MockingContext.createInstance();
        instance.setTestClassInstance(this);
        instance.mockContext();
        final InjectableLookup injectables = instance.getInjectableLookup();
    }
}