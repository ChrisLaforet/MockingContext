package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.ioc.DIContext;

public class MockingContext {
    public static void mockContext() throws Exception {
        DIContext context = createContext();
    }

    private static DIContext createContext() throws Exception {
//        // snag the base package name from this main() class for scanning
//        String rootPackageName = null;
//        if (UnderstandingDI.class.getPackage() != null) {
//            rootPackageName = UnderstandingDI.class.getPackage().getName();
//        }
        return DIContext.createContextForPackage(null);
    }
}
