package org.virtue.bytecode.graph;

import java.util.LinkedList;
import java.util.List;

import org.virtue.Injector;
import org.virtue.bytecode.graph.method.EntryPoint;
import org.virtue.bytecode.graph.method.MethodCall;

/**
 * @author : const_
 */
public class MethodCallGraph {

    private List<EntryPoint> entryPoints;
    private List<MethodCall> calls = new LinkedList<>();
    private List<MethodCall> visited = new LinkedList<>();

    public MethodCallGraph(List<EntryPoint> entryPoints) {
        this.entryPoints = entryPoints;
    }

    public void build() {
        List<MethodCall> toVisit = new LinkedList<>();
        for (EntryPoint entry : entryPoints) {
            for(MethodCall call : entry.calls()) {
                if(Injector.get(call.node().owner) == null || visited(call)) {
                    continue;
                }
                toVisit.add(call);
                calls.add(call);
            }
        }
        while (true) {
            MethodCall next = null;
            while (!toVisit.isEmpty()) {
                next = toVisit.get(0);
                if (visited(next)) {
                    break;
                }
                visited.add(next);
                for(MethodCall call : next.calls()) {
                    if(!visited(call) && Injector.get(call.node().owner) != null) {
                        calls.add(call);
                        toVisit.add(call);
                    }
                }
            }
            if (next != null) {
                toVisit.remove(next);
                continue;
            }
            break;
        }
    }

    public boolean visited(MethodCall call) {
        for(MethodCall call_ : visited) {
            if(call.node().name.equals(call_.node().name) &&
                    call.node().owner.equals(call_.node().owner) &&
                    call.node().desc.equals(call_.node().desc)) {
                return true;
            }
        }
        return false;
    }

    public List<MethodCall> calls() {
        return calls;
    }
}
