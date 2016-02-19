package org.apache.samoa.learners.classifiers.rules;

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

import org.apache.samoa.instances.Instance;
import org.apache.samoa.learners.DataInstance;
import org.apache.samoa.learners.InstanceUtils;
import org.apache.samoa.learners.Model;
import org.apache.samoa.learners.classifiers.rules.common.ActiveRule;
import org.apache.samoa.learners.classifiers.rules.common.PassiveRule;
import org.apache.samoa.moa.classifiers.rules.core.voting.ErrorWeightedVote;

import java.util.List;

public class AMRulesModel implements Model {
    private List<PassiveRule> ruleSet;
    private ActiveRule defaultRule;
    private ErrorWeightedVote errorWeightedVote;
    private boolean unorderedRules;

    public AMRulesModel(ActiveRule defaultRule, List<PassiveRule> ruleSet,
                        ErrorWeightedVote errorWeightedVote, boolean unorderedRules) {
        this.defaultRule = defaultRule;
        this.ruleSet = ruleSet;
        this.errorWeightedVote = errorWeightedVote;
        this.unorderedRules = unorderedRules;
    }

    @Override
    public double[] predict(DataInstance dataInstance) {
        Instance inst = InstanceUtils.convertToSamoaInstance(dataInstance);
        double[] prediction;
        boolean predictionCovered = false;

        for (PassiveRule rule : ruleSet) {
            if (rule.isCovering(inst)) {
                predictionCovered = true;
                double[] vote = rule.getPrediction(inst);
                double error = rule.getCurrentError();
                errorWeightedVote.addVote(vote, error);
                if (!unorderedRules) {
                    break;
                }
            }
        }

        if (predictionCovered) {
            prediction = errorWeightedVote.computeWeightedVote();
        } else {
            prediction = defaultRule.getPrediction(inst);
        }
        return prediction;
    }
}
