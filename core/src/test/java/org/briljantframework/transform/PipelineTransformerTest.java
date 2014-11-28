package org.briljantframework.transform;

import org.junit.Test;

public class PipelineTransformerTest {

  @Test
  public void testPipeline() throws Exception {
    // DenseDataset dataset = new CSVInputStream(new
    // FileInputStream("erlang/test.txt")).read(DenseDataset
    // .getFactory());
    // Container.Factory<TargetContainer> factory =
    // TargetContainer.factory(DenseDataset.FACTORY, DefaultTarget.FACTORY);
    //
    // TargetContainer container = TargetContainer.create(dataset, "Class",
    // DenseDataset.FACTORY, DefaultTarget.FACTORY);
    //
    // System.out.println(container.getDataset());
    // System.out.println(container.getTarget());
    // System.out.println(dataset);
    //
    //
    // Split<TargetContainer> split = Containers.split(container, 0.3, factory);
    // System.out.println(split.getTrainingSet());

    // RemoveIncompleteCases<DenseDataset> cases = new RemoveIncompleteCases<>();
    // Transformation<DenseDataset> targetContainerTransformation = cases.fit(dataset);
    // DenseDataset noMissing = targetContainerTransformation.transform(dataset,
    // DenseDataset.getFactory());
    //
    // Transformer<DenseDataset> transformers = PipelineTransformer.of(DenseDataset.getFactory(),
    // new RemoveIncompleteColumns<>(), new RemoveIncompleteCases<>());
    //
    // System.out.println(transformers.fitTransform(dataset, DenseDataset.getFactory()));
    // System.out.println(noMissing);


    // Partitions<TargetContainer> partitions = Containers.partition(container, 10, factory);
    // TargetableContainer container1 = partitions.takeAndMerge(factory, 1, 2, 3);
    // System.out.println(container1);

  }
}
