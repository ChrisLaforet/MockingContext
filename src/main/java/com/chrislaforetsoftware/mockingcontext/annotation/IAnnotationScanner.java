package com.chrislaforetsoftware.mockingcontext.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface IAnnotationScanner {
    boolean isAnnotatedAsSource(Field field);

    boolean isAnnotatedAsTarget(Class<?> theClass);
}
