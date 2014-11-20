package org.briljantframework.learning.tree;

/**
 * Created by Isak Karlsson on 08/09/14.
 */
public class Entropy implements Impurity {

    private static final Entropy instance = new Entropy();

    private static final double LOG_2 = Math.log(2);

    private Entropy() {
    }

    /**
     * The constant instance.
     */
    public static Entropy getInstance() {
        return instance;
    }

    /**
     * @param values the values
     * @return the impurity
     * @see org.briljantframework.learning.tree.Impurity#impurity(double[])
     */
    @Override
    public double impurity(double[] values) {
        double entropy = 0;
        for (double value : values) {
            if (value != 0) {
                entropy += value * (Math.log(value) / LOG_2);
            }
        }
        return -1 * entropy;
    }
}
