package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextAutowired;
import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextComponent;

@MockingContextComponent
public class ExtendedTarget {
	@MockingContextAutowired
	public AbstractBaseClass injectableClass;
}
