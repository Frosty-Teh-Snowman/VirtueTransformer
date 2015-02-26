package net.lemonrs.lemonpicker;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.lemonrs.lemonpicker.bytecode.element.ClassElement;
import net.lemonrs.lemonpicker.deob.Transformer;
import net.lemonrs.lemonpicker.deob.impl.ArithmeticStatementOrderTransformer;
import net.lemonrs.lemonpicker.deob.impl.IllegalStateExceptionRemovalTransformer;
import net.lemonrs.lemonpicker.deob.impl.UnusedClassRemovalTransformer;
import net.lemonrs.lemonpicker.graph.hierarchy.HierarchyTree;
import net.lemonrs.lemonpicker.identifier.AbstractClassIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.ClientIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.FacadeIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.cache.CacheIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.cache.ModelIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.cache.WidgetIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.cache.WidgetNodeIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.cache.definition.ItemDefinitionIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.cache.definition.NpcDefinitionIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.cache.definition.ObjectDefinitionIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.cache.definition.PlayerDefinitionIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.input.KeyboardIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.input.MouseIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.net.FileOnDiskIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.net.SocketIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.net.StreamIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.node.BagIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.node.CacheableNodeIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.node.DequeIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.node.NodeIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.node.QueueIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.renderable.ActorIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.renderable.LootIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.renderable.NpcIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.renderable.PlayerIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.renderable.RenderableIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.scene.CollisionMapIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.scene.RegionIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.scene.TileIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.scene.object.BoundaryObjectIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.scene.object.GameObjectIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.scene.object.GroundLayerIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.scene.object.GroundObjectIdentifier;
import net.lemonrs.lemonpicker.identifier.impl.scene.object.WallObjectIdentifier;
import net.lemonrs.lemonpicker.util.ASMUtil;
import net.lemonrs.lemonpicker.util.RevisionFinder;
import net.lemonrs.lemonpicker.util.Timer;

public class Main {

    public static List<ClassElement> elements;
    public static HierarchyTree hierarchyTree;
    public static List<AbstractClassIdentifier> classIdentifiers;
    public static List<Transformer> transformers;

    public static int totalFields, foundFields;

    public static void main(String[] args) {
        Timer timer = new Timer();
        transformers = new LinkedList<>();
        initTransofmers();
        classIdentifiers = new LinkedList<>();
        initIdentifiers();
        System.out.println("\t\tLemonPicker initialized...");
        System.out.println("\t\tFinding tree...");
        timer.start();
        elements = ASMUtil.load(new File(args[0]));
        System.out.println("\t\tLocated tree with " + elements.size() + " lemons in " + timer.clock() + "ms");
        int revision = RevisionFinder.find();
        System.out.println("\t\tMeasured tree with a height of " + revision + " meters");
        System.out.println("\t\tThrowing away rotten lemons...");
        for (Transformer transform : transformers) {
            transform.execute(elements);
            System.out.println(transform.result());
        }
        System.out.println("\t\tFinished throwing away rotten lemons...");
        ASMUtil.save(new File(args[1]), elements);
        System.out.println("\t\tReplanting tree...");
        elements = ASMUtil.load(new File(args[1]));
        System.out.println("\t\tLocating lemons...");
        timer.start();
        hierarchyTree = new HierarchyTree(elements);
        hierarchyTree.build();
       // System.out.println(hierarchyTree.toString());
        System.out.println("\t\tConstructed graph of lemons in " + timer.clock() + "ms");
        System.out.println("\t\tBeginning to name lemons and their seeds...");
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
        System.out.println("\t\tNamed " + found + " out of " + classIdentifiers.size() + " lemons in " + finish + "ms.");
        System.out.println("\t\tNamed " + foundFields + " out of " + totalFields + " seeds in " + fieldsFinish + "ms.");
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
