package org.apache.samoa.topology.impl;

import org.apache.samoa.core.ContentEvent;
import org.apache.samoa.core.Processor;

/**
 * Created by keweisun on 7/20/2015.
 */
public class ProcessorTest implements Processor {
    private static final long serialVersionUID = -9066409791668954444L;

    public int i = 0;
    public String s = null;

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    @Override
    public boolean process(ContentEvent event) {
        return false;
    }

    @Override
    public void onCreate(int id) {

    }

    @Override
    public Processor newProcessor(Processor processor) {
        return null;
    }
}
