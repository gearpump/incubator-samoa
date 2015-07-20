package org.apache.samoa.topology.impl;

import org.apache.gearpump.cluster.UserConfig;
import org.apache.gearpump.streaming.Processor;
import org.apache.gearpump.util.Graph;
import org.apache.samoa.core.EntranceProcessor;
import org.apache.samoa.topology.AbstractEntranceProcessingItem;
import org.apache.samoa.topology.IProcessingItem;
import org.apache.samoa.topology.Stream;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public class GearpumpEntranceProcessingItem extends AbstractEntranceProcessingItem
        implements GearpumpTopologyNode, Serializable {

    private GearpumpStream stream;
    private Graph graph = null;
    private Map<IProcessingItem, Processor> piToProcessor;

    public GearpumpEntranceProcessingItem(EntranceProcessor entranceProcessor) {
        super(entranceProcessor);
        this.setName(entranceProcessor.getClass().getName());
    }

    @Override
    public void setGraph(Graph graph) {
        if (this.graph == null) {
            this.graph = graph;
        }
    }

    @Override
    public GearpumpStream createStream() {
        GearpumpStream stream = new GearpumpStream(this);
        this.stream = stream;
        return stream;
    }

    @Override
    public Processor createGearpumpProcessor() {
        byte[] bytes = GearpumpSamoaUtils.objectToBytes(this);
        System.out.println("entrance pi write to bytes success");
        UserConfig userConfig = UserConfig.empty().withBytes(GearpumpSamoaUtils.entrancePiConf,bytes);
        System.out.println("entrance pi create userConfig success");
        Processor<GearpumpEntranceProcessingItemTask> gearpumpProcessor =
                new org.apache.gearpump.streaming.Processor
                .DefaultProcessor<>(1, "", userConfig, GearpumpEntranceProcessingItemTask.class);
        System.out.println("entrance pi create processor success");
        if (gearpumpProcessor == null) {
            System.out.println("gearpump processor is null");
        } else {
            System.out.println("gearpump processor is not null");
            System.out.println("parallelism: " + gearpumpProcessor.parallelism());
        }
        return gearpumpProcessor;
    }

    public GearpumpStream getStream() {
        return stream;
    }

    @Override
    public void setPiToProcessor(Map map) {
        this.piToProcessor = map;
    }

    private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        stream.writeObject(getProcessor());
        stream.writeObject(getName());
        stream.writeObject(getStream());
        stream.writeObject(this.stream);
        stream.writeObject(graph);
        stream.writeObject(piToProcessor);
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        setProcessor((org.apache.samoa.core.EntranceProcessor) stream.readObject());
        setName((String) stream.readObject());
        setOutputStream((Stream) stream.readObject());
        this.stream = (GearpumpStream) stream.readObject();
        graph = (Graph) stream.readObject();
        piToProcessor = (Map<IProcessingItem, org.apache.gearpump.streaming.Processor>) stream.readObject();
    }

}
