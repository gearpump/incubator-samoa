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
import org.apache.samoa.instances.Instance;
import org.apache.samoa.learners.DataInstance;
import org.apache.samoa.learners.classifiers.NumericDataInstance;
import org.apache.samoa.learners.classifiers.rules.AMRulesModel;
import org.apache.samoa.moa.core.SerializeUtils;
import org.apache.samoa.tasks.Task;
import org.apache.samoa.topology.impl.SimpleComponentFactory;
import org.apache.samoa.topology.impl.SimpleEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class AMRulesModelTest extends TestCase {
    private static final String BASE_DIR = "amr";
    private static final int NUM_MODEL_IN_DIR = 10;

    private static final String CLISTRING =
            "PrequentialEvaluation " +
                    "-l (org.apache.samoa.learners.classifiers.rules.VerticalAMRulesRegressor -p 4) " +
                    "-s (generators.RandomTreeGenerator -c 2 -o 0 -u 10)";

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
    public void testAMRulesModel() throws Exception {
        FileUtils.forceDeleteOnExit(new File(BASE_DIR));
        FileUtils.forceMkdir(new File(BASE_DIR));

        Task task = ClassOption.cliStringToObject(CLISTRING, Task.class, extraOptions);
        task.setFactory(new SimpleComponentFactory());
        task.init();
        SimpleEngine.submitTopology(task.getTopology());

        for (int i = 0; i < NUM_MODEL_IN_DIR; i++) {
            File fileModel = new File(BASE_DIR + "/amr-model-0-" + i);
            File fileData = new File(BASE_DIR + "/amr-data-0-" + i);

            AMRulesModel amRulesModel = (AMRulesModel) SerializeUtils.readFromFile(fileModel);
            Instance inst = (Instance) SerializeUtils.readFromFile(fileData);
            System.out.println("=== model: " + i + " ===");

            double[] data = Arrays.copyOfRange(inst.toDoubleArray(), 0, inst.toDoubleArray().length - 1);

            DataInstance dataInstance = new NumericDataInstance(data.length,
                    inst.numClasses(), inst.classValue(), data);

            System.out.println(Arrays.toString(amRulesModel.predict(dataInstance)));
            System.out.println("true predict: " + (int) inst.classValue());
        }

        FileUtils.forceDeleteOnExit(new File(BASE_DIR));
    }
}
