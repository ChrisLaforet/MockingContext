package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.ioc.Injectable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Mock
    IAnnotationScanner scannerA;

//    @Mock
//    IAnnotationScanner scannerB;

    @Test
    public void givenContext_whenGettingInstance_thenInstanceKnowsTestClass() throws Exception {
        MockingContext instance = MockingContext.getInstance();
        assertEquals(MockingContextTest.class.getName(), instance.getTestClassName());
    }

    @Test
    public void givenContext_whenGettingInstance_thenInstanceInsertsPackageToExplore() throws Exception {
        MockingContext instance = MockingContext.getInstance();
        assertFalse(instance.getPackagesToExplore().isEmpty());
        assertEquals(MockingContextTest.class.getPackage().getName(), instance.getPackagesToExplore().get(0));
    }

    @Test
    public void givenContext_whenGettingInstance_thenInstanceInsertsPackageTree() throws Exception {
        MockingContext instance = MockingContext.getInstance();
        final List<String> packages = instance.getPackagesToExplore();
        assertEquals(5, packages.size());
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.match"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.annotation"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.annotation.impl"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.ioc"));
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
        MockingContext instance = MockingContext.getInstance();
        instance.setTestClassInstance(this);
        instance.mockContext();

        final Map<String, Injectable> injectables = instance.getInjectables();
        assertEquals(1, injectables.size());
        Injectable match = injectables.values().stream().findFirst().orElseThrow(RuntimeException::new);
        assertEquals(scannerA, match.getInstance());
    }

    @Test
    public void givenContextInstance_whenManualAddInjectable_thenInjectablesContainsManualInstance() throws Exception {
        MockingContext instance = MockingContext.getInstance();
        final String injectable = "Hello world";
        instance.addInjectable(injectable);

        final Map<String, Injectable> injectables = instance.getInjectables();
        assertEquals(1, injectables.size());
        Injectable match = injectables.values().stream().findFirst().orElseThrow(RuntimeException::new);
        assertEquals(injectable, match.getInstance());
        assertEquals(injectable.getClass().getName(), match.getClassName());
    }
}