package com.chrislaforetsoftware.mockingcontext.ioc;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;
import com.chrislaforetsoftware.mockingcontext.annotation.impl.SpringAnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClassScanner {

	private static final SpringAnnotationScanner springAnnotationScanner = new SpringAnnotationScanner();

	public static Optional<ClassComponents> decomposeClass(Class<?> theClass, IAnnotationScanner springAnnotationScanner) throws NoSuchMethodException {

		// determine if this class is eligible
		if (!isClassAnnotatedAsComponent(theClass)) {
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
		final Field [] fields = theClass.getFields();
		final List<Field> autowiredDeclaredFields = loadAutowiredFields(fields);

		final List<Field> autowiredDeclaredFields = new ArrayList<>();

		final Constructor<?> defaultConstructor = theClass.getConstructor();
		final Constructor<?>[] constructors = theClass.getConstructors();
		boolean isOnlyDefault = false;
		if (constructors.length == 1) {
			if (constructors[0].getParameterTypes().length == 0) {
				isOnlyDefault = true;
			}
		} else if (constructors.length == 0) {
			isOnlyDefault = true;
		}

		if (isOnlyDefault) {
			if (!autowiredDeclaredFields.isEmpty()) {
				defaultConstructor.setAccessible(true);
				final ClassComponents components = new ClassComponents(theClass, defaultConstructor, autowiredDeclaredFields);

				return Optional.of(components);
			}
			return Optional.empty();
		}

		Constructor<?> autowiredConstructor = null;
		if (constructors.length == 1 && constructors[0] != defaultConstructor) {
			for (Constructor<?> constructor : constructors) {
				if (constructor == defaultConstructor) {
					continue;
				}

				final Annotation[] annotations = constructor.getAnnotations();

				if (constructor.getParameterTypes())
			}
		}


		return new ClassComponents(theClass, defaultConstructor, autowiredDeclaredFields);


		return dependencies;
	}

	private static List<Field> loadAutowiredFields(Field [] fields) {
		final List<Field> autowiredFields = new ArrayList<>();

		return autowiredFields;
	}

	private static boolean isClassAnnotatedAsComponent(Class<?> theClass) {
		return springAnnotationScanner.isAnnotatedAsTarget(theClass);
	}
}
