package org.apache.samoa.learners.clusterers;

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


import org.apache.samoa.instances.Instance;
import org.apache.samoa.learners.Model;
import org.apache.samoa.moa.cluster.Clustering;
import org.apache.samoa.moa.core.DataPoint;
import org.apache.samoa.moa.evaluation.MeasureCollection;

import java.util.ArrayList;

public class CluStreamModel implements Model {
    private Clustering clustering;

    public CluStreamModel(Clustering clustering) {
        this.clustering = clustering;
    }

    public CluStreamModel() {
    }

    @Override
    public double[] predict(Instance inst) {
        DataPoint dataPoint = (DataPoint) inst;
        double[] distances = new double[clustering.size()];
        for (int c = 0; c < clustering.size(); c++) {
            double distance = 0.0;
            double[] center = clustering.get(c).getCenter();
            for (int i = 0; i < center.length; i++) {
                double d = dataPoint.value(i) - center[i];
                distance += d * d;
            }
            distances[c] = Math.sqrt(distance);
        }
        return distances;
    }

    public double evaluate(ArrayList<DataPoint> points, MeasureCollection measure) {
        double score = 0.0;
        try {
            measure.evaluateClusteringPerformance(clustering, null, points);
            score = measure.getMean(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }

    @Override
    public String toString() {
        return "CluStreamModel{ " +
                "number of kernels = " + clustering.size() +
                ", " +
                "dimension of a kernel = " + clustering.dimension() +
                " }";
    }
}
