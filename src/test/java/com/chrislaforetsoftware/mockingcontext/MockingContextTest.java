package com.chrislaforetsoftware.mockingcontext;

import org.junit.Test;

import static org.junit.Assert.*;

public class MockingContextTest {

    @Test
    public void givenContext_whenGettingInstance_thenInstanceKnowsTestClass() throws ClassNotFoundException {
        MockingContext instance = MockingContext.getInstance();
        assertEquals(MockingContextTest.class.getName(), instance.getTestClassName());
    }

    @Test
    public void givenContext_whenGettingInstance_thenInstanceInsertsPackageToExplore() throws ClassNotFoundException {
        MockingContext instance = MockingContext.getInstance();
        assertFalse(instance.getPackagesToExplore().isEmpty());
        assertEquals(MockingContextTest.class.getPackage().getName(), instance.getPackagesToExplore().get(0));
    }


}