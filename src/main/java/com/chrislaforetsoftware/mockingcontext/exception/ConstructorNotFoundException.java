package com.chrislaforetsoftware.mockingcontext.exception;

public class ConstructorNotFoundException extends RuntimeException {
	public ConstructorNotFoundException(String className) {
		super(String.format("Class %s failed to find constructors", className));
	}
}
