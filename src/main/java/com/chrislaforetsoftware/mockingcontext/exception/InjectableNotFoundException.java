package com.chrislaforetsoftware.mockingcontext.exception;

public class InjectableNotFoundException extends RuntimeException {
	public InjectableNotFoundException(String className) {
		super(String.format("Field class %s failed to find injectable", className));
	}
}
