package org.briljantframework.classification;

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
}
