package org.briljantframework.classification;

import com.google.common.base.Preconditions;

import org.briljantframework.Bj;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.GenericVector;
import org.briljantframework.vector.Vector;

import java.util.EnumSet;
import java.util.stream.IntStream;

import static org.briljantframework.Bj.argmax;

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
    // This is really only safe since Builder is initialized with a size i.e. filled with NA
    Vector.Builder labels = new GenericVector.Builder(Object.class, x.rows());
    IntStream.range(0, x.rows()).parallel().forEach(
        i -> labels.set(i, predict(x.getRecord(i)))
    );
    return labels.build();
  }

  @Override
  public Object predict(Vector record) {
    return getClasses().get(Object.class, argmax(estimate(record)));
  }

  @Override
  public DoubleMatrix estimate(DataFrame x) {
    DoubleMatrix estimations = Bj.doubleMatrix(x.rows(), getClasses().size());
    IntStream.range(0, x.rows()).parallel().forEach(
        i -> estimations.setRow(i, estimate(x.getRecord(i)))
    );
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
