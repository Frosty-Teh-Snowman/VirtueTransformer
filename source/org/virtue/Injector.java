/*package org.virtue;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.deobfuscate.AbstractClassIdentifier;
import org.virtue.deobfuscate.Transformer;
import org.virtue.deobfuscate.identifiers.ClientIdentifier;
import org.virtue.deobfuscate.identifiers.FacadeIdentifier;
import org.virtue.deobfuscate.identifiers.cache.CacheIdentifier;
import org.virtue.deobfuscate.identifiers.cache.ModelIdentifier;
import org.virtue.deobfuscate.identifiers.cache.WidgetIdentifier;
import org.virtue.deobfuscate.identifiers.cache.WidgetNodeIdentifier;
import org.virtue.deobfuscate.identifiers.cache.definition.ItemDefinitionIdentifier;
import org.virtue.deobfuscate.identifiers.cache.definition.NpcDefinitionIdentifier;
import org.virtue.deobfuscate.identifiers.cache.definition.ObjectDefinitionIdentifier;
import org.virtue.deobfuscate.identifiers.cache.definition.PlayerDefinitionIdentifier;
import org.virtue.deobfuscate.identifiers.input.KeyboardIdentifier;
import org.virtue.deobfuscate.identifiers.input.MouseIdentifier;
import org.virtue.deobfuscate.identifiers.net.FileOnDiskIdentifier;
import org.virtue.deobfuscate.identifiers.net.SocketIdentifier;
import org.virtue.deobfuscate.identifiers.net.StreamIdentifier;
import org.virtue.deobfuscate.identifiers.node.BagIdentifier;
import org.virtue.deobfuscate.identifiers.node.CacheableNodeIdentifier;
import org.virtue.deobfuscate.identifiers.node.DequeIdentifier;
import org.virtue.deobfuscate.identifiers.node.NodeIdentifier;
import org.virtue.deobfuscate.identifiers.node.QueueIdentifier;
import org.virtue.deobfuscate.identifiers.renderable.ActorIdentifier;
import org.virtue.deobfuscate.identifiers.renderable.LootIdentifier;
import org.virtue.deobfuscate.identifiers.renderable.NpcIdentifier;
import org.virtue.deobfuscate.identifiers.renderable.PlayerIdentifier;
import org.virtue.deobfuscate.identifiers.renderable.RenderableIdentifier;
import org.virtue.deobfuscate.identifiers.scene.CollisionMapIdentifier;
import org.virtue.deobfuscate.identifiers.scene.RegionIdentifier;
import org.virtue.deobfuscate.identifiers.scene.TileIdentifier;
import org.virtue.deobfuscate.identifiers.scene.object.BoundaryObjectIdentifier;
import org.virtue.deobfuscate.identifiers.scene.object.GameObjectIdentifier;
import org.virtue.deobfuscate.identifiers.scene.object.GroundLayerIdentifier;
import org.virtue.deobfuscate.identifiers.scene.object.GroundObjectIdentifier;
import org.virtue.deobfuscate.identifiers.scene.object.WallObjectIdentifier;
import org.virtue.deobfuscate.transformers.ArithmeticStatementOrderTransform;
import org.virtue.deobfuscate.transformers.IllegalStateExceptionRemovalTransform;
import org.virtue.deobfuscate.transformers.UnusedClassRemovalTransform;

public class Injector {
	

	*//**
	 * The {@link Logger} instance
	 *//*
	private static Logger logger = LoggerFactory.getLogger(Injector.class);
	
	public List<ClassElement> classes;
	private final List<Transformer> transformers;
	private final List<AbstractClassIdentifier> identifiers;
	
	public Injector() {
		classes = new ArrayList<ClassElement>();
		transformers = new LinkedList<Transformer>();
		identifiers = new LinkedList<AbstractClassIdentifier>();
	}

	public void initialization(String path) {
		if (!classes.isEmpty())
			classes = new ArrayList<ClassElement>();
		
		try {
			classes.add(new ClassGen(new ClassParser("./build/classes/org/virtue/TestData.class").parse()));
		} catch (ClassFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try (JarFile jar = new JarFile(path)) {
            Enumeration<JarEntry> enumeration = jar.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry next = enumeration.nextElement();
                if (next.getName().endsWith(".class")) {
                    ClassReader reader = new ClassReader(jar.getInputStream(next));
                    ClassNode node = new ClassNode();
                    reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    ClassElement element = new ClassElement(node, reader);
                    classes.add(element);
                }
            }
            jar.close();
		} catch (RuntimeException exception) {
			throw exception;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
		logger.info("Loaded " + classes.size() + " Class(es)");
	}

	public void registerTransformer() {
		transformers.add(new IllegalStateExceptionRemovalTransform());
		//transformers.add(new UnusedFieldRemovalTransform());
		transformers.add(new UnusedClassRemovalTransform());
		//transformers.add(new UnusedMethodRemovalTransform());
		transformers.add(new ArithmeticStatementOrderTransform());
		//transformers.add(new OpaquePredicateRemovalTransform());
		//transformers.add(new ControlFlowTransform());
	}

	public void registerIdentifiers() {
        identifiers.add(new ClientIdentifier());
        identifiers.add(new NodeIdentifier());
        identifiers.add(new CacheableNodeIdentifier());
        identifiers.add(new DequeIdentifier());
        identifiers.add(new QueueIdentifier());
        identifiers.add(new BagIdentifier());
        identifiers.add(new CacheIdentifier());
        identifiers.add(new StreamIdentifier());
        identifiers.add(new SocketIdentifier());
        identifiers.add(new FileOnDiskIdentifier());
        identifiers.add(new PlayerIdentifier());
        identifiers.add(new NpcIdentifier());
        identifiers.add(new ActorIdentifier());
        identifiers.add(new RenderableIdentifier());
        identifiers.add(new ModelIdentifier());
        identifiers.add(new LootIdentifier());
        identifiers.add(new WidgetIdentifier());
        identifiers.add(new NpcDefinitionIdentifier());
        identifiers.add(new PlayerDefinitionIdentifier());
        identifiers.add(new ItemDefinitionIdentifier());
        identifiers.add(new ObjectDefinitionIdentifier());
        identifiers.add(new RegionIdentifier());
        identifiers.add(new TileIdentifier());
        identifiers.add(new GameObjectIdentifier());
        identifiers.add(new WallObjectIdentifier());
        identifiers.add(new BoundaryObjectIdentifier());
        identifiers.add(new GroundObjectIdentifier());
        identifiers.add(new GroundLayerIdentifier());
        identifiers.add(new CollisionMapIdentifier());
        identifiers.add(new LootIdentifier());
        identifiers.add(new WidgetNodeIdentifier());
        identifiers.add(new MouseIdentifier());
        identifiers.add(new KeyboardIdentifier());
        identifiers.add(new FacadeIdentifier());
	}
	
	public void transform() throws IOException {
        for (Transformer transform : transformers) {
            transform.execute(classes);
            System.out.println(transform.result());
        }
		
		saveToJar();
	}
	
	public void identify() throws IOException {
		for (AbstractClassIdentifier ident : identifiers) {
			System.out.println(ident.getClass().getName());
            ident.run();
        }
        for (AbstractClassIdentifier ident : identifiers) {
            ident.runFields();
        }
        int found = 0;
        for(AbstractClassIdentifier ident : identifiers) {
            if(!ident.broken()) {
                found++;
            }
            System.out.println(ident.format());
        }
        System.out.println("\tNamed " + found + " out of " + identifiers.size() + " lemons.");
	}

	private void saveToJar() throws IOException {
        try (JarOutputStream output = new JarOutputStream(new FileOutputStream(VirtueTransformer.getInstance().getDirectory() + (VirtueTransformer.getInstance().getTransformMode().equals(TransformMode.OBFUSCATE) ? "obfuscated.jar" : "deobfuscated.jar")))) {
            for (ClassElement element : classes) {
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                output.putNextEntry(new JarEntry(element.name().replaceAll("\\.", "/") + ".class"));
                element.node().accept(writer);
                output.write(writer.toByteArray());
                output.closeEntry();
            }
        }
	}
	
    @SuppressWarnings("unchecked")
	public <T extends AbstractClassIdentifier>  T get(Class<T> clazz) {
        for(AbstractClassIdentifier identifier : identifiers) {
            if(identifier.getClass().equals(clazz)) {
                return (T) identifier;
            }
        }
        return null;
    }

    public ClassElement get(String name) {
        for (ClassElement element : classes) {
            if (element.name().equals(name)) {
                return element;
            }
        }
        return null;
    }

	public List<ClassElement> getClasses() {
		return classes;
	}
    
}*/