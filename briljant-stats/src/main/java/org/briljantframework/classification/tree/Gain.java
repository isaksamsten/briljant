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

import org.briljantframework.array.DoubleArray;

/**
 * Created by Isak Karlsson on 08/09/14.
 */
public class Gain {

  /**
   * The constant GINI.
   */
  public static final Gain GINI = Gain.with(Gini.getInstance());

  /**
   * The constant INFO.
   */
  public static final Gain INFO = Gain.with(Entropy.getInstance());

  private final Impurity impurity;

  private Gain(Impurity impurity) {
    this.impurity = impurity;
  }

  public static Gain with(Impurity impurity) {
    return new Gain(impurity);
  }

  public Impurity getImpurity() {
    return impurity;
  }

  public double compute(ClassSet before, TreeSplit<?> split) {
    ClassSet left = split.getLeft();
    ClassSet right = split.getRight();
    return compute(left.getTotalWeight(), left.getRelativeFrequencies(), right.getTotalWeight(),
        right.getRelativeFrequencies());
  }

  public double compute(double leftWeight, DoubleArray left, double rightWeight, DoubleArray right) {
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

    return leftWeight + rightWeight;
  }
}
