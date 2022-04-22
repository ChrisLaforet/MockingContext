package com.chrislaforetsoftware.mockingcontext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MockingContextTest {

    //@Mock

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
        assertEquals(4, packages.size());
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.match"));
        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.annotation"));

        assertTrue(packages.contains("com.chrislaforetsoftware.mockingcontext.ioc"));
    }

//    @Test
//    public void givenContextInstance_whenAddingNewPackagePath_thenInstanceInsertsNewPackageTree() throws Exception {
//        MockingContext instance = MockingContext.getInstance();
//        instance.addPackageToExplore("java.util");
//        final List<String> packages = instance.getPackagesToExplore();
//        assertEquals(4, packages.size());
//    }
}