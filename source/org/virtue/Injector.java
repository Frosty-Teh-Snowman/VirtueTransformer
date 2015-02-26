package org.virtue;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.graph.hierarchy.HierarchyTree;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.Transformer;
import org.virtue.deobfuscation.identifiers.ClientIdentifier;
import org.virtue.deobfuscation.identifiers.FacadeIdentifier;
import org.virtue.deobfuscation.identifiers.cache.CacheIdentifier;
import org.virtue.deobfuscation.identifiers.cache.ModelIdentifier;
import org.virtue.deobfuscation.identifiers.cache.WidgetIdentifier;
import org.virtue.deobfuscation.identifiers.cache.WidgetNodeIdentifier;
import org.virtue.deobfuscation.identifiers.cache.definition.ItemDefinitionIdentifier;
import org.virtue.deobfuscation.identifiers.cache.definition.NpcDefinitionIdentifier;
import org.virtue.deobfuscation.identifiers.cache.definition.ObjectDefinitionIdentifier;
import org.virtue.deobfuscation.identifiers.cache.definition.PlayerDefinitionIdentifier;
import org.virtue.deobfuscation.identifiers.input.KeyboardIdentifier;
import org.virtue.deobfuscation.identifiers.input.MouseIdentifier;
import org.virtue.deobfuscation.identifiers.net.FileOnDiskIdentifier;
import org.virtue.deobfuscation.identifiers.net.SocketIdentifier;
import org.virtue.deobfuscation.identifiers.net.StreamIdentifier;
import org.virtue.deobfuscation.identifiers.node.BagIdentifier;
import org.virtue.deobfuscation.identifiers.node.CacheableNodeIdentifier;
import org.virtue.deobfuscation.identifiers.node.DequeIdentifier;
import org.virtue.deobfuscation.identifiers.node.NodeIdentifier;
import org.virtue.deobfuscation.identifiers.node.QueueIdentifier;
import org.virtue.deobfuscation.identifiers.renderable.ActorIdentifier;
import org.virtue.deobfuscation.identifiers.renderable.LootIdentifier;
import org.virtue.deobfuscation.identifiers.renderable.NpcIdentifier;
import org.virtue.deobfuscation.identifiers.renderable.PlayerIdentifier;
import org.virtue.deobfuscation.identifiers.renderable.RenderableIdentifier;
import org.virtue.deobfuscation.identifiers.scene.CollisionMapIdentifier;
import org.virtue.deobfuscation.identifiers.scene.RegionIdentifier;
import org.virtue.deobfuscation.identifiers.scene.TileIdentifier;
import org.virtue.deobfuscation.identifiers.scene.object.BoundaryObjectIdentifier;
import org.virtue.deobfuscation.identifiers.scene.object.GameObjectIdentifier;
import org.virtue.deobfuscation.identifiers.scene.object.GroundLayerIdentifier;
import org.virtue.deobfuscation.identifiers.scene.object.GroundObjectIdentifier;
import org.virtue.deobfuscation.identifiers.scene.object.WallObjectIdentifier;
import org.virtue.deobfuscation.transformers.ArithmeticStatementOrderTransformer;
import org.virtue.deobfuscation.transformers.IllegalStateExceptionRemovalTransformer;
import org.virtue.deobfuscation.transformers.UnusedClassRemovalTransformer;
import org.virtue.utility.ASMUtility;
import org.virtue.utility.RevisionFinder;
import org.virtue.utility.Timer;

public class Injector {

    private List<ClassElement> elements;
    private HierarchyTree hierarchyTree;
    private List<AbstractClassIdentifier> classIdentifiers;
    private List<Transformer> transformers;

    private int totalFields, foundFields;
    
    public Injector() {
    	elements = new ArrayList<ClassElement>();
    	classIdentifiers = new LinkedList<AbstractClassIdentifier>();
    	transformers = new LinkedList<Transformer>();
    }
    
    public void initialization(String path) {
    	elements = ASMUtility.load(new File(path));
    }

    public void transform(String to) {
        for (Transformer transform : transformers) {
            transform.execute(elements);
            System.out.println(transform.result());
        }
        ASMUtility.save(new File(to), elements);
    }
    
    public void identify() {
        for (AbstractClassIdentifier ident : classIdentifiers) {
            ident.run();
        }
        for (AbstractClassIdentifier ident : classIdentifiers) {
            ident.runFields();
        }
        for(AbstractClassIdentifier ident : classIdentifiers) {
            System.out.println(ident.format());
        }
    }
    
