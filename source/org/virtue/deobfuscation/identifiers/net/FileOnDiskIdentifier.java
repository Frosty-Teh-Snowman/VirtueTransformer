package org.virtue.deobfuscation.identifiers.net;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;

/**
 * @author : const_
 */
public class FileOnDiskIdentifier extends AbstractClassIdentifier {
    /**
	 * @param injector
	 */
	public FileOnDiskIdentifier(Injector injector) {
		super(injector);
		// TODO Auto-generated constructor stub
	}

	@Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasField("Ljava/io/RandomAccessFile;", true, 1).hasField("J", true, 2).fieldCount(3).first();
    }
}
