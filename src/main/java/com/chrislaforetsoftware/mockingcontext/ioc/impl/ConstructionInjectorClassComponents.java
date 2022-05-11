package com.chrislaforetsoftware.mockingcontext.ioc.impl;

import com.chrislaforetsoftware.mockingcontext.ioc.ClassComponents;
import com.chrislaforetsoftware.mockingcontext.ioc.Injectable;
import com.chrislaforetsoftware.mockingcontext.ioc.InjectableLookup;
import com.chrislaforetsoftware.mockingcontext.ioc.InjectionPoint;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Collectors;

public class ConstructionInjectorClassComponents implements ClassComponents {

	private final Class<?> theClass;
	private final Constructor<?> defaultConstructor;
	private final List<ParameterInjectionPoint> parameters;

	public ConstructionInjectorClassComponents(Class<?> theClass, Constructor<?> defaultConstructor, List<Class<?>> parameters) {
		this.theClass = theClass;
		this.defaultConstructor = defaultConstructor;
		this.parameters = parameters.stream().map(ParameterInjectionPoint::new).collect(Collectors.toList());
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

	public List<? extends InjectionPoint> getInjectionPoints() {
		return parameters;
	}

	public List<Class<?>> getClasses() {
		return parameters.stream().map(ParameterInjectionPoint::getTheClass).collect(Collectors.toList());
	}

	@Override
	public Injectable instantiateClassWith(InjectableLookup injectableLookup) {
		throw new NotImplementedException();
	}
}