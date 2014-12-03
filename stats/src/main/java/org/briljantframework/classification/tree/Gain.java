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

package org.briljantframework.classification.tree;

/**
 * Created by Isak Karlsson on 08/09/14.
 */
public class Gain {

  /**
   * The constant GINI.
   */
  public static final Gain GINI = Gain.with(Gini.INSTANCE);

  /**
   * The constant INFO.
   */
  public static final Gain INFO = Gain.with(Entropy.getInstance());

  private final Impurity impurity;

  /**
   * Instantiates a new Gain.
   *
   * @param impurity the impurity
   */
  private Gain(Impurity impurity) {
    this.impurity = impurity;
  }

  /**
   * With gain.
   *
   * @param impurity the impurity
   * @return the gain
   */
  public static Gain with(Impurity impurity) {
    return new Gain(impurity);
  }

  /**
   * Gets impurity.
   *
   * @return the impurity
   */
  public Impurity getImpurity() {
    return impurity;
  }

  /**
   * Calculate double.
   *
   * @param before the before
   * @param split the split
   * @return the double
   */
  public double calculate(Examples before, Tree.Split split) {
    return calculate(split.getLeft().getTotalWeight(), split.getLeft().getRelativeFrequencies(),
        split.getRight().getTotalWeight(), split.getRight().getRelativeFrequencies());
  }

  /**
   * Calculate double.
   *
   * @param leftWeight the left weight
   * @param left the left
   * @param rightWeight the right weight
   * @param right the right
   * @return the double
   */
  public double calculate(double leftWeight, double[] left, double rightWeight, double[] right) {
    return calculate(leftWeight, left, rightWeight, right, null);
  }

  /**
   * Calculate the gain from the vectors left and right, wighted by their respective weight.
   * {@code leftRight} is used to pass out-parameters (this is a hack :)) consisting of the error
   * 
   * @param leftWeight
   * @param left
   * @param rightWeight
   * @param right
   * @param leftRight
   * @return
   */
  public double calculate(double leftWeight, double[] left, double rightWeight, double[] right,
      double[] leftRight) {
    double totalWeight = leftWeight + rightWeight;
    if (leftWeight > 0) {
      leftWeight = (leftWeight / totalWeight) * impurity.impurity(left);// * leftWeight;
    } else {
      leftWeight = 0.0;
    }

    if (rightWeight > 0) {
      rightWeight = (rightWeight / totalWeight) * impurity.impurity(right);// * rightWeight;
    } else {
      rightWeight = 0.0;
    }

    if (leftRight != null && leftRight.length == 2) {
      leftRight[0] = leftWeight;
      leftRight[1] = rightWeight;
    }

    return leftWeight + rightWeight;
  }


  /**
   * Calculate double.
   *
   * @param leftWeight the left weight
   * @param rightWeight the right weight
   * @param leftRelativeFrequencies the left relative frequencies
   * @param rightRelativeFrequencies the right relative frequencies
   * @return double double
   */
  public double calculate(double leftWeight, double rightWeight, double[] leftRelativeFrequencies,
      double[] rightRelativeFrequencies) {
    return (leftWeight * impurity.impurity(leftRelativeFrequencies))
        + (rightWeight * impurity.impurity(rightRelativeFrequencies));
  }

  /**
   * Is better.
   *
   * @param candidate the impurity
   * @param best the so far
   * @return the boolean
   */
  public boolean isBetter(double candidate, double best) {
    return candidate < best;
  }

}
