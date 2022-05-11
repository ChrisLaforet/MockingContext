package com.chrislaforetsoftware.mockingcontext.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public interface ClassComponents {

	String getClassName();

	Class<?> getTheClass();

	Constructor<?> getConstructor();

	List<InjectionPoint> getInjectionPoints();
}
