package org.briljantframework.learning.evaluation.tune;

import org.briljantframework.learning.lazy.KNearestNeighbors;
import org.junit.Test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.briljantframework.learning.evaluation.tune.Updaters.enumeration;
import static org.briljantframework.learning.evaluation.tune.Updaters.range;
import static org.junit.Assert.assertEquals;

public class UpdatersTest {

    @Test
    public void testRange() throws Exception {
        KNearestNeighbors.Builder knnBuilder = KNearestNeighbors.withNeighbors(1);
        Updater<KNearestNeighbors.Builder> updater = range("n", KNearestNeighbors.Builder::withNeighbors, 0, 10, 2);
        while (updater.hasUpdate()) {
            updater.update(knnBuilder);
        }
        assertEquals(10, knnBuilder.neighbors);
    }

    @Test
    public void testOptions() throws Exception {
        KNearestNeighbors.Builder knnBuilder = KNearestNeighbors.withNeighbors(1);
//        Tuners.split(knnBuilder, null,
//                range("n", KNearestNeighbors.Builder::withNeighbors, 0, 10, 2));
//                enumeration("d", KNearestNeighbors.Builder::distance, Distance.EUCLIDEAN, Distance.MANHATTAN));
//
    }

    @Test
    public void testCombinations() throws Exception {
//        Updater<PrintStream> updater = ;
//        Updater<PrintStream> updater1 = enumeration(PrintStream::println, 10, 11, 12);

        List<Updater<PrintStream>> updaters = new ArrayList<>();
        updaters.add(range("r", PrintStream::println, 0.0, 3, 1));
        updaters.add(enumeration("e", PrintStream::println, 10, 11, 12));
//        updaters.add(enumeration("e", PrintStream::println, 10, 11, 12));

        testCombinations(updaters, System.out);

    }

    private <T> void testCombinations(List<Updater<T>> ud, T toUpdate) {
        cartesian(new int[][]{new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 10}, new int[]{10, 11}});
        cartesian(ud, toUpdate, 0);

    }

    private <T> void cartesian(List<Updater<T>> updaters, T toUpdate, int n) {
        if (n != updaters.size()) {
            Updater<T> updater = updaters.get(n);
            while (updater.hasUpdate()) {
                updater.update(toUpdate);
                cartesian(updaters, toUpdate, n + 1);
            }
            updater.restore();
        }
    }

    public void cartesian(int[][] lists) {
        cartesian(lists, new int[lists.length], 0);
    }

    public void cartesian(int[][] lists, int[] values, int n) {
        if (n == lists.length) {
            System.out.println(Arrays.toString(values));
        } else {
            for (int i : lists[n]) {
                values[n] = i;
                cartesian(lists, values, n + 1);
            }
        }
    }
}