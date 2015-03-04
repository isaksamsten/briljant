package org.briljantframework.classification;

import static org.briljantframework.matrix.Matrices.argmax;
import static org.briljantframework.matrix.DoubleMatrix.newMatrix;

import java.util.EnumSet;
import java.util.stream.IntStream;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 * Provides sane defaults for a predictor. Sub-classes only have to implement the
 * {@link #estimate(org.briljantframework.vector.Vector)} method to have a sensible default
 * predictor.
 * 
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
    Vector.Builder labels = new StringVector.Builder(x.rows());
    IntStream.range(0, x.rows()).parallel().forEach(i -> {
      labels.set(i, predict(x.getRecord(i)));
    });
    return labels.build();
  }

  @Override
  public Value predict(Vector row) {
    return classes.getAsValue(argmax(estimate(row)));
  }

  @Override
  public DoubleMatrix estimate(DataFrame x) {
    DoubleMatrix estimations = newMatrix(x.rows(), getClasses().size());
    IntStream.range(0, x.rows()).parallel().forEach(i -> {
      estimations.setRow(i, estimate(x.getRecord(i)));
    });
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
