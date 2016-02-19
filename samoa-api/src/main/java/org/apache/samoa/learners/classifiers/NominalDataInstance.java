package org.apache.samoa.learners.classifiers;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2014 - 2016 Apache Software Foundation
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.samoa.learners.DataInstance;

public class NominalDataInstance implements DataInstance {

    private int numNominals;
    private int numClasses;
    private double trueClass; // index started from 0
    private int[] numValsPerNominal;
    private double[] data; // index started from 0

    public NominalDataInstance(int numNominals, int numClasses, double trueClass,
                               int[] numValsPerNominal, double[] data) {
        this.numNominals = numNominals;
        this.numClasses = numClasses;
        this.trueClass = trueClass;
        this.numValsPerNominal = numValsPerNominal;
        this.data = data;
    }

    public int getNumNominals() {
        return numNominals;
    }

    public void setNumNominals(int numNominals) {
        this.numNominals = numNominals;
    }

    public int getNumClasses() {
        return numClasses;
    }

    public void setNumClasses(int numClasses) {
        this.numClasses = numClasses;
    }

    public double getTrueClass() {
        return trueClass;
    }

    public void setTrueClass(double trueClass) {
        this.trueClass = trueClass;
    }

    public int[] getNumValsPerNominal() {
        return numValsPerNominal;
    }

    public void setNumValsPerNominal(int[] numValsPerNominal) {
        this.numValsPerNominal = numValsPerNominal;
    }

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = data;
    }
}
