package com.chrislaforetsoftware.mockingcontext.match;


import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Association {
	private final Class<?> clazz;
	private final Object implementation;

	public Association(Class<?> clazz, Object implementation) {
		this.clazz = clazz;
		this.implementation = implementation;
	}

	public String getClassType() {
		return clazz.getTypeName();
	}
}
