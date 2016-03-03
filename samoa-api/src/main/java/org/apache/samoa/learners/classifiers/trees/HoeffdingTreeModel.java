package org.apache.samoa.learners.classifiers.trees;

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
import org.apache.samoa.instances.Instances;
import org.apache.samoa.instances.Utils;
import org.apache.samoa.learners.InstanceUtils;
import org.apache.samoa.learners.classifiers.ClassificationDataInstance;
import org.apache.samoa.learners.classifiers.ClassificationModel;

public class HoeffdingTreeModel implements ClassificationModel {
    private Instances dataset;
    private Node treeRoot;

    public HoeffdingTreeModel(Instances dataset, Node treeRoot) {
        this.dataset = dataset;
        this.treeRoot = treeRoot;
    }

    @Override
    public double[] predict(ClassificationDataInstance dataInstance) {
        double[] prediction;
        Instance inst = InstanceUtils.convertClassificationDataInstance(dataInstance);
        // inst.setDataset(dataset);

        FoundNode foundNode;
        if (treeRoot != null) {
            foundNode = treeRoot.filterInstanceToLeaf(inst, null, -1);
            Node leafNode = foundNode.getNode();
            if (leafNode == null) {
                leafNode = foundNode.getParent();
            }
            prediction = leafNode.getClassVotes(inst, null);
        } else {
            int numClasses = dataset.numClasses();
            prediction = new double[numClasses];
        }

        return prediction;
    }

    /**
     * Predict the class of an input data instance, and evaluate if it is the true class.
     */
    public boolean evaluate(ClassificationDataInstance dataInstance) {
        Instance inst = InstanceUtils.convertClassificationDataInstance(dataInstance);
        int trueClass = (int) inst.classValue();
        double[]  prediction = this.predict(dataInstance);
        int predictedClass = Utils.maxIndex(prediction);

        return trueClass == predictedClass;
    }

    @Override
    public String toString() {
        return "HoeffdingTreeModel{ " +
                "number of classes: " + dataset.numClasses() +
                ", number of attributes: " + dataset.numAttributes() +
                " }";
    }
}