    public void deobfuscate(String from, String to) {
        Timer timer = new Timer();
        transformers = new LinkedList<>();
        registerTransformers();
        registerIdentifiers();
        System.out.println("\t\tLemonPicker initialized...");
        System.out.println("\t\tFinding tree...");
        timer.start();
        elements = ASMUtility.load(new File(from));
        System.out.println("\t\tLocated tree with " + elements.size() + " lemons in " + timer.clock() + "ms");
        int revision = RevisionFinder.find();
        System.out.println("\t\tMeasured tree with a height of " + revision + " meters");
        System.out.println("\t\tThrowing away rotten lemons...");
        for (Transformer transform : transformers) {
            transform.execute(elements);
            System.out.println(transform.result());
        }
        System.out.println("\t\tFinished throwing away rotten lemons...");
        ASMUtility.save(new File(to), elements);
        System.out.println("\t\tReplanting tree...");
        elements = ASMUtility.load(new File(to));
        System.out.println("\t\tLocating lemons...");
        timer.start();
        hierarchyTree = new HierarchyTree(elements);
        hierarchyTree.build();
       // System.out.println(hierarchyTree.toString(this));
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
    
    public void registerTransformers() {
        transformers.add(new IllegalStateExceptionRemovalTransformer(this));
        transformers.add(new UnusedClassRemovalTransformer(this));
        transformers.add(new ArithmeticStatementOrderTransformer(this));
    }

    public void registerIdentifiers() {
        classIdentifiers.add(new ClientIdentifier(this));
        classIdentifiers.add(new NodeIdentifier(this));
        classIdentifiers.add(new CacheableNodeIdentifier(this));
        classIdentifiers.add(new DequeIdentifier(this));
        classIdentifiers.add(new QueueIdentifier(this));
        classIdentifiers.add(new BagIdentifier(this));
        classIdentifiers.add(new CacheIdentifier(this));
        classIdentifiers.add(new StreamIdentifier(this));
        classIdentifiers.add(new SocketIdentifier(this));
        classIdentifiers.add(new FileOnDiskIdentifier(this));
        classIdentifiers.add(new PlayerIdentifier(this));
        classIdentifiers.add(new NpcIdentifier(this));
        classIdentifiers.add(new ActorIdentifier(this));
        classIdentifiers.add(new RenderableIdentifier(this));
        classIdentifiers.add(new ModelIdentifier(this));
        classIdentifiers.add(new LootIdentifier(this));
        classIdentifiers.add(new WidgetIdentifier(this));
        classIdentifiers.add(new NpcDefinitionIdentifier(this));
        classIdentifiers.add(new PlayerDefinitionIdentifier(this));
        classIdentifiers.add(new ItemDefinitionIdentifier(this));
        classIdentifiers.add(new ObjectDefinitionIdentifier(this));
        classIdentifiers.add(new RegionIdentifier(this));
        classIdentifiers.add(new TileIdentifier(this));
        classIdentifiers.add(new GameObjectIdentifier(this));
        classIdentifiers.add(new WallObjectIdentifier(this));
        classIdentifiers.add(new BoundaryObjectIdentifier(this));
        classIdentifiers.add(new GroundObjectIdentifier(this));
        classIdentifiers.add(new GroundLayerIdentifier(this));
        classIdentifiers.add(new CollisionMapIdentifier(this));
        classIdentifiers.add(new LootIdentifier(this));
        classIdentifiers.add(new WidgetNodeIdentifier(this));
        classIdentifiers.add(new MouseIdentifier(this));
        classIdentifiers.add(new KeyboardIdentifier(this));
        classIdentifiers.add(new FacadeIdentifier(this));
    }
    
    public List<ClassElement> getElements() {
    	return elements;
    }

    @SuppressWarnings("unchecked")
	public <T extends AbstractClassIdentifier>  T get(Class<T> clazz) {
        for(AbstractClassIdentifier identifier : classIdentifiers) {
            if(identifier.getClass().equals(clazz)) {
                return (T) identifier;
            }
        }
        return null;
    }

    public ClassElement get(String name) {
        for (ClassElement element : elements) {
            if (element.name().equals(name)) {
                return element;
            }
        }
        return null;
    }
}
