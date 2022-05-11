package com.chrislaforetsoftware.mockingcontext;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextAutowired;
import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextComponent;

@MockingContextComponent
public class YetAnotherAnnotatedClass {

	IAnnotationScanner mockScanner;

	AnnotatedClass annotatedClass;

	public YetAnotherAnnotatedClass(IAnnotationScanner mockScanner, AnnotatedClass annotatedClass) {
		this.mockScanner = mockScanner;
		this.annotatedClass = annotatedClass;
	}

	public IAnnotationScanner getMockScanner() {
		return mockScanner;
	}

	public AnnotatedClass getAnnotatedClass() {
		return annotatedClass;
	}
}
