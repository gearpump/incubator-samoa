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

import java.io.Serializable;

/**
 * DataInstance for classification problem
 * There may be tow types of feature in feature vector: numeric feature and nominal feature
 */
public class ClassificationDataInstance implements Serializable {
    private int numberNumericFeatures;
    private double[] numericData;

    private int numberNominalFeatures;
    private int[] numberValsPerNominalFeature;
    private int[] nominalData;

    private int numberLabels;
    private int trueLabel;

    public ClassificationDataInstance(int numberNumericFeatures,
                                      double[] numericData, int numberNominalFeatures,
                                      int[] numberValsPerNominalFeature, int[] nominalData,
                                      int numberLabels, int trueLabel) {
        this.numberNumericFeatures = numberNumericFeatures;
        this.numericData = numericData;
        this.numberNominalFeatures = numberNominalFeatures;
        this.numberValsPerNominalFeature = numberValsPerNominalFeature;
        this.nominalData = nominalData;
        this.numberLabels = numberLabels;
        this.trueLabel = trueLabel;
    }

    public int getNumberNumericFeatures() {
        return numberNumericFeatures;
    }

    public double[] getNumericData() {
        return numericData;
    }

    public int getNumberNominalFeatures() {
        return numberNominalFeatures;
    }

    public int[] getNumberValsPerNominalFeature() {
        return numberValsPerNominalFeature;
    }

    public int[] getNominalData() {
        return nominalData;
    }

    public int getNumberLabels() {
        return numberLabels;
    }

    public int getTrueLabel() {
        return trueLabel;
    }
}
