package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextAutowired;
import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextComponent;

@MockingContextComponent
public class AnotherAnnotatedClass {

	@MockingContextAutowired
	public AnnotatedClass annotatedClass;
}
