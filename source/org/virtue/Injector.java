package org.virtue;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.graph.hierarchy.HierarchyTree;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.Transformer;
import org.virtue.deobfuscation.indentifiers.ClientIdentifier;
import org.virtue.deobfuscation.indentifiers.FacadeIdentifier;
import org.virtue.deobfuscation.indentifiers.cache.CacheIdentifier;
import org.virtue.deobfuscation.indentifiers.cache.ModelIdentifier;
import org.virtue.deobfuscation.indentifiers.cache.WidgetIdentifier;
import org.virtue.deobfuscation.indentifiers.cache.WidgetNodeIdentifier;
import org.virtue.deobfuscation.indentifiers.cache.definition.ItemDefinitionIdentifier;
import org.virtue.deobfuscation.indentifiers.cache.definition.NpcDefinitionIdentifier;
import org.virtue.deobfuscation.indentifiers.cache.definition.ObjectDefinitionIdentifier;
import org.virtue.deobfuscation.indentifiers.cache.definition.PlayerDefinitionIdentifier;
import org.virtue.deobfuscation.indentifiers.input.KeyboardIdentifier;
import org.virtue.deobfuscation.indentifiers.input.MouseIdentifier;
import org.virtue.deobfuscation.indentifiers.net.FileOnDiskIdentifier;
import org.virtue.deobfuscation.indentifiers.net.SocketIdentifier;
import org.virtue.deobfuscation.indentifiers.net.StreamIdentifier;
import org.virtue.deobfuscation.indentifiers.node.BagIdentifier;
import org.virtue.deobfuscation.indentifiers.node.CacheableNodeIdentifier;
import org.virtue.deobfuscation.indentifiers.node.DequeIdentifier;
import org.virtue.deobfuscation.indentifiers.node.NodeIdentifier;
import org.virtue.deobfuscation.indentifiers.node.QueueIdentifier;
import org.virtue.deobfuscation.indentifiers.renderable.ActorIdentifier;
import org.virtue.deobfuscation.indentifiers.renderable.LootIdentifier;
import org.virtue.deobfuscation.indentifiers.renderable.NpcIdentifier;
import org.virtue.deobfuscation.indentifiers.renderable.PlayerIdentifier;
import org.virtue.deobfuscation.indentifiers.renderable.RenderableIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.CollisionMapIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.RegionIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.TileIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.object.BoundaryObjectIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.object.GameObjectIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.object.GroundLayerIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.object.GroundObjectIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.object.WallObjectIdentifier;
import org.virtue.deobfuscation.transformers.ArithmeticStatementOrderTransformer;
import org.virtue.deobfuscation.transformers.IllegalStateExceptionRemovalTransformer;
import org.virtue.deobfuscation.transformers.OpaquePredicateRemovalTransformer;
import org.virtue.deobfuscation.transformers.UnusedClassRemovalTransformer;
import org.virtue.deobfuscation.transformers.UnusedFieldRemovalTransformer;
import org.virtue.deobfuscation.transformers.UnusedMethodRemovalTransformer;
import org.virtue.deobfuscation.transformers.refactor.ClassNameTransformer;
import org.virtue.deobfuscation.transformers.refactor.FieldNameTransformer;
import org.virtue.deobfuscation.transformers.refactor.MethodNameTransformer;
import org.virtue.utility.ASMUtility;
import org.virtue.utility.ClassContainer;
import org.virtue.utility.Timer;

public class Injector {

	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(Injector.class);
	
	public static ClassContainer container;
    //public static List<ClassElement> elements;
    public static HierarchyTree hierarchyTree;
    public static List<AbstractClassIdentifier> classIdentifiers;
    public static Map<Integer, Transformer[]> transformers;

    public static int totalFields, foundFields;

