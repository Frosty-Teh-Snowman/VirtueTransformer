package org.virtue.obfuscate.transformer;

import org.apache.bcel.generic.ClassGen;
import org.virtue.Injector;

public abstract class Transformer {
	
	protected final Injector injector;

	public Transformer(Injector injector) {
		this.injector = injector;
	}

	public abstract void transform(ClassGen classGen);

	public abstract void finish();
	
	protected Injector getInjector() {
		return injector;
	}
}
