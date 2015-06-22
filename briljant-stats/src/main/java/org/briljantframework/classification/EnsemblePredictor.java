package org.briljantframework.classification;

import org.briljantframework.matrix.BitArray;

import java.util.List;

/**
 * @author Isak Karlsson
 */
public interface EnsemblePredictor extends Predictor {
  /**
   * Shape = {@code [no training samples, no members]}, if element e<sup>i,j</sup> is {@code true}
   * the i:th training sample is out of the j:th members training sample.
   *
   * @return the out of bag indicator matrix
   */
  public BitArray getOobIndicator();

  List<Predictor> getPredictors();
}
