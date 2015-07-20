package org.apache.samoa.topology.impl;

import org.apache.samoa.core.EntranceProcessor;
import org.apache.samoa.core.Processor;
import org.apache.samoa.topology.*;

/**
 * Created by keweisun on 7/6/2015.
 */
public class GearpumpComponentFactory implements ComponentFactory {
    @Override
    public ProcessingItem createPi(Processor processor) {
        return createPi(processor, 1);
    }

    @Override
    public ProcessingItem createPi(Processor processor, int parallelism) {
        System.out.println("factory: create processor: " + processor.getClass().getSimpleName()
                + ", parallelism: " + parallelism);
        ProcessingItem pi = new GearpumpProcessingItem(processor,parallelism);
        return pi;
    }

    @Override
    public EntranceProcessingItem createEntrancePi(EntranceProcessor entranceProcessor) {
        System.out.println("factory: create entrance processor: "
                + entranceProcessor.getClass().getSimpleName());
        EntranceProcessingItem entrancePi = new GearpumpEntranceProcessingItem(entranceProcessor);
        return entrancePi;
    }

    @Override
    public Stream createStream(IProcessingItem sourcePi) {
        System.out.println("factory: create stream, source: "
                + sourcePi.getProcessor().getClass().getSimpleName());
        GearpumpTopologyNode gearpumpTopologyNode = (GearpumpTopologyNode) sourcePi;
        return gearpumpTopologyNode.createStream();
    }

    @Override
    public Topology createTopology(String topoName) {
        return new GearpumpTopology(topoName);
    }
}
