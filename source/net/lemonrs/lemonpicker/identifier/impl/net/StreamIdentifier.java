package net.lemonrs.lemonpicker.identifier.impl.net;

import java.util.List;

import net.lemonrs.lemonpicker.bytecode.element.ClassElement;
import net.lemonrs.lemonpicker.bytecode.element.FieldElement;
import net.lemonrs.lemonpicker.identifier.AbstractClassIdentifier;
import net.lemonrs.lemonpicker.identifier.AbstractFieldIdentifier;
import net.lemonrs.lemonpicker.query.impl.ClassQuery;
import net.lemonrs.lemonpicker.query.impl.FieldQuery;

import org.objectweb.asm.Opcodes;

/**
 * @author : const_
 */
public class StreamIdentifier extends AbstractClassIdentifier {
	
	public StreamIdentifier() {
		add(new Payload());
		add(new Pointer());
	}

    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        return query.branchSize(3).hasField("[B", true, 1).hasField("I", true, 1).fieldCount(2).firstOnBranch(1);
    }
	
	private class Pointer extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            FieldQuery query = new FieldQuery(StreamIdentifier.this.identified());
            List<FieldElement> fields = query.desc("I").member().all();
			for (FieldElement field : fields) {
				if (field.access() == Opcodes.ACC_PUBLIC)
					return field;
			}
			return null;
        }
    }
	
	private class Payload extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            FieldQuery query = new FieldQuery(StreamIdentifier.this.identified());
            return query.desc("[B").member().first();
        }
    }
}
