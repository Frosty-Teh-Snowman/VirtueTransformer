package org.virtue.utility.refactor;

public abstract class Identifiable {

	protected boolean identified;

	public Identifiable identify() {
		identified = true;
		return this;
	}

	public boolean isIdentified() {
		return identified;
	}

	public void setIdentified(boolean identified) {
		this.identified = identified;
	}
}