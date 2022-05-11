package com.chrislaforetsoftware.mockingcontext.exception;

public class InvalidComponentClassException extends IllegalStateException {
	public InvalidComponentClassException(String className, String reason) {
		super(String.format("Class %s fails injection because %s", className, reason));
	}
}
