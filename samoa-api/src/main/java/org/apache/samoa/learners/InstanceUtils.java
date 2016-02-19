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
import org.apache.samoa.learners.classifiers.NominalDataInstance;
import org.apache.samoa.learners.classifiers.NumericDataInstance;
import org.apache.samoa.learners.clusterers.ClusterDataInstance;
import org.apache.samoa.moa.core.DataPoint;
import org.apache.samoa.moa.core.FastVector;

import java.util.ArrayList;

public class InstanceUtils {
    static private InstancesHeader getNumericInstanceHeader(NumericDataInstance numericDataInstance) {
        FastVector<Attribute> attributes = new FastVector<>();

        for (int i = 0; i < numericDataInstance.getNumNumerics(); i++) {
            attributes.addElement(new Attribute("numeric" + (i + 1)));
        }

        FastVector<String> classLabels = new FastVector<>();
        for (int i = 0; i < numericDataInstance.getNumClasses(); i++) {
            classLabels.addElement("class" + (i + 1));
        }
        attributes.addElement(new Attribute("class", classLabels));

        InstancesHeader instancesHeader = new InstancesHeader(
                new Instances(null, attributes, 0));
        instancesHeader.setClassIndex(instancesHeader.numAttributes() - 1);

        return instancesHeader;
    }

    static private Instance convertNumericInstance(NumericDataInstance numericDataInstance) {
        InstancesHeader header = InstanceUtils.getNumericInstanceHeader(numericDataInstance);
        Instance inst = new DenseInstance(header.numAttributes());

        for (int i = 0; i < numericDataInstance.getNumNumerics(); i++) {
            inst.setValue(i, numericDataInstance.getData()[i]);
        }

        inst.setDataset(header);
        inst.setClassValue(numericDataInstance.getTrueClass());

        return inst;
    }

    static private InstancesHeader getNominalInstanceHeader(NominalDataInstance nominalDataInstance) {
        FastVector<Attribute> attributes = new FastVector<>();

        for (int i = 0; i < nominalDataInstance.getNumNominals(); i++) {
            FastVector<String> nominalAttVals = new FastVector<>();
            for (int j = 0; j < nominalDataInstance.getNumValsPerNominal()[i]; j++) {
                nominalAttVals.addElement("value" + (j + 1));
            }
            attributes.addElement(new Attribute("nominal" + (i + 1),
                    nominalAttVals));
        }

        FastVector<String> classLabels = new FastVector<>();
        for (int i = 0; i < nominalDataInstance.getNumClasses(); i++) {
            classLabels.addElement("class" + (i + 1));
        }
        attributes.addElement(new Attribute("class", classLabels));

        InstancesHeader instancesHeader = new InstancesHeader(
                new Instances(null, attributes, 0));
        instancesHeader.setClassIndex(instancesHeader.numAttributes() - 1);

        return instancesHeader;
    }

    static private Instance convertNominalInstance(NominalDataInstance nominalDataInstance) {
        InstancesHeader header = InstanceUtils.getNominalInstanceHeader(nominalDataInstance);
        Instance inst = new DenseInstance(header.numAttributes());

        for (int i = 0; i < nominalDataInstance.getNumNominals(); i++) {
            inst.setValue(i, nominalDataInstance.getData()[i]);
        }

        inst.setDataset(header);
        inst.setClassValue(nominalDataInstance.getTrueClass());

        return inst;
    }

    static private InstancesHeader getClusterInstanceHeader(ClusterDataInstance clusterDataInstance) {
        ArrayList<Attribute> attributes = new ArrayList<>();

        for (int i = 0; i < clusterDataInstance.getNumAtts(); i++) {
            attributes.add(new Attribute("att" + (i + 1)));
        }

        // attributes.add(new Attribute("class", null));

        InstancesHeader instancesHeader = new InstancesHeader(
                new Instances(null, attributes, 0));
        instancesHeader.setClassIndex(instancesHeader.numAttributes() - 1);

        return instancesHeader;
    }

    static private Instance convertClusterInstance(ClusterDataInstance clusterDataInstance) {
        Instance inst = new DenseInstance(1.0, clusterDataInstance.getData());
        inst.setDataset(InstanceUtils.getClusterInstanceHeader(clusterDataInstance));
        return new DataPoint(inst, clusterDataInstance.getTimeStamp());
    }

    static public Instance convertToSamoaInstance(DataInstance dataInstance) {
        if (dataInstance instanceof NumericDataInstance) {
            return InstanceUtils.convertNumericInstance((NumericDataInstance) dataInstance);
        } else if (dataInstance instanceof NominalDataInstance) {
            return InstanceUtils.convertNominalInstance((NominalDataInstance) dataInstance);
        } else if (dataInstance instanceof ClusterDataInstance) {
            return InstanceUtils.convertClusterInstance((ClusterDataInstance) dataInstance);
        } else {
            throw new Error("Invalid input class!");
        }
    }
}
