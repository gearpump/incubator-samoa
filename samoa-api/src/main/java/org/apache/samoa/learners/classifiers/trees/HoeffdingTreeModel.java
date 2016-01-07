package org.apache.samoa.learners.classifiers.trees;

import org.apache.samoa.instances.Instance;
import org.apache.samoa.instances.Instances;
import org.apache.samoa.instances.Utils;
import org.apache.samoa.learners.Model;

public class HoeffdingTreeModel implements Model {
    private Instances dataset;
    private Node treeRoot;

    public HoeffdingTreeModel(Instances dataset, Node treeRoot) {
        this.dataset = dataset;
        this.treeRoot = treeRoot;
    }

    public HoeffdingTreeModel() {
    }

    @Override
    public double[] predict(Instance inst) {
        double[] prediction;
        inst.setDataset(dataset);

        FoundNode foundNode;
        if (this.treeRoot != null) {
            foundNode = this.treeRoot.filterInstanceToLeaf(inst, null, -1);
            Node leafNode = foundNode.getNode();
            if (leafNode == null) {
                leafNode = foundNode.getParent();
            }
            prediction = leafNode.getClassVotes(inst, null);
        } else {
            int numClasses = this.dataset.numClasses();
            prediction = new double[numClasses];
        }
        return prediction;
    }

    public boolean evaluate(Instance inst) {
        double score = 0.0;
        int trueClass = (int) inst.classValue();
        double[]  prediction = this.predict(inst);
        int predictedClass = Utils.maxIndex(prediction);
        return trueClass == predictedClass;
    }
}
