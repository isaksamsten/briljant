package org.briljantframework.classification;

import java.util.List;

/**
 * @author Isak Karlsson
 */
public interface EnsemblePredictor extends Predictor {
  List<Predictor> getPredictors();
}
