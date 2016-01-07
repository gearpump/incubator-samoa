package org.apache.samoa.learners.clusterers;

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
            distances[c] = distance;
        }
        return distances;
    }

    public double evaluate(ArrayList<DataPoint> points, MeasureCollection measure) {
        double score = 0.0;
        try {
            score = measure.evaluateClusteringPerformance(clustering, null, points);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }
}
