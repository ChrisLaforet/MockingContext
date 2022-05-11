package com.chrislaforetsoftware.mockingcontext.exception;

public class ReflectionFailedException extends RuntimeException{
	public ReflectionFailedException(Exception ex) {
		super(ex.getMessage(), ex);
	}
}
