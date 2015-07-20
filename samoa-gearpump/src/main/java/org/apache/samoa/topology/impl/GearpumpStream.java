package org.apache.samoa.topology.impl;

import org.apache.gearpump.Message;
import org.apache.gearpump.streaming.task.TaskContext;
import org.apache.samoa.core.ContentEvent;
import org.apache.samoa.topology.AbstractStream;
import org.apache.samoa.topology.IProcessingItem;

import java.io.Serializable;

/**
 * Created by keweisun on 7/6/2015.
 */
public class GearpumpStream extends AbstractStream implements Serializable {

    private TaskContext taskContext;
    private String targetId;

    public GearpumpStream(IProcessingItem sourcePi) {
        super(sourcePi);
    }

    public void setTaskContext(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetId() {
        return targetId;
    }

    @Override
    public void put(ContentEvent event) {
        GearpumpMessage message = new GearpumpMessage(event, targetId);
        taskContext.output(new Message(message, System.currentTimeMillis()));
    }

}
