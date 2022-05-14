package com.chrislaforetsoftware.mockingcontext.ioc;

import com.chrislaforetsoftware.mockingcontext.match.Injectable;

import java.lang.reflect.Constructor;
import java.util.List;

public interface ClassComponents {

	String getClassName();

	Class<?> getTheClass();

	Constructor<?> getConstructor();

	List<? extends InjectionPoint> getInjectionPoints();

	List<Class<?>> getClasses();

	Injectable instantiateClassWith(InjectableLookup injectableLookup);
}
