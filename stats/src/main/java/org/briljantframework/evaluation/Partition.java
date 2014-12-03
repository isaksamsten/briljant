package org.briljantframework.evaluation;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * Stupid POJO for holding training / validation set
 * 
 * Created by Isak Karlsson on 01/12/14.
 */
public class Partition {

  private final DataFrame trainingX, validationX;
  private final Vector trainingY, validationY;

  public Partition(DataFrame trainingX, DataFrame validationX, Vector trainingY, Vector validationY) {
    this.trainingX = trainingX;
    this.validationX = validationX;
    this.trainingY = trainingY;
    this.validationY = validationY;
  }

  /**
   * Get the data intended for training
   * 
   * @return the training data
   */
  public DataFrame getTrainingData() {
    return trainingX;
  }

  /**
   * Get the target intended for training
   * 
   * @return the training target
   */
  public Vector getTrainingTarget() {
    return trainingY;
  }

  /**
   * Get the data intended for validation
   * 
   * @return the validation data
   */
  public DataFrame getValidationData() {
    return validationX;
  }

  /**
   * Get the target intended for validation
   * 
   * @return the validation target
   */
  public Vector getValidationTarget() {
    return validationY;
  }
}