    public static void deobfuscate(String dypt, String deob, String ref) {
        Timer timer = new Timer();
        transformers = new HashMap<>();
        initTransofmers();
        classIdentifiers = new LinkedList<>();
        initIdentifiers();
        timer.start();
       // elements = ASMUtility.load(new File(args[0]));
        container = new ClassContainer(ASMUtility.load(new File(dypt)));
        logger.info("Loaded " + container.getElements().size() + " Class(es) in " + timer.clock() + "ms");
        for (Transformer transform : transformers.get(1)) {
            transform.execute(container.getElements());
            System.out.println(transform.result());
        }
        container.refactor();
        ASMUtility.save(new File(deob), container.getElements().values());
        container = new ClassContainer(ASMUtility.load(new File(deob)));
        timer.start();
        hierarchyTree = new HierarchyTree(container.getElements().values());
        hierarchyTree.build();
       // System.out.println(hierarchyTree.toString());
        timer.start();
        for (AbstractClassIdentifier ident : classIdentifiers) {
            try {
            	ident.run();
            } catch (Exception e) {
            	logger.error("Error Identifiying " + ident.getClass().getSimpleName(), e);
            }
        }
        long finish = timer.clock();
        timer.start();
        for (AbstractClassIdentifier ident : classIdentifiers) {
            ident.runFields();
        }
        long fieldsFinish = timer.clock();
        int found = 0;
        for(AbstractClassIdentifier ident : classIdentifiers) {
            if(!ident.broken()) {
                found++;
            }
            System.out.println(ident.format());
        }
        container.refactor();
        for (Transformer transform : transformers.get(2)) {
            transform.execute(container.getElements());
            System.out.println(transform.result());
        }
        container.refactor();
        ASMUtility.save(new File(ref), container.getElements().values());
        logger.info("Named " + found + " out of " + classIdentifiers.size() + " Class(es) in " + finish + "ms.");
        logger.info("Named " + foundFields + " out of " + totalFields + " Field(s) in " + fieldsFinish + "ms.");
    }
    
    private static void initTransofmers() {
    	/** Phase 1 */
		transformers.put(1, new Transformer[] { 
				new IllegalStateExceptionRemovalTransformer(),
				//new UnusedFieldRemovalTransformer(), 
				new UnusedClassRemovalTransformer(),
				//new UnusedMethodRemovalTransformer(), 
				new ArithmeticStatementOrderTransformer()
				//new OpaquePredicateRemovalTransformer()
		});
		
		/** Phase 2 */
		transformers.put(2, new Transformer[] {
		    	new ClassNameTransformer(),
		    	new MethodNameTransformer()
		    	//new FieldNameTransformer()
		});
    }

    private static void initIdentifiers() {
        classIdentifiers.add(new ClientIdentifier());
        classIdentifiers.add(new NodeIdentifier());
        classIdentifiers.add(new CacheableNodeIdentifier());
        classIdentifiers.add(new DequeIdentifier());
        classIdentifiers.add(new QueueIdentifier());
        classIdentifiers.add(new BagIdentifier());
        classIdentifiers.add(new CacheIdentifier());
        classIdentifiers.add(new StreamIdentifier());
        classIdentifiers.add(new SocketIdentifier());
        classIdentifiers.add(new FileOnDiskIdentifier());
        classIdentifiers.add(new PlayerIdentifier());
        classIdentifiers.add(new NpcIdentifier());
        classIdentifiers.add(new ActorIdentifier());
        classIdentifiers.add(new RenderableIdentifier());
        classIdentifiers.add(new ModelIdentifier());
        classIdentifiers.add(new LootIdentifier());
        classIdentifiers.add(new WidgetIdentifier());
        classIdentifiers.add(new NpcDefinitionIdentifier());
        classIdentifiers.add(new PlayerDefinitionIdentifier());
        classIdentifiers.add(new ItemDefinitionIdentifier());
        classIdentifiers.add(new ObjectDefinitionIdentifier());
        classIdentifiers.add(new RegionIdentifier());
        classIdentifiers.add(new TileIdentifier());
        classIdentifiers.add(new GameObjectIdentifier());
        classIdentifiers.add(new WallObjectIdentifier());
        classIdentifiers.add(new BoundaryObjectIdentifier());
        classIdentifiers.add(new GroundObjectIdentifier());
        classIdentifiers.add(new GroundLayerIdentifier());
        classIdentifiers.add(new CollisionMapIdentifier());
        classIdentifiers.add(new LootIdentifier());
        classIdentifiers.add(new WidgetNodeIdentifier());
        classIdentifiers.add(new MouseIdentifier());
        classIdentifiers.add(new KeyboardIdentifier());
        classIdentifiers.add(new FacadeIdentifier());
    }
    
	/**
	 * @return
	 */
	public static ClassContainer getContainer() {
		return container;
	}
    
    @SuppressWarnings("unchecked")
	public static <T extends AbstractClassIdentifier>  T get(Class<T> clazz) {
        for(AbstractClassIdentifier identifier : classIdentifiers) {
            if(identifier.getClass().equals(clazz)) {
                return (T) identifier;
            }
        }
        return null;
    }

    public static ClassElement get(String name) {
        for (ClassElement element : container.getElements().values()) {
            if (element.name().equals(name)) {
                return element;
            }
        }
        return null;
    }
}
