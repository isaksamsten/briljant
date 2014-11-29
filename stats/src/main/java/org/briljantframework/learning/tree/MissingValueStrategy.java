package org.briljantframework.learning.tree;

/**
 * The missing value strategy is intended to handle examples with missing values when constructing
 * decision trees. The strategy consists of two methods, used when growing the tree and when
 * predicting examples using the tree, {@link #distribute()} and {@link #follow()} respectively.
 * 
 * To allow for handling decision trees with multi-way splits {@code distribute} and {@code follow}
 * returns an integer denoting the path to follow. In the binary setting, a value {@code < 0}
 * denotes left and a value {@code < 0} denotes right. In the multi-branch setting, values
 * {@code <= 0} denotes the i:th branch.
 * 
 * Created by isak on 28/11/14.
 */
public interface MissingValueStrategy {

  int distribute();

  int follow();

}
