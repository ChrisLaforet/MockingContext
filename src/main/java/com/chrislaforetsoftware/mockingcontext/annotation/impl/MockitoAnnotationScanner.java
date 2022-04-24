package com.chrislaforetsoftware.mockingcontext.annotation.impl;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class MockitoAnnotationScanner implements IAnnotationScanner {

    private static final String MOCK_ANNOTATION = "org.mockito.Mock";

    private static final String INJECTMOCKS_ANNOTATION = "org.mockito.InjectMocks";

    public boolean isAnnotatedAsSource(Field field) {
        for (Annotation annotation : field.getAnnotations()) {
            if (annotation.annotationType().getName().equals(MOCK_ANNOTATION)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnnotatedAsTarget(Class<?> theClass) {
        for (Annotation annotation : theClass.getAnnotations()) {
            if (annotation.annotationType().getName().equals(INJECTMOCKS_ANNOTATION)) {
                return true;
            }
        }
        return false;
    }
}
