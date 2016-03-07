package org.apache.samoa.learners;

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

import org.apache.samoa.core.ContentEvent;
import org.apache.samoa.learners.classifiers.ClassificationModel;

final public class ClassificationModelContentEvent implements ContentEvent {
    final private boolean isLast;
    private ClassificationModel model;
    private long modelIndex;
    private long instanceIndex;
    private int classifierIndex;
    private int evaluationIndex;

    public ClassificationModelContentEvent() {
        this.isLast = false;
    }

    public ClassificationModelContentEvent(boolean isLast) {
        this.isLast = isLast;
    }

    public ClassificationModelContentEvent(boolean isLast, ClassificationModel model, long modelIndex, long instanceIndex,
                                           int classifierIndex, int evaluationIndex) {
        this.isLast = isLast;
        this.model = model;
        this.modelIndex = modelIndex;
        this.instanceIndex = instanceIndex;
        this.classifierIndex = classifierIndex;
        this.evaluationIndex = evaluationIndex;
    }

    @Override
    public String getKey() {
        return Long.toString(evaluationIndex % 100);
    }

    @Override
    public void setKey(String str) {
        evaluationIndex = Integer.parseInt(str);
    }

    @Override
    public boolean isLastEvent() {
        return isLast;
    }

    public long getModelIndex() {
        return modelIndex;
    }

    public void setModelIndex(long modelIndex) {
        this.modelIndex = modelIndex;
    }

    public ClassificationModel getModel() {
        return model;
    }

    public void setModel(ClassificationModel model) {
        this.model = model;
    }

    public int getClassifierIndex() {
        return classifierIndex;
    }

    public void setClassifierIndex(int classifierIndex) {
        this.classifierIndex = classifierIndex;
    }

    public int getEvaluationIndex() {
        return evaluationIndex;
    }

    public void setEvaluationIndex(int evaluationIndex) {
        this.evaluationIndex = evaluationIndex;
    }

    public long getInstanceIndex() {
        return instanceIndex;
    }

    public void setInstanceIndex(long instanceIndex) {
        this.instanceIndex = instanceIndex;
    }
}
