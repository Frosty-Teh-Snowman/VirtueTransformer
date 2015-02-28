package org.virtue;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

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
import org.virtue.deobfuscation.transformers.UnusedClassRemovalTransformer;
import org.virtue.utility.ASMUtility;
import org.virtue.utility.Timer;

public class Injector {

	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(Injector.class);
	
    public static List<ClassElement> elements;
    public static HierarchyTree hierarchyTree;
    public static List<AbstractClassIdentifier> classIdentifiers;
    public static List<Transformer> transformers;

    public static int totalFields, foundFields;

    public static void deobfuscate(String[] args) {
        Timer timer = new Timer();
        transformers = new LinkedList<>();
        initTransofmers();
        classIdentifiers = new LinkedList<>();
        initIdentifiers();
        timer.start();
        elements = ASMUtility.load(new File(args[0]));
        logger.info("Loaded " + elements.size() + " Class(es) in " + timer.clock() + "ms");
        for (Transformer transform : transformers) {
            transform.execute(elements);
            System.out.println(transform.result());
        }
        ASMUtility.save(new File(args[1]), elements);
        elements = ASMUtility.load(new File(args[1]));
        timer.start();
        hierarchyTree = new HierarchyTree(elements);
        hierarchyTree.build();
       // System.out.println(hierarchyTree.toString());
        timer.start();
        for (AbstractClassIdentifier ident : classIdentifiers) {
            ident.run();
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
        logger.info("Named " + found + " out of " + classIdentifiers.size() + " Class(es) in " + finish + "ms.");
        logger.info("Named " + foundFields + " out of " + totalFields + " Field(s) in " + fieldsFinish + "ms.");
    }
    
    private static void initTransofmers() {
        transformers.add(new IllegalStateExceptionRemovalTransformer());
     //   TRANSFORMS.add(new UnusedFieldRemovalTransform());
        transformers.add(new UnusedClassRemovalTransformer());
        // TRANSFORMS.add(new UnusedMethodRemovalTransform());
        transformers.add(new ArithmeticStatementOrderTransformer());
        //   TRANSFORMS.add(new OpaquePredicateRemovalTransform());
        //TRANSFORMS.add(new ControlFlowTransform());
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
        for (ClassElement element : elements) {
            if (element.name().equals(name)) {
                return element;
            }
        }
        return null;
    }
}
