package org.apache.samoa.learners.classifiers.ensemble;

import org.apache.samoa.instances.Instance;
import org.apache.samoa.learners.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2014 - 2015 Apache Software Foundation
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

public class EnsembleModel implements Model {
    private ArrayList<Model> modelList;
    private ArrayList<Double> modelWeightList;

    public EnsembleModel() {
    }

    public EnsembleModel(ArrayList<Model> modelList, ArrayList<Double> modelWeightList) {
        this.modelList = modelList;
        this.modelWeightList = modelWeightList;
    }

    private void normalize() {
        double sum = 0.0;
        for (double weight: modelWeightList) {
            sum += weight;
        }
        for (int i = 0; i < modelWeightList.size(); i++) {
            modelWeightList.set(i, modelWeightList.get(i) / sum);
        }
    }

    @Override
    public double[] predict(Instance inst) {
        double[] prediction = new double[inst.numClasses()];
        Arrays.fill(prediction, 0.0);
        this.normalize();

        for (int i = 0; i < modelList.size(); i++) {
            double[] predictionPart = modelList.get(i).predict(inst);
            for (int j = 0; j < prediction.length; j++) {
                prediction[j] = prediction[j] + modelWeightList.get(i) * predictionPart[j];
            }
        }
        return prediction;
    }
}
