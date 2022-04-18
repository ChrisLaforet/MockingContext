package com.chrislaforetsoftware.mockingcontext.match;

import java.util.HashMap;
import java.util.Map;

public class Associations {

	private final Map<String, Association> classAssociations = new HashMap<>();

	public void addAssociation(Class<?> clazz, Object implementation) {
		final String classType = clazz.getTypeName();
		if (!classAssociations.containsKey(classType)) {
			classAssociations.put(classType, new Association(clazz, implementation));
		}
	}

	public Object getImplementationFor(String classType) {
		return classAssociations.get(classType);
	}

	public Object getImplementationFor(Class<?> clazz) {
		return getImplementationFor(clazz.getTypeName());
	}
}
