package org.apache.samoa.topology.impl;

import org.apache.gearpump.streaming.Processor;
import org.apache.gearpump.util.Graph;
import org.apache.samoa.topology.AbstractTopology;
import org.apache.samoa.topology.IProcessingItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by keweisun on 7/6/2015.
 */
public class GearpumpTopology extends AbstractTopology {

    private Graph graph;
    private Map<IProcessingItem,Processor> piToProcessor;

    public GearpumpTopology(String name) {
        super(name);
        graph = Graph.empty();
        piToProcessor = new HashMap<IProcessingItem,Processor>();
    }

    @Override
    public void addProcessingItem(IProcessingItem procItem, int parallelismHint) {
        GearpumpTopologyNode gearpumpTopologyNode = (GearpumpTopologyNode) procItem;
        System.out.println("GearpumpTopology: add pi: " + procItem.getProcessor().getClass().getSimpleName());
        Processor gearpumpProcessor = gearpumpTopologyNode.createGearpumpProcessor();
        System.out.println("add pi success");
        piToProcessor.put(procItem, gearpumpProcessor);
        System.out.println("put processor success");
        System.out.println("pi.getProcessor(): " + procItem.getProcessor().getClass().getSimpleName());
        graph.addVertex(gearpumpProcessor);
        System.out.println("add vertex success");
        ((GearpumpTopologyNode) procItem).setPiToProcessor(piToProcessor);
        ((GearpumpTopologyNode) procItem).setGraph(graph);
        System.out.println("set processor and graph success");
        super.addProcessingItem(procItem, parallelismHint);
    }

    public Graph getGraph() {
        return graph;
    }
}
