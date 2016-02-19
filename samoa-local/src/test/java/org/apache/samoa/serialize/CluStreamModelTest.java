package org.apache.samoa.serialize;

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

import com.github.javacliparser.ClassOption;
import com.github.javacliparser.FlagOption;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.Option;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.samoa.evaluation.measures.SSQ;
import org.apache.samoa.learners.DataInstance;
import org.apache.samoa.learners.clusterers.CluStreamModel;
import org.apache.samoa.learners.clusterers.ClusterDataInstance;
import org.apache.samoa.moa.core.DataPoint;
import org.apache.samoa.moa.core.SerializeUtils;
import org.apache.samoa.moa.evaluation.MeasureCollection;
import org.apache.samoa.tasks.Task;
import org.apache.samoa.topology.impl.SimpleComponentFactory;
import org.apache.samoa.topology.impl.SimpleEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class CluStreamModelTest extends TestCase {
    private static final String BASE_DIR = "clu";
    private static final String CLISTRING = "ClusteringEvaluation";

    private static final String SUPPRESS_STATUS_OUT_MSG = "Suppress the task status output. Normally it is sent to stderr.";
    private static final String SUPPRESS_RESULT_OUT_MSG = "Suppress the task result output. Normally it is sent to stdout.";
    private static final String STATUS_UPDATE_FREQ_MSG = "Wait time in milliseconds between status updates.";

    private Option[] extraOptions;

    @Before
    public void setUp() {
        FlagOption suppressStatusOutOpt = new FlagOption("suppressStatusOut", 'S', SUPPRESS_STATUS_OUT_MSG);
        FlagOption suppressResultOutOpt = new FlagOption("suppressResultOut", 'R', SUPPRESS_RESULT_OUT_MSG);
        IntOption statusUpdateFreqOpt = new IntOption("statusUpdateFrequency", 'F', STATUS_UPDATE_FREQ_MSG, 1000, 0,
                Integer.MAX_VALUE);
        extraOptions = new Option[] { suppressStatusOutOpt, suppressResultOutOpt, statusUpdateFreqOpt };
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testCluStreamModel() throws Exception {
        FileUtils.forceDeleteOnExit(new File(BASE_DIR));
        FileUtils.forceMkdir(new File(BASE_DIR));

        Task task = ClassOption.cliStringToObject(CLISTRING, Task.class, extraOptions);
        task.setFactory(new SimpleComponentFactory());
        task.init();
        SimpleEngine.submitTopology(task.getTopology());

        File fileModel = new File("clu/clu-model");
        File fileData = new File("clu/clu-data");

        ArrayList<DataPoint> points = (ArrayList<DataPoint>) SerializeUtils.readFromFile(fileData);
        CluStreamModel cluStreamModel  = (CluStreamModel) SerializeUtils.readFromFile(fileModel);

        assert points != null;
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        for (DataPoint point : points) {
            double[] data = point.toDoubleArray();
            DataInstance dataInstance = new ClusterDataInstance(data.length, point.getTimestamp(), data);
            dataInstances.add(dataInstance);
            System.out.println(Arrays.toString(cluStreamModel.predict(dataInstance)));
        }

        assert cluStreamModel != null;
        System.out.println(cluStreamModel.toString());

        MeasureCollection measure = new SSQ();
        double score = cluStreamModel.evaluate(dataInstances, measure);
        System.out.println(score);

        FileUtils.forceDeleteOnExit(new File(BASE_DIR));
    }
}
