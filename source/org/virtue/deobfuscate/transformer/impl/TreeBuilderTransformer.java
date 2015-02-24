package org.virtue.deobfuscate.transformer.impl;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.Type;
import org.virtue.deobfuscate.Injector;
import org.virtue.deobfuscate.transformer.Transformer;

public class TreeBuilderTransformer extends Transformer {
	
	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
	private final DefaultTreeModel model = new DefaultTreeModel(root);

	public TreeBuilderTransformer(Injector injector) {
		super(injector);
	}

	@Override
	public void deob(ClassGen classGen) {
		if (findNodeFor(classGen.getClassName(), root) != null)
			return;
		List<ClassGen> hierarchy = new ArrayList<ClassGen>();
		ClassGen cg = classGen;
		while (cg != null) {
			hierarchy.add(0, cg);
			cg = getClassForName(cg.getSuperclassName());
		}

		DefaultMutableTreeNode lastNode = root;
		for (ClassGen c : hierarchy) {
			DefaultMutableTreeNode node = findNodeFor(asString(c), lastNode);
			if (node == null) {
				node = new DefaultMutableTreeNode(asString(c));
				lastNode.add(node);
			}
			lastNode = node;
		}
	}

	private DefaultMutableTreeNode findNodeFor(String className, DefaultMutableTreeNode root) {
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> children = root.breadthFirstEnumeration();
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode child = children.nextElement();
			if (child.getUserObject().toString().equals(className))
				return child;
		}
		return null;
	}

	private String asString(ClassGen classGen) {
		StringBuilder buffer = new StringBuilder(classGen.getClassName());
		if (classGen.getSuperclassName() != null && !classGen.getSuperclassName().equals(Type.OBJECT.getClassName())
				&& getClassForName(classGen.getSuperclassName()) == null) {
			buffer.append(" extends ");
			buffer.append(classGen.getSuperclassName());
		}
		if (classGen.getInterfaceNames().length > 0) {
			buffer.append(" implements ");
			buffer.append(classGen.getInterfaceNames()[0]);
			for (int i = 1; i < classGen.getInterfaceNames().length; i++) {
				buffer.append(", ");
				buffer.append(classGen.getInterfaceNames()[i]);
			}
		}
		return buffer.toString();
	}

	private ClassGen getClassForName(String className) {
		return injector.getClasses().getByName(className);
	}

	@Override
	public void finish() {
		JFrame frame = new JFrame("Class Hierarchy");
		frame.setLayout(new BorderLayout());
		frame.add(new JScrollPane(new JTree(model)));
		frame.setSize(300, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
