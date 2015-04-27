package org.briljantframework.classification.tree;

import org.briljantframework.Briljant;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 2/11/15.
 */
public final class TreeLeaf<T> implements TreeNode<T> {

  private final Vector domain;
  private final DoubleMatrix probabilities;

  public TreeLeaf(Vector domain, DoubleMatrix probabilities) {
    this.domain = domain;
    this.probabilities = probabilities;
  }

  public static <T> TreeLeaf<T> fromExamples(ClassSet classSet) {
    Vector domain = classSet.getDomain();
    DoubleMatrix prob = Briljant.doubleVector(domain.size());
    double totalWeight = classSet.getTotalWeight();
    for (int i = 0; i < domain.size(); i++) {
      String label = domain.getAsString(i);
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

  public DoubleMatrix getProbabilities() {
    return probabilities;
  }

  @Override
  public final DoubleMatrix visit(TreeVisitor<T> visitor, Vector example) {
    return visitor.visitLeaf(this, example);
  }
}
