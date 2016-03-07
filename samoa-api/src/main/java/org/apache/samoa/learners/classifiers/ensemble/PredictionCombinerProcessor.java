package org.apache.samoa.learners.classifiers.ensemble;

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

/**
 * License
 */
import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.samoa.core.ContentEvent;
import org.apache.samoa.core.Processor;
import org.apache.samoa.learners.ClassificationModelContentEvent;
import org.apache.samoa.learners.ResultContentEvent;
import org.apache.samoa.learners.classifiers.ClassificationModel;
import org.apache.samoa.moa.core.DoubleVector;
import org.apache.samoa.moa.core.SerializeUtils;
import org.apache.samoa.topology.Stream;

/**
 * Combines predictions coming from an ensemble. Equivalent to a majority-vote classifier.
 */
public class PredictionCombinerProcessor implements Processor {

  private static final long serialVersionUID = -1606045723451191132L;

  /**
   * The ensemble size.
   */
  protected int ensembleSize;

  /**
   * The output stream.
   */
  protected Stream outputStream;

  /**
   * Sets the output stream.
   * 
   * @param stream
   *          the new output stream
   */
  public void setOutputStream(Stream stream) {
    outputStream = stream;
  }

  /**
   * Gets the output stream.
   * 
   * @return the output stream
   */
  public Stream getOutputStream() {
    return outputStream;
  }

  /**
   * Gets the size ensemble.
   * 
   * @return the ensembleSize
   */
  public int getEnsembleSize() {
    return ensembleSize;
  }

  /**
   * Sets the size ensemble.
   * 
   * @param ensembleSize
   *          the new size ensemble
   */
  public void setEnsembleSize(int ensembleSize) {
    this.ensembleSize = ensembleSize;
  }

  protected Map<Integer, Integer> mapCountsforInstanceReceived;

  protected Map<Integer, DoubleVector> mapVotesforInstanceReceived;

  protected Map<Long, Integer> mapCountsforModelReceived; // for serialize

  protected Map<Long, ArrayList<ClassificationModel>> mapModelListforModelReceived; // for serialize

  protected Map<Long, ArrayList<Double>> mapModelWeightListforModelReceived; // for serialize
  /**
   * On event.
   * 
   * @param event
   *          the event
   * @return true, if successful
   */
  public boolean process(ContentEvent event) {
    // for serialize
    if (event instanceof ClassificationModelContentEvent) {
      return this.processModel((ClassificationModelContentEvent) event);
    }

    ResultContentEvent inEvent = (ResultContentEvent) event;
    double[] prediction = inEvent.getClassVotes();
    int instanceIndex = (int) inEvent.getInstanceIndex();

    addStatisticsForInstanceReceived(instanceIndex, inEvent.getClassifierIndex(), prediction, 1);
    if (hasAllVotesArrivedInstance(instanceIndex)) {
      DoubleVector combinedVote = this.mapVotesforInstanceReceived.get(instanceIndex);
      if (combinedVote == null) {
        combinedVote = new DoubleVector(new double[inEvent.getInstance().numClasses()]);
      }
      ResultContentEvent outContentEvent = new ResultContentEvent(inEvent.getInstanceIndex(), inEvent.getInstance(),
          inEvent.getClassId(), combinedVote.getArrayCopy(), inEvent.isLastEvent());
      outContentEvent.setEvaluationIndex(inEvent.getEvaluationIndex());
      outputStream.put(outContentEvent);
      clearStatisticsInstance(instanceIndex);
      return true;
    }
    return false;

  }

  // for serialize
  protected boolean processModel(ClassificationModelContentEvent event) {
    ClassificationModel model = event.getModel();
    long modelIndex = event.getModelIndex();
    long instanceIndex = event.getInstanceIndex();
    int classifierIndex = event.getClassifierIndex();

    this.addStatisticsForModelReceived(modelIndex, classifierIndex, model, 1);
    if (hasAllVotesArrivedModel(modelIndex)) {
      EnsembleModel baggingModel = new EnsembleModel(
              this.mapModelListforModelReceived.get(modelIndex),
              this.mapModelWeightListforModelReceived.get(modelIndex));

      File fileModel = new File("bagging/bagging-model-" + modelIndex);
      try {
        SerializeUtils.writeToFile(fileModel, baggingModel);
      } catch (IOException e) {
        e.printStackTrace();
      }

      // test if the prediction using serialized model equals to original model
      DoubleVector combinedVote = this.mapVotesforInstanceReceived.get((int) instanceIndex);
      double[] prediction = combinedVote.getArrayCopy();
      System.out.println("### bagging model " + modelIndex + " ###");
      System.out.println("### predict: " + Arrays.toString(prediction));

      return true;
    }
    return false;
  }

