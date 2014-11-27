package org.briljantframework.learning;

import com.google.common.base.Preconditions;

import java.util.*;

/**
 * Created by isak on 02/10/14.
 */
public class Predictions implements Iterable<Prediction> {

    private final List<Prediction> predictions;
    private final Set<String> labels;

    /**
     * Instantiates a new Predictions.
     *
     * @param predictions the predictions
     * @param labels
     */
    private Predictions(List<Prediction> predictions, Set<String> labels) {
        this.predictions = Collections.unmodifiableList(predictions);
        this.labels = Collections.unmodifiableSet(labels);
    }

    /**
     * Create predictions.
     *
     * @param predictions the predictions
     * @return the predictions
     */
    public static Predictions create(List<Prediction> predictions) {
        Preconditions.checkNotNull(predictions);
        Preconditions.checkArgument(predictions.size() > 0);

        ArrayList<Prediction> copy = new ArrayList<>(predictions.size());
        Set<String> labels = new HashSet<>();
        for (Prediction p : predictions) {
            copy.add(p);
            labels.addAll(p.getTargets());
        }
        return new Predictions(copy, labels);
    }

    /**
     * Size int.
     *
     * @return the int
     */
    public int size() {
        return predictions.size();
    }

    /**
     * Is empty.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return predictions.isEmpty();
    }

    /**
     * Contains boolean.
     *
     * @param o the o
     * @return the boolean
     */
    public boolean contains(Object o) {
        return predictions.contains(o);
    }

    /**
     * Contains all.
     *
     * @param c the c
     * @return the boolean
     */
    public boolean containsAll(Collection<?> c) {
        return predictions.containsAll(c);
    }

    /**
     * Gets labels.
     *
     * @return the labels
     */
    public Set<String> getLabels() {
        return labels;
    }

    /**
     * Get prediction.
     *
     * @param index the index
     * @return the prediction
     */
    public Prediction get(int index) {
        return predictions.get(index);
    }

    @Override
    public Iterator<Prediction> iterator() {
        return predictions.iterator();
    }
}
