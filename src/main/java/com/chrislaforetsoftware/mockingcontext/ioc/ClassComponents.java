package com.chrislaforetsoftware.mockingcontext.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public class ClassComponents {

	private final Class<?> theClass;
	private final Constructor<?> defaultConstructor;
	private final List<Field> dependencies;
	private Constructor<?> autowiredConstructor;

	public ClassComponents(Class<?> theClass, Constructor<?> defaultConstructor, List<Field> dependencies) {
		this.theClass = theClass;
		this.defaultConstructor = defaultConstructor;
		this.dependencies = dependencies;
	}

	public void setAutowiredConstructor(Constructor<?> constructor, List<Field> dependencies) {
		autowiredConstructor = constructor;
		this.dependencies.clear();
		this.dependencies.addAll(dependencies);
	}

	public String getClassName() {
		return theClass.getName();
	}

	public Class<?> getTheClass() {
		return theClass;
	}

	public Constructor<?> getDefaultConstructor() {
		return defaultConstructor;
	}

	public Constructor<?> getAutowiredConstructor() {
		return autowiredConstructor;
	}

	public List<Field> getDependencies() {
		return dependencies;
	}
}
