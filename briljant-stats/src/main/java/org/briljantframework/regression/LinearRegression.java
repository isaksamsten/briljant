/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.regression;

import com.google.common.base.Preconditions;

import org.briljantframework.classification.AbstractPredictor;
import org.briljantframework.classification.Classifier;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.linalg.LinearAlgebra;
import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 29/09/14.
 */
public class LinearRegression implements Classifier {

  private LinearRegression() {
  }

  /**
   * Create linear regression.
   *
   * @return the linear regression
   */
  public static LinearRegression create() {
    return new LinearRegression();
  }

  @Override
  public Model fit(DataFrame x, Vector y) {
    Preconditions.checkArgument(x.rows() == y.size());

    DoubleArray yMatrix = y.toMatrix().asDouble();
    return new Model(LinearAlgebra.leastLinearSquares(x.toMatrix().asDouble(), yMatrix));
  }

  /**
   * The type Model.
   */
  public static final class Model extends AbstractPredictor {

    private final DoubleArray theta;

    /**
     * Instantiates a new Model.
     *
     * @param theta the theta
     */
    public Model(DoubleArray theta) {
      super(null);
      this.theta = theta;
    }

    /**
     * Gets theta.
     *
     * @return the theta
     */
    public DoubleArray getTheta() {
      return theta;
    }

    @Override
    public DoubleArray estimate(Vector record) {
      throw new UnsupportedOperationException();
    }
  }
}
