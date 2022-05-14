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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(of = "theClass", callSuper = false)
public class ConstructionInjectorClassComponents extends Traceable implements ClassComponents {

	private final Class<?> theClass;
	private final Constructor<?> constructor;
	private final List<ParameterInjectionPoint> parameters;

	public ConstructionInjectorClassComponents(Class<?> theClass, Constructor<?> constructor, List<Class<?>> parameters, boolean isDebugMode) {
		super(isDebugMode);
		this.theClass = theClass;
		this.constructor = constructor;
		this.parameters = parameters.stream().map(ParameterInjectionPoint::new).collect(Collectors.toList());
	}

	public String getClassName() {
		return theClass.getName();
	}

	public Class<?> getTheClass() {
		return theClass;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public List<? extends InjectionPoint> getInjectionPoints() {
		return parameters;
	}

	public List<Class<?>> getClasses() {
		return parameters.stream().map(ParameterInjectionPoint::getTheClass).collect(Collectors.toList());
	}

	@Override
	public Injectable instantiateClassWith(InjectableLookup injectableLookup) {
		try {
			trace(String.format("Instantiating injectable %s with wiring constructor", getClassName()));
			final List<Object> constructorParameters = new ArrayList<>();
			for (ParameterInjectionPoint parameter : parameters) {
				Injectable injectable = injectableLookup.find(parameter.getTheClass().getName())
											.orElseThrow(() -> new InjectableNotFoundException(parameter.getTheClass().getName()));
				constructorParameters.add(injectable.getInstance());
				trace(String.format("  Injecting parameter with instance of %s", injectable.getInstance().getClass().getName()));
			}

			Object instance = constructor.newInstance(constructorParameters.toArray());
			return new Injectable(instance.getClass().getName(), instance);
		} catch (Exception ex) {
			throw new ClassInstantiationFailedException(theClass.getName(), ex);
		}
	}
}