package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextAutowired;
import com.chrislaforetsoftware.mockingcontext.ioc.Injectable;
import com.chrislaforetsoftware.mockingcontext.ioc.InjectableLookup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MockingContextTest {

//    class TestInjection {
//
//
//    }
//
//    @InjectMocks
//    TestInjection testInjectionClass;

    /** Testing ToDo List ****
     *
     * -- annotate field with MockingContextAutowired should be found
     * annotate class with MockingContextComponent should be found
     * MockingContext createInstance throws RuntimeException
     *
     ***********/

    @Mock
    IAnnotationScanner mockScanner;

    @MockingContextAutowired
    AnnotatedClass annotatedClass;

    @Test
    public void givenContext_whenGettingInstance_thenInstanceKnowsTestClass() throws Exception {
        MockingContext instance = MockingContext.createInstance();
        assertEquals(MockingContextTest.class.getName(), instance.getTestClassName());
    }

    @Test
    public void givenContext_whenGettingInstance_thenInstanceInsertsPackageToExplore() throws Exception {
        MockingContext instance = MockingContext.createInstance();
        assertFalse(instance.getPackagesToExplore().isEmpty());
        assertEquals(MockingContextTest.class.getPackage().getName(), instance.getPackagesToExplore().get(0));
    }

    @Test
    public void givenContext_whenGettingInstance_thenInstanceInsertsPackageTree() throws Exception {
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
//    public void givenContextInstance_whenAddingNewPackagePath_thenInstanceInsertsNewPackageTree() throws Exception {
//        MockingContext instance = MockingContext.getInstance();
//        instance.addPackageToExplore("java.util");
//        final List<String> packages = instance.getPackagesToExplore();
//        assertEquals(4, packages.size());
//    }

    @Test
    public void givenContextInstance_whenMockContextCalled_thenInjectablesContainsMockAnnotatedClasses() throws Exception {
        MockingContext instance = MockingContext.createInstance();
        instance.setTestClassInstance(this);
        instance.mockContext();

        final InjectableLookup injectables = instance.getInjectableLookup();
        Injectable match = injectables.find(IAnnotationScanner.class.getName()).orElseThrow(RuntimeException::new);
        assertEquals(mockScanner, match.getInstance());
    }

    @Test
    public void givenContextInstance_whenManualAddInjectable_thenInjectablesContainsManualInstance() throws Exception {
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
    public void givenContextInstance_whenMockContextCalled_thenInjectablesContainMockingContextAutowiredClasses() throws Exception {
        MockingContext instance = MockingContext.createInstance();
        instance.setTestClassInstance(this);
        instance.mockContext();

        final InjectableLookup injectables = instance.getInjectableLookup();
        Injectable match = injectables.find(AnnotatedClass.class.getName()).orElseThrow(RuntimeException::new);
        assertEquals(annotatedClass, match.getInstance());
    }
}