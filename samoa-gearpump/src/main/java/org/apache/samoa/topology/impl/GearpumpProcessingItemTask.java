package org.apache.samoa.topology.impl;

import org.apache.gearpump.Message;
import org.apache.gearpump.cluster.UserConfig;
import org.apache.gearpump.streaming.task.StartTime;
import org.apache.gearpump.streaming.task.Task;
import org.apache.gearpump.streaming.task.TaskContext;
import org.apache.samoa.core.ContentEvent;
import org.apache.samoa.core.Processor;

import java.util.Set;

/**
 * Created by keweisun on 7/15/2015.
 */
public class GearpumpProcessingItemTask extends Task {

    private TaskContext taskContext;
    private UserConfig userConfig;
    private Processor processor;
    private Set<GearpumpStream> streams;

    public GearpumpProcessingItemTask(TaskContext taskContext, UserConfig userConf) {
        super(taskContext, userConf);
        this.taskContext = taskContext;
        this.userConfig = userConf;
        byte[] bytes = userConf.getBytes(GearpumpSamoaUtils.piConf).get();
        GearpumpProcessingItem pi = (GearpumpProcessingItem) GearpumpSamoaUtils.bytesToObject(bytes);
        this.processor = pi.getProcessor();
        if (this.processor != null) {
            LOG().info("piTask.processor is not null");
            LOG().info("piTask.processor: " + this.processor.getClass().getSimpleName());
        } else {
            LOG().info("piTask.processor is null");
        }
        this.streams = pi.getStreams();
        if (this.streams != null && this.streams.isEmpty()) {
            LOG().info("piTask.streams is not null");
            LOG().info("piTask.streams,size: " + this.streams.size());
        } else {
            LOG().info("piTask.processor is null");
        }
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public void onStart(StartTime startTime) {
        for (GearpumpStream stream : streams) {
            stream.setTaskContext(this.taskContext);
        }
        if (this.processor != null) {
            LOG().info("piTask.processor is not null");
            LOG().info("piTask.processor: " + this.processor.getClass().getSimpleName());
        } else {
            LOG().info("piTask.processor is null");
        }

        processor.onCreate(taskContext.taskId().index());
    }

    @Override
    public void onNext(Message msg) {
        GearpumpMessage message = (GearpumpMessage) msg.msg();
        String targetId = message.getTargetId();
        if (targetId.equals(this.processor.getClass().getSimpleName())){
            ContentEvent event = message.getEvent();
            processor.process(event);
        }
    }

    @Override
    public void onStop() {
    }
}
