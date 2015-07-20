package org.apache.samoa.topology.impl;

import org.apache.gearpump.Message;
import org.apache.gearpump.cluster.UserConfig;
import org.apache.gearpump.streaming.task.StartTime;
import org.apache.gearpump.streaming.task.Task;
import org.apache.gearpump.streaming.task.TaskContext;
import org.apache.samoa.core.EntranceProcessor;

/**
 * Created by keweisun on 7/16/2015.
 */
public class GearpumpEntranceProcessingItemTask extends Task {
    EntranceProcessor entranceProcessor;
    private TaskContext taskContext;
    private UserConfig userConfig;
    private GearpumpStream outputStream;

    public GearpumpEntranceProcessingItemTask(TaskContext taskContext, UserConfig userConf) {
        super(taskContext, userConf);
        this.taskContext = taskContext;
        this.userConfig = userConf;
        byte[] bytes = userConf.getBytes(GearpumpSamoaUtils.entrancePiConf).get();
        GearpumpEntranceProcessingItem entranceProcessingItem =
                ((GearpumpEntranceProcessingItem) GearpumpSamoaUtils.bytesToObject(bytes));
        this.entranceProcessor = entranceProcessingItem.getProcessor();
        this.outputStream = entranceProcessingItem.getStream();
    }

    @Override
    public void onStart(StartTime startTime) {
        outputStream.setTaskContext(this.taskContext);

        entranceProcessor.onCreate(taskContext.taskId().index());
        self().tell(new Message("start", System.currentTimeMillis()), self());
    }

    @Override
    public void onNext(Message msg) {
        if (entranceProcessor.hasNext()) {
            GearpumpMessage message =
                    new GearpumpMessage(entranceProcessor.nextEvent(), outputStream.getTargetId());
            taskContext.output(new Message(message, System.currentTimeMillis()));
        }
        self().tell(new Message("continue", System.currentTimeMillis()), self());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}

