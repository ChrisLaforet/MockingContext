package com.chrislaforetsoftware.mockingcontext.ioc;

import com.chrislaforetsoftware.mockingcontext.util.Traceable;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ClassResolver extends Traceable {

	private final Set<String> packagesToExplore;

	private Map<String, Class<?>> aliasLookup = new HashMap<>();

	public ClassResolver(Set<String> packagesToExplore, boolean isDebugMode) {
		super(isDebugMode);
		this.packagesToExplore = packagesToExplore;
	}

	public void add(Class<?> theClass) {
		if (Modifier.isAbstract(theClass.getModifiers())) {
			return;
		}
		final String instanceName = InjectableLookup.cleanClassName(theClass.getName());
		trace(String.format("    Registering class resolver for %s", instanceName));
		aliasLookup.put(instanceName, theClass);

		// find all interfaces this instance fulfills
		for (Class<?> theInterface : theClass.getClass().getInterfaces()) {
			final String interfaceName = InjectableLookup.cleanClassName(theInterface.getName());
			if (!isClassInExplorablePackages(theInterface)) {
				continue;
			}
			trace(String.format("    Registering class resolver interface %s", interfaceName));
			aliasLookup.put(interfaceName, theClass);
		}

		final Class<?> superClass = theClass.getSuperclass();
		if (isClassInExplorablePackages(superClass)) {
			final String superClassName = InjectableLookup.cleanClassName(superClass.getName());
			trace(String.format("    Registering class resolver superclass %s", superClassName));
			aliasLookup.put(superClassName, theClass);
		}
	}

	public Optional<Class<?>> resolve(String className) {
		if (aliasLookup.containsKey(className)) {
			return Optional.of(aliasLookup.get(className));
		}
		return Optional.empty();
	}

	private boolean isClassInExplorablePackages(Class<?> theClass) {
		return packagesToExplore.contains(theClass.getPackage().getName());
	}
}
