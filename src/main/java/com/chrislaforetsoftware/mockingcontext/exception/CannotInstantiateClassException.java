package com.chrislaforetsoftware.mockingcontext.exception;

public class CannotInstantiateClassException extends RuntimeException {
	public CannotInstantiateClassException(String classNames) {
		super(String.format("The following classes cannot be instantiated due to missing injectables: %s", classNames));
	}
}
