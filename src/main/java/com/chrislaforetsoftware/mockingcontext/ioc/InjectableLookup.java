package com.chrislaforetsoftware.mockingcontext.ioc;

import com.chrislaforetsoftware.mockingcontext.match.Injectable;
import com.chrislaforetsoftware.mockingcontext.util.Traceable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class InjectableLookup extends Traceable {

	private final Map<String, Injectable> injectables = new HashMap<>();
	private final Set<String> packagesToExplore;

	public InjectableLookup(Set<String> packagesToExplore, boolean isDebugMode) {
		super(isDebugMode);
		this.packagesToExplore = packagesToExplore;
	}

	public void addInjectablesFor(Object instance) {
		this.addInjectablesFor(instance.getClass().getName(), instance);
	}

	public List<String> getInjectableClasses() {
		return new ArrayList<>(injectables.keySet());
	}

	public void addInjectablesFor(String className, Object instance) {
		final String cleanClassName = cleanClassName(className);
		this.add(cleanClassName, instance);

		// check we actually have the class type that we registering else get that also
		final String instanceName = cleanClassName(instance.getClass().getName());
		if (!instanceName.equals(cleanClassName)) {
			trace(String.format("    Registering class %s being implemented/extended", instanceName));
			add(instanceName, instance);
		}

		// find all interfaces this instance fulfills
		for (Class<?> theInterface : instance.getClass().getInterfaces()) {
			final String interfaceName = cleanClassName(theInterface.getName());
			if (!isClassInExplorablePackages(theInterface) || cleanClassName.equals(interfaceName)) {
				continue;
			}
			trace(String.format("    Registering interface implemented by instance %s", interfaceName));
			add(interfaceName, instance);
		}

		// find all extended super classes this instance fulfills
		final Class<?> superClass = instance.getClass().getSuperclass();
		if (isClassInExplorablePackages(superClass)) {
			final String superClassName = cleanClassName(superClass.getName());
			trace(String.format("    Registering superclass extending %s", superClassName));
			add(superClassName, instance);
		}
	}

	public static String cleanClassName(String className) {
		return className.contains("$") ? className.substring(0, className.indexOf("$")) : className;
	}

	private void add(String className, Object instance) {
		if (!injectables.containsKey(className)) {
			injectables.put(className, new Injectable(className, instance));
		}
	}

	private boolean isClassInExplorablePackages(Class<?> theClass) {
		return packagesToExplore.contains(theClass.getPackage().getName());
	}

	public Optional<Injectable> find(String className) {
		if (injectables.containsKey(className)) {
			return Optional.of(injectables.get(className));
		}
		return Optional.empty();
	}

	public int size() {
		return injectables.size();
	}
}
