package org.apache.samoa.topology.impl;

import org.apache.gearpump.cluster.UserConfig;
import org.apache.gearpump.partitioner.ShufflePartitioner;
import org.apache.gearpump.util.Graph;
import org.apache.samoa.core.Processor;
import org.apache.samoa.topology.AbstractProcessingItem;
import org.apache.samoa.topology.IProcessingItem;
import org.apache.samoa.topology.ProcessingItem;
import org.apache.samoa.topology.Stream;
import org.apache.samoa.utils.PartitioningScheme;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by keweisun on 7/6/2015.
 */
public class GearpumpProcessingItem extends AbstractProcessingItem implements GearpumpTopologyNode, Serializable {
    private static final long serialVersionUID = -9066409791668954099L;
    private Graph graph = null;
    private Set<GearpumpStream> streams;
    private Map<IProcessingItem, org.apache.gearpump.streaming.Processor> piToProcessor;


    public GearpumpProcessingItem(Processor processor, int parallelism) {
        super(processor, parallelism);
        this.setName(processor.getClass().getSimpleName());
        this.streams = new HashSet<GearpumpStream>();
    }

    @Override
    protected ProcessingItem addInputStream(Stream inputStream, PartitioningScheme scheme) {
        IProcessingItem sourcePi = ((GearpumpStream) inputStream).getSourceProcessingItem();
        org.apache.gearpump.streaming.Processor sourceProcessor = piToProcessor.get(sourcePi);
        org.apache.gearpump.streaming.Processor targetProcessor = piToProcessor.get(this);
        ((GearpumpStream) inputStream).setTargetId(this.getName());
        ShufflePartitioner shufflePartitioner;
        switch (scheme) {
            case SHUFFLE:
                shufflePartitioner = new ShufflePartitioner();
                graph.addEdge(sourceProcessor, shufflePartitioner, targetProcessor);
                break;
            case GROUP_BY_KEY:
                shufflePartitioner = new ShufflePartitioner();
                graph.addEdge(sourceProcessor, shufflePartitioner, targetProcessor);
                break;
            case BROADCAST:
                shufflePartitioner = new ShufflePartitioner();
                graph.addEdge(sourceProcessor, shufflePartitioner, targetProcessor);
                break;
        }
        return this;
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
        streams.add(stream);
        return stream;
    }

    @Override
    public org.apache.gearpump.streaming.Processor createGearpumpProcessor() {
        byte[] bytes = GearpumpSamoaUtils.objectToBytes(this);
        UserConfig userConfig = UserConfig.empty().withBytes(GearpumpSamoaUtils.piConf, bytes);
        org.apache.gearpump.streaming.Processor<GearpumpProcessingItemTask> gearpumpProcessor =
                new org.apache.gearpump.streaming.Processor
                .DefaultProcessor<>(this.getParallelism(), "", userConfig, GearpumpProcessingItemTask.class);
        return gearpumpProcessor;
    }

    public Set<GearpumpStream> getStreams() {
        return streams;
    }

    @Override
    public void setPiToProcessor(Map map) {
        this.piToProcessor = map;
    }

    private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        stream.writeObject(getProcessor());
        stream.writeObject(getName());
        stream.writeInt(getParallelism());
        stream.writeObject(streams);
        stream.writeObject(graph);
        stream.writeObject(piToProcessor);
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        setProcessor((Processor) stream.readObject());
        setName((String) stream.readObject());
        setParallelism(stream.readInt());
        streams = (Set<GearpumpStream>) stream.readObject();
        graph = (Graph) stream.readObject();
        piToProcessor = (Map<IProcessingItem, org.apache.gearpump.streaming.Processor>) stream.readObject();
    }

}
