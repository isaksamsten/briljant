/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.classification.tree;

import org.briljantframework.Bj;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 2/11/15.
 */
public final class TreeLeaf<T> implements TreeNode<T> {

  private final Vector domain;
  private final DoubleArray probabilities;

  public TreeLeaf(Vector domain, DoubleArray probabilities) {
    this.domain = domain;
    this.probabilities = probabilities;
  }

  public static <T> TreeLeaf<T> fromExamples(ClassSet classSet) {
    Vector domain = classSet.getDomain();
    DoubleArray prob = Bj.doubleArray(domain.size());
    double totalWeight = classSet.getTotalWeight();
    for (int i = 0; i < domain.size(); i++) {
      Object label = domain.get(Object.class, i);
      ClassSet.Sample sample = classSet.get(label);
      if (sample == null) {
        prob.set(i, 0);
      } else {
        prob.set(i, sample.getWeight() / totalWeight);
      }
    }
    return new TreeLeaf<>(domain, prob);
  }

  public Vector getDomain() {
    return domain;
  }

  public DoubleArray getProbabilities() {
    return probabilities;
  }

  @Override
  public final DoubleArray visit(TreeVisitor<T> visitor, Vector example) {
    return visitor.visitLeaf(this, example);
  }
}
