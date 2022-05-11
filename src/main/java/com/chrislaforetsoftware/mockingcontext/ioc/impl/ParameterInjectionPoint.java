package com.chrislaforetsoftware.mockingcontext.ioc.impl;

import com.chrislaforetsoftware.mockingcontext.ioc.InjectionPoint;

public class ParameterInjectionPoint implements InjectionPoint {
	private Class<?> theClass;

	public ParameterInjectionPoint(Class<?> theClass) {
		this.theClass = theClass;
	}

	public Class<?> getTheClass() {
		return theClass;
	}
}
