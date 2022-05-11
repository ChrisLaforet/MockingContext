package com.chrislaforetsoftware.mockingcontext.ioc.impl;

import com.chrislaforetsoftware.mockingcontext.exception.ClassInstantiationFailedException;
import com.chrislaforetsoftware.mockingcontext.exception.InjectableNotFoundException;
import com.chrislaforetsoftware.mockingcontext.ioc.ClassComponents;
import com.chrislaforetsoftware.mockingcontext.ioc.Injectable;
import com.chrislaforetsoftware.mockingcontext.ioc.InjectableLookup;
import com.chrislaforetsoftware.mockingcontext.ioc.InjectionPoint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultInjectorClassComponents implements ClassComponents {

	private final Class<?> theClass;
	private final Constructor<?> defaultConstructor;
	private final List<FieldInjectionPoint> injectableFields;

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

	public List<? extends InjectionPoint> getInjectionPoints() {
		return injectableFields;
	}

	@Override
	public List<Class<?>> getClasses() {
		return injectableFields.stream().map(FieldInjectionPoint::getTheClass).collect(Collectors.toList());
	}

	@Override
	public Injectable instantiateClassWith(InjectableLookup injectableLookup) {
		try {
			Object instance = defaultConstructor.newInstance();
			for (FieldInjectionPoint field : injectableFields) {
				field.getField().setAccessible(true);
				Injectable injectable = injectableLookup.find(field.getField().getType().getName()).orElseThrow(() -> new InjectableNotFoundException(field.getTheClass().getName()));
				field.getField().set(instance, injectable.getInstance());
			}
			return new Injectable(instance.getClass().getName(), instance);
		} catch (Exception ex) {
			throw new ClassInstantiationFailedException(theClass.getName(), ex);
		}
	}
}