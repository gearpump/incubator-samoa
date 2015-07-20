package org.apache.samoa.topology.impl;

import java.io.Serializable;

/**
 * Created by keweisun on 7/20/2015.
 */
public class ProcessorWrapperTest implements Serializable {
    private ProcessorTest pt;
    private SerializableTest st;
    private String s;
    private int i;

    public ProcessorWrapperTest(ProcessorTest pt) {
        this.pt = pt;
    }

    public ProcessorTest getPt() {
        return pt;
    }

    public void setPt(ProcessorTest pt) {
        this.pt = pt;
    }

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

    public SerializableTest getSt() {
        return st;
    }

    public void setSt(SerializableTest st) {
        this.st = st;
    }
}
