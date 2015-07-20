package org.apache.samoa.topology.impl;

import org.apache.gearpump.streaming.Processor;
import org.apache.gearpump.util.Graph;

import java.util.Map;

/**
 * Created by keweisun on 7/14/2015.
 */
public interface GearpumpTopologyNode {

    public void setGraph(Graph graph);

    public GearpumpStream createStream();

    public Processor createGearpumpProcessor();

    public void setPiToProcessor(Map map);
}
