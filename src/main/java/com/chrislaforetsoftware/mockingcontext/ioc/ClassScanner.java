package com.chrislaforetsoftware.mockingcontext.ioc;

import com.chrislaforetsoftware.mockingcontext.annotation.IAnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {

	public static ClassComponents decomposeClass(Class<?> theClass, IAnnotationScanner springAnnotationScanner) throws NoSuchMethodException {
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
		final List<Field> autowiredDeclaredFields = new ArrayList<>();

		final Constructor<?> defaultConstructor = theClass.getConstructor();

		Constructor<?> autowiredConstructor = null;
		final Constructor<?>[] constructors = theClass.getConstructors();
		if (constructors.length == 1 && constructors[0] != defaultConstructor) {
			for (Constructor<?> constructor : constructors) {
				if (constructor == defaultConstructor) {
					continue;
				}

				final Annotation[] annotations = constructor.getAnnotations();

				if (constructor.getParameterTypes())
			}
		}


		defaultConstructor.setAccessible(true);
		return new ClassComponents(theClass, defaultConstructor, autowiredDeclaredFields);


		return dependencies;
	}
}
