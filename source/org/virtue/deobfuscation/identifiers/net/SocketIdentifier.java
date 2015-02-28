package org.virtue.deobfuscation.identifiers.net;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;

/**
 * @author : const_
 */
public class SocketIdentifier extends AbstractClassIdentifier {
    /**
	 * @param injector
	 */
	public SocketIdentifier(Injector injector) {
		super(injector);
		// TODO Auto-generated constructor stub
	}

	@Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasField("Ljava/net/Socket;", true, 1).hasField("Ljava/io/OutputStream;",true, 1)
                .hasField("Ljava/io/InputStream;", true, 1).implement("java/lang/Runnable").firstOnBranch(0);
    }
}