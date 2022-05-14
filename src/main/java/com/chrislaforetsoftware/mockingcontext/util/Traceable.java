package com.chrislaforetsoftware.mockingcontext.util;

public abstract class Traceable {

	private boolean isDebugTracingEnabled;

	protected Traceable(boolean isDebugMode) {
		this.isDebugTracingEnabled = isDebugMode;
	}

	public boolean isDebugMode() {
		return this.isDebugTracingEnabled;
	}

	protected void trace(String statement) {
		if (isDebugTracingEnabled) {
			System.out.println("MockingContext> " + statement);
		}
	}
}
