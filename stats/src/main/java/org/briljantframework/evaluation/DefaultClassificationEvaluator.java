package org.briljantframework.evaluation;

import static org.briljantframework.evaluation.result.Measure.Sample.IN;
import static org.briljantframework.evaluation.result.Measure.Sample.OUT;

import java.util.ArrayList;
import java.util.List;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.*;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * The default
 * <p>
 * Created by Isak Karlsson on 01/12/14.
 */
public class DefaultClassificationEvaluator extends AbstractClassificationEvaluator {

  public DefaultClassificationEvaluator(MeasureProvider measureProvider, Partitioner partitioner) {
    super(measureProvider, partitioner);
  }

  public DefaultClassificationEvaluator(Partitioner partitioner) {
    this(Measures.getDefaultClassificationMeasures(), partitioner);
  }

  public DefaultClassificationEvaluator() {
    this(new SplitPartitioner(0.33));
  }

  @Override
  public Result evaluate(Classifier classifier, DataFrame x, Vector y) {
    Iterable<Partition> partitions = getPartitioner().partition(x, y);
    Vector domain = Vectors.unique(y);
    List<Measure.Builder> builders = getMeasureProvider().getMeasures(domain);
    List<ConfusionMatrix> confusionMatrices = new ArrayList<>();

    for (Partition partition : partitions) {
      DataFrame trainingData = partition.getTrainingData();
      Vector trainingTarget = partition.getTrainingTarget();
      Predictor predictor = classifier.fit(trainingData, trainingTarget);

      DataFrame validationData = partition.getValidationData();
      Vector validationTarget = partition.getValidationTarget();

      Vector outSamplePredictions = predictor.predict(validationData);
      Vector inSamplePredictions = predictor.predict(trainingData);

      DoubleMatrix outSampleProba = predictor.predictProba(validationData);
      DoubleMatrix inSampleProba = predictor.predictProba(trainingData);

      ConfusionMatrix matrix =
          ConfusionMatrix.compute(outSamplePredictions, validationTarget, domain);
      confusionMatrices.add(matrix);
      for (Measure.Builder builder : builders) {
        builder.compute(IN, predictor, inSamplePredictions, inSampleProba, trainingTarget);
        builder.compute(OUT, predictor, outSamplePredictions, outSampleProba, validationTarget);
      }
    }
    return Result.create(collect(builders), confusionMatrices);
  }
}
