package com.chrislaforetsoftware.mockingcontext.annotation.impl;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class SpringAnnotationScanner implements IAnnotationScanner {

	public static final String COMPONENT_ANNOTATION = "org.springframework.stereotype.Component";
	public static final String REPOSITORY_ANNOTATION = "org.springframework.stereotype.Repository";
	public static final String SERVICE_ANNOTATION = "org.springframework.stereotype.Service";

	public static final String AUTOWIRED_ANNOTATION = "org.springframework.beans.factory.annotation.Autowired";

	public boolean isAnnotatedAsSource(Field field) {
		for (Annotation annotation : field.getAnnotations()) {
			if (annotation.annotationType().getName().equals(COMPONENT_ANNOTATION) ||
					annotation.annotationType().getName().equals(REPOSITORY_ANNOTATION) ||
					annotation.annotationType().getName().equals(SERVICE_ANNOTATION)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAnnotatedAsTarget(Class<?> theClass) {
		for (Annotation annotation : theClass.getAnnotations()) {
			if (annotation.annotationType().getName().equals(AUTOWIRED_ANNOTATION)) {
				return true;
			}
		}
		return false;
	}
}
