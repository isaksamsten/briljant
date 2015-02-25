package org.briljantframework.classification;

import static org.briljantframework.matrix.Matrices.argmax;
import static org.briljantframework.matrix.Matrices.newDoubleMatrix;

import java.util.EnumSet;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractPredictor implements Predictor {

  private final Vector classes;

  protected AbstractPredictor(Vector classes) {
    this.classes = Preconditions.checkNotNull(classes);
  }

  @Override
  public final Vector getClasses() {
    return classes;
  }

  @Override
  public Vector predict(DataFrame x) {
    long time = System.nanoTime();
    Vector.Builder labels = new StringVector.Builder(x.rows());
    for (int i = 0; i < x.rows(); i++) {
      labels.set(i, predict(x.getRecord(i)));
    }
    System.out.println((System.nanoTime() - time) / 1e6);
    return labels.build();
  }

  @Override
  public Value predict(Vector row) {
    return classes.getAsValue(argmax(estimate(row)));
  }

  @Override
  public DoubleMatrix estimate(DataFrame x) {
    long time = System.nanoTime();
    DoubleMatrix estimations = newDoubleMatrix(x.rows(), getClasses().size());
    for (int i = 0; i < x.rows(); i++) {
      estimations.setRow(i, estimate(x.getRecord(i)));
    }
    System.out.println((System.nanoTime() - time) / 1e6);
    return estimations;
  }

  @Override
  public EnumSet<Characteristics> getCharacteristics() {
    return EnumSet.noneOf(Characteristics.class);
  }

  @Override
  public void evaluation(EvaluationContext ctx) {

  }
}
