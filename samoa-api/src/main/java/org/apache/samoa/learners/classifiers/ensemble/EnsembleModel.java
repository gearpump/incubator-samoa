package org.apache.samoa.learners.classifiers.ensemble;

import org.apache.samoa.instances.Instance;
import org.apache.samoa.learners.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by gongy on 2016/1/11.
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
