package org.apache.samoa.topology.impl;

import org.apache.gearpump.cluster.UserConfig;
import org.apache.gearpump.cluster.client.ClientContext;
import org.apache.gearpump.streaming.StreamApplication;
import org.apache.gearpump.util.Graph;

/**
 * Created by keweisun on 7/17/2015.
 */
public class GearpumpDoTask {

    public static void main(String[] args) {

        for (String arg : args) {
            System.out.println(arg);
        }
        GearpumpTopology gearpumpTopology = GearpumpSamoaUtils.argsToTopology(args);
        String topologyName = gearpumpTopology.getTopologyName();
        System.out.println(topologyName);
        Graph graph = gearpumpTopology.getGraph();
        if (graph.isEmpty()) {
            System.out.println("graph is empty");
        } else {
            System.out.println("graph is not empty");
            System.out.println(graph.edges().length);
        }
        StreamApplication app = StreamApplication.apply("samoa", graph, UserConfig.empty());
        ClientContext context = ClientContext.apply();
        context.submit(app);
        context.close();

    }

}
