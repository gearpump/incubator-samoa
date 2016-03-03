package org.apache.samoa.learners;

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

import org.apache.samoa.instances.*;
import org.apache.samoa.learners.classifiers.ClassificationDataInstance;
import org.apache.samoa.learners.clusterers.ClusterDataInstance;
import org.apache.samoa.moa.core.DataPoint;
import org.apache.samoa.moa.core.FastVector;

import java.util.ArrayList;
import java.util.Arrays;

public class InstanceUtils {

    private static InstancesHeader getClusterInstanceHeader(ClusterDataInstance dataInstance) {
        ArrayList<Attribute> attributes = new ArrayList<>();

        for (int i = 0; i < dataInstance.getNumberFeatures(); i++) {
            attributes.add(new Attribute("att" + (i + 1)));
        }

        // attributes.add(new Attribute("class", null));

        InstancesHeader instancesHeader = new InstancesHeader(
                new Instances(null, attributes, 0));
        instancesHeader.setClassIndex(instancesHeader.numAttributes() - 1);

        return instancesHeader;
    }

    private static InstancesHeader getClassificationInstanceHeader(ClassificationDataInstance dataInstance) {
        FastVector<Attribute> attributes = new FastVector<>();

        for (int i = 0; i < dataInstance.getNumberNominalFeatures(); i++) {
            FastVector<String> nominalAttVals = new FastVector<>();
            for (int j = 0; j < dataInstance.getNumberValsPerNominalFeature()[i]; j++) {
                nominalAttVals.addElement("value" + (j + 1));
            }
            attributes.addElement(new Attribute("nominal" + (i + 1),
                    nominalAttVals));
        }

        for (int i = 0; i < dataInstance.getNumberNumericFeatures(); i++) {
            attributes.addElement(new Attribute("numeric" + (i + 1)));
        }

        FastVector<String> classLabels = new FastVector<>();
        for (int i = 0; i < dataInstance.getNumberLabels(); i++) {
            classLabels.addElement("class" + (i + 1));
        }
        attributes.addElement(new Attribute("class", classLabels));

        InstancesHeader instancesHeader = new InstancesHeader(
                new Instances(null, attributes, 0));
        instancesHeader.setClassIndex(instancesHeader.numAttributes() - 1);

        return instancesHeader;
    }

    /**
     * convert ClassificationDataInstance to SAMOA Instance
     */
    public static Instance convertClassificationDataInstance(ClassificationDataInstance dataInstance) {
        InstancesHeader header = InstanceUtils.getClassificationInstanceHeader(dataInstance);
        Instance inst = new DenseInstance(header.numAttributes());

        int numNomFeatures = dataInstance.getNumberNominalFeatures();
        int numNumFeatures = dataInstance.getNumberNumericFeatures();

        for (int i = 0; i < numNomFeatures + numNumFeatures; i++) {
            if (i < numNomFeatures) {
                inst.setValue(i, dataInstance.getNominalData()[i]);
            } else {
                inst.setValue(i, dataInstance.getNumericData()[i - numNomFeatures]);
            }
        }

        inst.setDataset(header);
        inst.setClassValue(dataInstance.getTrueLabel());

        return inst;
    }

    /**
     * convert SAMOA Instance to ClassificationDataInstance
     */
    public static ClassificationDataInstance reConvertClassificationDataInstance(
            Instance inst, int numberNominalFeatures, int numberNumericFeatures) {
        double[] nominalDataTmp = Arrays.copyOfRange(inst.toDoubleArray(), 0, numberNominalFeatures);
        int[] nominalData = new int[nominalDataTmp.length];
        for (int j = 0; j < nominalData.length; j++) {
            nominalData[j] = (int) nominalDataTmp[j];
        }

        double[] numericData = Arrays.copyOfRange(
                inst.toDoubleArray(), numberNominalFeatures,
                inst.toDoubleArray().length - 1);

        int[] numValsPerNominal = new int[nominalData.length];
        Arrays.fill(numValsPerNominal, inst.attribute(0).numValues());

        return new ClassificationDataInstance(
                numberNumericFeatures, numericData, numberNominalFeatures,
                numValsPerNominal, nominalData, inst.numClasses(), (int) inst.classValue());
    }

    /**
     * convert ClusterDataInstance to SAMOA Instance
     */
    public static Instance convertClusterDataInstance(ClusterDataInstance dataInstance) {
        Instance inst = new DenseInstance(1.0, dataInstance.getData());
        inst.setDataset(InstanceUtils.getClusterInstanceHeader(dataInstance));
        return new DataPoint(inst, dataInstance.getTimeStamp());
    }

    /**
     * convert SAMOA Instance to ClusterDataInstance
     */
    public static ClusterDataInstance reConvertClusterDataInstance(DataPoint point) {
        double[] data = point.toDoubleArray();
        return new ClusterDataInstance(data.length, point.getTimestamp(), data);
    }
}
