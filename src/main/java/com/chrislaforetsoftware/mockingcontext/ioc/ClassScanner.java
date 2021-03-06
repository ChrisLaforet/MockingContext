package com.chrislaforetsoftware.mockingcontext.ioc;

import com.chrislaforetsoftware.mockingcontext.annotation.impl.SpringAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextAutowired;
import com.chrislaforetsoftware.mockingcontext.annotation.mockingcontext.MockingContextComponent;
import com.chrislaforetsoftware.mockingcontext.exception.ConstructorNotFoundException;
import com.chrislaforetsoftware.mockingcontext.exception.InvalidComponentClassException;
import com.chrislaforetsoftware.mockingcontext.ioc.impl.ConstructionInjectorClassComponents;
import com.chrislaforetsoftware.mockingcontext.ioc.impl.DefaultInjectorClassComponents;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ClassScanner {

	public static Optional<ClassComponents> decomposeClass(Class<?> theClass, boolean isDebugMode) {
		try {
			return ClassScanner.decompose(theClass, isDebugMode);
		} catch (NoSuchMethodException ex) {
			throw new ConstructorNotFoundException(theClass.getName());
		}
	}

	private static Optional<ClassComponents> decompose(Class<?> theClass, boolean isDebugMode) throws NoSuchMethodException {

		// determine if this class is eligible
		if (!ClassScanner.isClassAnnotatedAsComponent(theClass)) {
			return Optional.empty();
		}

		final List<String> dependencies = new ArrayList<>();

		/* TODO: determine alternate forms of the dependency based on implementation like
		** this: class AddBookCommandHandler implements ICommandHandler<AddBookCommand, IBook>
		** can be known as AddBookCommandHandler OR
		** as ICommandHandler<xyz.commands.requests.AddBookCommand, xyz.business.entity.IBook>
		** and should be injectable as either.  Mockito does not detect the latter pattern.
		*/

		// find sole constructor with dependencies
		// find constructor with @Autowired
		// find @Autowired class dependencies

		// do we have autowired fields in this class?
		final Field [] fields = theClass.getDeclaredFields();
		final List<Field> autowiredDeclaredFields = loadAutowiredFields(fields);

		final Constructor<?> defaultConstructor = getDefaultConstructorFor(theClass);
		final Constructor<?>[] constructors = theClass.getConstructors();
		boolean onlyHasDefaultConstructor = false;
		if (defaultConstructor != null) {
			if (constructors.length == 1) {
				if (constructors[0].getParameterTypes().length == 0) {
					onlyHasDefaultConstructor = true;
				}
			} else if (constructors.length == 0) {
				onlyHasDefaultConstructor = true;
			}
		}

		if (onlyHasDefaultConstructor) {
			defaultConstructor.setAccessible(true);
			final ClassComponents components = new DefaultInjectorClassComponents(theClass, defaultConstructor, autowiredDeclaredFields, isDebugMode);

			return Optional.of(components);
		}

		if (constructors.length == 1 && constructors[0] != defaultConstructor) {
			return Optional.of(
					new ConstructionInjectorClassComponents(theClass,
						constructors[0],
						Arrays.asList(constructors[0].getParameterTypes()), isDebugMode));
		}

		throw new InvalidComponentClassException(theClass.getName(), "Invalid number of DI constructors");
	}

	private static Constructor<?> getDefaultConstructorFor(Class<?> theClass) {
		try {
			return theClass.getConstructor();
		} catch (Exception ex) {
			return null;
		}
	}

	private static List<Field> loadAutowiredFields(Field [] fields) {
		final List<Field> autowiredFields = new ArrayList<>();
		Arrays.asList(fields).forEach(field -> {
			if (ClassScanner.isAnnotatedAsAutowired(field)) {
				autowiredFields.add(field);
			}
		});

		return autowiredFields;
	}

	private static boolean isAnnotatedAsAutowired(Field field) {
		for (Annotation annotation : field.getAnnotations()) {
			if (annotation.annotationType().getName().equals(SpringAnnotationScanner.AUTOWIRED_ANNOTATION) ||
					annotation.annotationType().isAssignableFrom(MockingContextAutowired.class)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isClassAnnotatedAsComponent(Class<?> theClass) {
		for (Annotation annotation : theClass.getAnnotations()) {
			if (annotation.annotationType().getName().equals(SpringAnnotationScanner.COMPONENT_ANNOTATION) ||
					annotation.annotationType().getName().equals(SpringAnnotationScanner.REPOSITORY_ANNOTATION) ||
					annotation.annotationType().getName().equals(SpringAnnotationScanner.SERVICE_ANNOTATION) ||
					annotation.annotationType().isAssignableFrom(MockingContextComponent.class)) {
				return true;
			}
		}
		return false;
	}
}
