package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextAutowired;
import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextComponent;

@MockingContextComponent
public class InjectableTarget {

	@MockingContextAutowired
	public IInjectableClass injectableClass;
}
