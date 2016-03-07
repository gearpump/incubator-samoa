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
import org.apache.samoa.learners.InstanceUtils;
import org.apache.samoa.learners.classifiers.ClassificationDataInstance;
import org.apache.samoa.learners.classifiers.ensemble.EnsembleModel;
import org.apache.samoa.moa.core.SerializeUtils;
import org.apache.samoa.tasks.Task;
import org.apache.samoa.topology.impl.SimpleComponentFactory;
import org.apache.samoa.topology.impl.SimpleEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class EnsembleModelTest extends TestCase {
    private static final String ENS_BASE_DIR = "bagging";
    private static final String BASE_DIR = "vht";
    private static final int NUM_MODEL_IN_DIR = 10;

    private static final int numberNumericFeatures = 10;
    private static final int numberNominalFeatures = 10;
    private static final String CLISTRING =
            "PrequentialEvaluation -i 1000000 -f 100000 " +
                    "-l (classifiers.ensemble.Bagging -s 10 -l (classifiers.trees.VerticalHoeffdingTree)) " +
                    "-s (generators.RandomTreeGenerator -c 2 -o " +
                    numberNominalFeatures + " -u " + numberNumericFeatures +")";

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
    public void testEnsembleModel() throws Exception {
        FileUtils.forceDeleteOnExit(new File(BASE_DIR));
        FileUtils.forceMkdir(new File(BASE_DIR));
        FileUtils.forceDeleteOnExit(new File(ENS_BASE_DIR));
        FileUtils.forceMkdir(new File(ENS_BASE_DIR));

        Task task = ClassOption.cliStringToObject(CLISTRING, Task.class, extraOptions);
        task.setFactory(new SimpleComponentFactory());
        task.init();
        SimpleEngine.submitTopology(task.getTopology());

        for (int i = 0; i < NUM_MODEL_IN_DIR; i++) {
            File fileModel = new File(ENS_BASE_DIR + "/bagging-model-" + i);
            File fileData = new File(BASE_DIR + "/vht-data-0-" + i);

            EnsembleModel em = (EnsembleModel) SerializeUtils.readFromFile(fileModel);
            Instance inst = (Instance) SerializeUtils.readFromFile(fileData);
            System.out.println("=== model: " + i + " ===");

            ClassificationDataInstance dataInstance =
                    InstanceUtils.reConvertClassificationDataInstance(inst,numberNominalFeatures, numberNumericFeatures);

            System.out.println(Arrays.toString(em.predict(dataInstance)));
            System.out.println("true predict: " + (int) inst.classValue());
            System.out.println("predict: " + em.evaluate(dataInstance));
        }

        FileUtils.forceDeleteOnExit(new File(BASE_DIR));
        FileUtils.forceDeleteOnExit(new File(ENS_BASE_DIR));
    }
}
