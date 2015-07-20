package org.apache.samoa.topology.impl;

import org.apache.samoa.core.ContentEvent;

/**
 * Created by keweisun on 7/16/2015.
 */
public class GearpumpMessage {
    private ContentEvent event;
    private String targetId;

    public GearpumpMessage() {
        this(null, null);
    }

    public GearpumpMessage(ContentEvent event, String targetId) {
        this.event = event;
        this.targetId = targetId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public ContentEvent getEvent() {
        return event;
    }

    public void setEvent(ContentEvent event) {
        this.event = event;
    }
}
