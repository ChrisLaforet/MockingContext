package com.chrislaforetsoftware.mockingcontext.ioc.impl;

import com.chrislaforetsoftware.mockingcontext.exception.ClassInstantiationFailedException;
import com.chrislaforetsoftware.mockingcontext.exception.InjectableNotFoundException;
import com.chrislaforetsoftware.mockingcontext.ioc.ClassComponents;
import com.chrislaforetsoftware.mockingcontext.match.Injectable;
import com.chrislaforetsoftware.mockingcontext.ioc.InjectableLookup;
import com.chrislaforetsoftware.mockingcontext.ioc.InjectionPoint;
import com.chrislaforetsoftware.mockingcontext.util.Traceable;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(of = "theClass", callSuper = false)
public class DefaultInjectorClassComponents extends Traceable implements ClassComponents {

	private final Class<?> theClass;
	private final Constructor<?> defaultConstructor;
	private final List<FieldInjectionPoint> injectableFields;

	public DefaultInjectorClassComponents(Class<?> theClass, Constructor<?> defaultConstructor, List<Field> dependencies, boolean isDebugMode) {
		super(isDebugMode);
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
	public Object instantiateClassWith(InjectableLookup injectableLookup) {
		try {
			trace(String.format("Instantiating injectable %s with default constructor", getClassName()));
			Object instance = defaultConstructor.newInstance();
			for (FieldInjectionPoint field : injectableFields) {
				final String fieldName = InjectableLookup.cleanClassName(field.getField().getType().getName());
				field.getField().setAccessible(true);
				Injectable injectable = injectableLookup.find(fieldName).orElseThrow(() -> new InjectableNotFoundException(fieldName));
				field.getField().set(instance, injectable.getInstance());
				trace(String.format("  Injecting field %s with instance of %s",
						fieldName,
						InjectableLookup.cleanClassName(injectable.getInstance().getClass().getName())));
			}
			return instance;
		} catch (Exception ex) {
			trace(String.format("Caught exception while instantiating injectable: %s", ex));
			throw new ClassInstantiationFailedException(theClass.getName(), ex);
		}
	}
}