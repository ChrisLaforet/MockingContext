package com.chrislaforetsoftware.mockingcontext.exception;

public class ClassInstantiationFailedException extends RuntimeException {
	public ClassInstantiationFailedException(String className, Exception ex) {
		super(String.format("Class %s failed to find instantiate", className), ex);
	}
}
