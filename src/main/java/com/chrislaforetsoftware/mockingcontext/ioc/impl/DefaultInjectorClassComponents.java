package com.chrislaforetsoftware.mockingcontext.ioc.impl;
import com.chrislaforetsoftware.mockingcontext.ioc.ClassComponents;
import com.chrislaforetsoftware.mockingcontext.ioc.InjectionPoint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultInjectorClassComponents implements ClassComponents {

	private final Class<?> theClass;
	private final Constructor<?> defaultConstructor;
	private final List<InjectionPoint> injectableFields;

	public DefaultInjectorClassComponents(Class<?> theClass, Constructor<?> defaultConstructor, List<Field> dependencies) {
		this.theClass = theClass;
		this.defaultConstructor = defaultConstructor;
		this.injectableFields = dependencies.stream().map(FieldInjectionPoint::new).collect(Collectors.toList());
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

	public List<InjectionPoint> getInjectionPoints() {
		return injectableFields;
	}
}