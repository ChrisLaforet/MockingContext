package com.chrislaforetsoftware.mockingcontext.annotation.impl;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextAutowired;
import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextComponent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class MockingContextAnnotationScanner implements IAnnotationScanner {

    public boolean isAnnotatedAsSource(Field field) {
        for (Annotation annotation : field.getAnnotations()) {
            if (annotation.annotationType().isAssignableFrom(MockingContextAutowired.class)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnnotatedAsTarget(Class<?> theClass) {
        for (Annotation annotation : theClass.getAnnotations()) {
            if (annotation.annotationType().isAssignableFrom(MockingContextComponent.class)) {
                return true;
            }
        }
        return false;
    }
}
