package org.briljantframework.classification.tree;

import org.briljantframework.Bj;
import org.briljantframework.matrix.DoubleArray;
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