  @Override
  public void onCreate(int id) {
    this.reset();
  }

  public void reset() {
  }

  /*
   * (non-Javadoc)
   * @see samoa.core.Processor#newProcessor(samoa.core.Processor)
   */
  @Override
  public Processor newProcessor(Processor sourceProcessor) {
    PredictionCombinerProcessor newProcessor = new PredictionCombinerProcessor();
    PredictionCombinerProcessor originProcessor = (PredictionCombinerProcessor) sourceProcessor;
    if (originProcessor.getOutputStream() != null) {
      newProcessor.setOutputStream(originProcessor.getOutputStream());
    }
    newProcessor.setEnsembleSize(originProcessor.getEnsembleSize());
    return newProcessor;
  }

  protected void addStatisticsForInstanceReceived(int instanceIndex, int classifierIndex, double[] prediction, int add) {
    if (this.mapCountsforInstanceReceived == null) {
      this.mapCountsforInstanceReceived = new HashMap<>();
      this.mapVotesforInstanceReceived = new HashMap<>();
    }
    DoubleVector vote = new DoubleVector(prediction);
    if (vote.sumOfValues() > 0.0) {
      vote.normalize();
      DoubleVector combinedVote = this.mapVotesforInstanceReceived.get(instanceIndex);
      if (combinedVote == null) {
        combinedVote = new DoubleVector();
      }
      vote.scaleValues(getEnsembleMemberWeight(classifierIndex));
      combinedVote.addValues(vote);

      this.mapVotesforInstanceReceived.put(instanceIndex, combinedVote);
    }
    Integer count = this.mapCountsforInstanceReceived.get(instanceIndex);
    if (count == null) {
      count = 0;
    }
    this.mapCountsforInstanceReceived.put(instanceIndex, count + add);
  }

  //for serialize
  protected void addStatisticsForModelReceived(long modelIndex, int classifierIndex, ClassificationModel model, int add) {
    if (this.mapCountsforModelReceived == null) {
      this.mapCountsforModelReceived = new HashMap<>();
      this.mapModelListforModelReceived = new HashMap<>();
      this.mapModelWeightListforModelReceived = new HashMap<>();
    }
    ArrayList<ClassificationModel> modelList = this.mapModelListforModelReceived.get(modelIndex);
    if (modelList == null) {
      modelList = new ArrayList<>();
    }
    modelList.add(model);
    this.mapModelListforModelReceived.put(modelIndex, modelList);

    ArrayList<Double> modelWeightList = this.mapModelWeightListforModelReceived.get(modelIndex);
    if (modelWeightList == null) {
      modelWeightList = new ArrayList<>();
    }
    modelWeightList.add(getEnsembleMemberWeight(classifierIndex));
    this.mapModelWeightListforModelReceived.put(modelIndex, modelWeightList);

    Integer count = this.mapCountsforModelReceived.get(modelIndex);
    if (count == null) {
      count = 0;
    }
    this.mapCountsforModelReceived.put(modelIndex, count + add);
  }

  protected boolean hasAllVotesArrivedInstance(int instanceIndex) {
    return (this.mapCountsforInstanceReceived.get(instanceIndex) == this.ensembleSize);
  }

  // for serialize
  protected boolean hasAllVotesArrivedModel(long modelIndex) {
    return (this.mapCountsforModelReceived.get(modelIndex) == this.ensembleSize);
  }

  protected void clearStatisticsInstance(int instanceIndex) {
    this.mapCountsforInstanceReceived.remove(instanceIndex);
    this.mapVotesforInstanceReceived.remove(instanceIndex);
  }

  protected double getEnsembleMemberWeight(int i) {
    return 1.0;
  }

}
