package com.chrislaforetsoftware.mockingcontext.ioc;

import com.chrislaforetsoftware.mockingcontext.match.Injectable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InjectableLookup {

	private final Map<String, Injectable> injectables = new HashMap<>();

	public void add(Injectable injectable) {
		injectables.put(injectable.getClassName(), injectable);
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
