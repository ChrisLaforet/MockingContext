package com.chrislaforetsoftware.mockingcontext.ioc.impl;

import com.chrislaforetsoftware.mockingcontext.ioc.InjectionPoint;

import java.lang.reflect.Field;

public class FieldInjectionPoint implements InjectionPoint {

	private Field field;

	public FieldInjectionPoint(Field field) {
		this.field = field;
	}

	public Class<?> getTheClass() {
		return field.getType();
	}

	public Field getField() {
		return field;
	}
}
