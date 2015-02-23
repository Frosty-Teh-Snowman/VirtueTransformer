package org.virtue.deobfuscate.deobbers;

import org.apache.bcel.generic.ClassGen;
import org.virtue.deobfuscate.Injector;

public abstract class Deobber {
	protected final Injector injector;

	public Deobber(Injector injector) {
		this.injector = injector;
	}

	public abstract void deob(ClassGen classGen);

	public abstract void finish();
}
