package org.briljantframework.evaluation.classification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.DefaultValidator;
import org.briljantframework.evaluation.Evaluator;
import org.briljantframework.evaluation.Validator;
import org.briljantframework.evaluation.partition.FoldPartitioner;
import org.briljantframework.evaluation.partition.LeaveOneOutPartitioner;
import org.briljantframework.evaluation.partition.Partition;
import org.briljantframework.evaluation.partition.SplitPartitioner;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class Validators {

  public static Validator holdout(DataFrame testX, Vector testY) {
    return new DefaultValidator(getEvaluators(), (x, y) -> Collections.singleton(new Partition(x,
        testX, y, testY)));
  }

  private static List<Evaluator> getEvaluators() {
    return Arrays.asList(new ZeroOneLossEvaluator(), new ProbabilityEvaluator(),
        new ConfusionMatrixEvaluator());
  }

  public static Validator splitValidation(double testFraction) {
    return new DefaultValidator(getEvaluators(), new SplitPartitioner(testFraction));
  }

  public static Validator leaveOneOutValidation() {
    return new DefaultValidator(getEvaluators(), new LeaveOneOutPartitioner());
  }

  public static Validator crossValidation(int folds) {
    return new DefaultValidator(getEvaluators(), new FoldPartitioner(folds));
  }
}
