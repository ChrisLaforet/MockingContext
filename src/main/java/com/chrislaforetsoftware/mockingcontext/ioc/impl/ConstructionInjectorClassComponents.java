package com.chrislaforetsoftware.mockingcontext.ioc.impl;
import com.chrislaforetsoftware.mockingcontext.ioc.ClassComponents;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ConstructionInjectorClassComponents implements ClassComponents {

	private final Class<?> theClass;
	private final Constructor<?> defaultConstructor;
	private final List<Class<?>> parameters;

	public ConstructionInjectorClassComponents(Class<?> theClass, Constructor<?> defaultConstructor, List<Class<?>> parameters) {
		this.theClass = theClass;
		this.defaultConstructor = defaultConstructor;
		this.parameters = parameters;
	}

	public String getClassName() {
		return theClass.getName();
	}

	public Class<?> getTheClass() {
		return theClass;
	}

	public Constructor<?> getConstructor() {
		return defaultConstructor;
	}

	public List<Field> getDependencies() {
		return new ArrayList<>();
		// TODO: change the behavior of getting dependencies
	}
}