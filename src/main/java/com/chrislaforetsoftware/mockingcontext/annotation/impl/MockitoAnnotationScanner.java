package com.chrislaforetsoftware.mockingcontext.annotation.impl;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class MockitoAnnotationScanner implements IAnnotationScanner {

    private static final String MOCK_ANNOTATION = "org.mockito.Mock";

    public boolean isAnnotatedAsSource(Field field) {
        for (Annotation annotation : field.getAnnotations()) {
            String x = annotation.annotationType().getName();
            if (annotation.annotationType().getName().equals(MOCK_ANNOTATION)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnnotatedAsTarget(Class<?> theClass) {
//        for (Annotation annotation : theClass.getAnnotations()) {
//            if (annotation.getClass().getName().equals(MOCK_ANNOTATION)) {
//                return true;
//            }
//        }
        return false;
    }
}
