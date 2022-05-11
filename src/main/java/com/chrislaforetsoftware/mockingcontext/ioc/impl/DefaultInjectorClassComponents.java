package com.chrislaforetsoftware.mockingcontext.ioc.impl;
import com.chrislaforetsoftware.mockingcontext.ioc.ClassComponents;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public class DefaultInjectorClassComponents implements ClassComponents {

	private final Class<?> theClass;
	private final Constructor<?> defaultConstructor;
	private final List<Field> dependencies;

	public DefaultInjectorClassComponents(Class<?> theClass, Constructor<?> defaultConstructor, List<Field> dependencies) {
		this.theClass = theClass;
		this.defaultConstructor = defaultConstructor;
		this.dependencies = dependencies;
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
		return dependencies;
	}
}