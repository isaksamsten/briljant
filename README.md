# ADEB


## Building

    gradle dependencies
    gradle compile
    
### IDE
Most of the development is performed in [IntellijIDEA](http://www.jetbrains.com/idea/). The project can (quite easily) be imported using the gradle plugin.

 * File > Import Project
 * Import project from external module (select Gradle)
 * Check "use default gradle wrapper"
 * Check "Project format": (.ipr file-based)
    
## Sub-projects

* _core_: contains core components such as data and instance handling and matrix and BLAS operations
* _stat_: implements of some machine learning techniques

### core

So far, this module provides a `DataFrame`s, file input and output, `Matrix` and BLAS operations and `Transformations`.

Here is an example using the `CsvInputStream` and `DataFrame` to remove cases with missing posterior using the `RemoveIncompleteCases` transformer:

    DataFrame iris = Datasets.loadIris();
    Transformation rm = new RemoveIncompleteCases().fit(iris);
    iris = rm.transform(iris);

### adeb-learning

Two learning algorithms are implemented (to experiment with the API). The first is a simple 
stochastic gradient descent logistic regression learner which uses `adeb-matrix` and the `Frame`-class. The second 
algorithm is more general and uses an external classifier which communicates over either sockets or `stdin` and `stdout`.

The following is an example of using the `CSVInputStream` to load a storage and fit a logistic regression model to that
data source.

    StorageInputStream sis = new CSVInputStream(new FileInputStream("erlang/test.txt"));
    
    // Logistic regression requires a Frame 
    Storage<Frame, Target> storage = sis.read(Frame.FACTORY, BasicTarget.FACTORY);
    
    // Logistic regression does not support missing posterior, and data should be normalized
    Transformer<Matrix, Target> normalize = PipelineTransformer.of(
        new RemoveIncompleteCases(), new MinMaxNormalizer());
        
    LogisticRegression lr = LogisticRegression
        .withIterations(1000)
        .setLearningRate(0.01)
        .setRegularization(0.01)
        .create();
    Result result = Evaluators.crossValidation(lr, normalize.fitTransform(storage), 10);
    System.out.println(result)
    
While the normalization step biases the result, the example is valid and gives the following result.

    Average Confusion Matrix
          0     1     
    0     99.0  0.0   
    1     1.0   46.0  
    Accuracy: 0.99 (0.01)
    
    Metrics
                         Area Under ROC Curve   Error    Accuracy   
    1                    1.0000                 0.0000   1.0000     
    2                    1.0000                 0.0000   1.0000     
    3                    1.0000                 0.0667   0.9333     
    ..                   ..                     ..       ..
    9                    1.0000                 0.0000   1.0000     
    10                   1.0000                 0.0000   1.0000     
    Average              1.0000                 0.0067   0.9933     
    Standard Deviation   0.0000                 0.0211   0.0211
        
In the second example we are using an `RemoteClassifier` (implemented in [Erlang](http://www.erlang.org/)). 
This classifier supports any dataset and target (not rejected at runtime). Hence, we 
can use a [bounded wildcard](http://docs.oracle.com/javase/tutorial/java/generics/upperBounded.html) 
(i.e. `? extends Dataset`) and load any storage type (in the example a `DenseDataset` is loaded). 
     
     StorageInputStream in = new CSVInputStream(new FileInputStream("erlang/adeb-rr/deps/rr/data/iris.txt"));
     Storage<? extends Dataset, Target> storage = in.read(DenseDataset.FACTORY, BasicTarget.FACTORY);

     RemoteClassifier randomForest = RemoteClassifier
         .with(ProcessProtocol.create("erlang/adeb-rr/rr_learn"), 
                  ProcessProtocol.create("erlang/adeb-rr/rr_predict"))
         .set("no_features", "default")
         .set("no_trees", 100)
         .build();
     Result result = 
     
Time series predictions, using KNN with dynamic time warping

     Storage<Frame, Target> syntheticTrain = TimeSeries.load("erlang/synthetic_control_TRAIN.txt", 
                                                             Frame.FACTORY, BasicTarget.FACTORY);
     DynamicTimeWarping warp = DynamicTimeWarping.withDistance(Distance.EUCLIDEAN).constraint(10).create()
     KNearestNeighbors oneNearestNeighbours = KNearestNeighbors
        .withNeighbors(1)
        .setDistance(warp)
        .create();
        
     Evaluators.crossValidation(oneNearestNeighbours, syntheticTrain, 10)
     
A recent addition is the ability to automatically tune the parameters of a particular machine learning classifier. For
example, for k-nearest neighbors `k` and the distance-function can be tuned. Given these requirements, the following code
can be used to tune these parameters.

      Configurations<KNearestNeighbors> knn = Tuners.crossValidatation(
              KNearestNeighbors.builder(),                      // The classifier constructor
              storage,                                          // the storage used for optimization    
              Configuration.metricComparator(Accuracy.class),   // the comparator
              10,                                               // the number of optimization folds
              
              // The parameters to tune (first the neighbors in the range [1,8])
              range("K", KNearestNeighbors.Builder::neighbours, 1, 8, 1),
              // and two distance functions, the euclidean and manhattan (city-block) distance               
              enumeration("Distance", KNearestNeighbors.Builder::distance, Distance.EUCLIDEAN, Distance.MANHATTAN)
      );
      System.out.println(knn);
      
The above code type-safely tune the parameters according to the hold-out accuracy estimated using 10-fold cross-validation.
All possible parameter configurations are investigated. The result is shown below:

      k-Nearest Neighbors
      
      Resampling: Cross-validation with k = 10
      
      Results across tuning parameters:
      
         K          Distance           Accuracy  Error   Area Under ROC Curve  Precision  Recall  F-Measure  
         3          ManhattanDistance  0.9133    0.0867  0.9731                0.9204     0.9133  0.9086     
         1          ManhattanDistance  0.9133    0.0867  0.9474                0.9201     0.9133  0.9097     
         1          EuclideanDistance  0.9100    0.0900  0.9433                0.9183     0.9100  0.9037     
         ..         ..                 ..        ..      ..                    ..         ..      ..
         6          ManhattanDistance  0.8667    0.1333  0.9840                0.8821     0.8667  0.8566     
         8          EuclideanDistance  0.8667    0.1333  0.9883                0.8834     0.8667  0.8558     
         6          EuclideanDistance  0.8600    0.1400  0.9840                0.8769     0.8600  0.8492     
         8          ManhattanDistance  0.8533    0.1467  0.9898                0.8723     0.8533  0.8408    

If the results above is to be believed, `K=3` using the Manhattan distance yields the highest accuracy on unseen data.
However, if we chose to optimize for another metric, e.g. area under ROC curve, then `K=8` using the Manhattan distance 
seems to perform better.

In recent versions, ADEB is able to plot `Result`s returned from, e.g., `Evaluators#crossValidation`. In this 
example, a random forest model is built and the results from cross-validation is visualized.


First we load the data and construct the `Ensemble`-classifier with a random `DecisionTree`

      CSVInputStream in = new CSVInputStream(new FileInputStream("erlang/adeb-rr/deps/rr/data/sick-euthyroid.txt"));
      SimpleStorage storage = SimpleStorage.load(in).permute();

      DecisionTree.Builder dt = DecisionTree.withSplitter(
              RandomSplitter.withMaximumFeatures(3)
                      .setCriterion(Gain.with(Entropy.INSTANCE))
      );
      Ensemble<Dataset> ensemble = Ensemble.withMember(dt)
              .withSampler(Bootstrap.create())
              .create();

Next, we produce the result from cross-validation

      Result result = Evaluators.crossValidation(ensemble, storage, 10);
      
Finally, we create a window and plot the result
      
      JPanel grid = new JPanel(new GridLayout(1, 2));
      grid.add(new ChartPanel(result.getAverageConfusionMatrix().getChart()));
      grid.add(new ChartPanel(result.getAverageConfusionMatrix().getHeatMap()));

      JPanel master = new JPanel(new GridLayout(2, 1));
      master.add(new ChartPanel(result.getChart()));
      master.add(grid);

      JFrame frame = new JFrame();
      frame.add(master);
      frame.pack();
      frame.setVisible(true);
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      
The result

![Result plot](http://people.dsv.su.se/~isak-kar/plot-example.png)
                      

### adeb-matrix

The Matrix library defines some common BLAS (Basic Linear Algebra Subroutines)-operations. Most of these
operations are delegated to [highly optimized Fortran](http://www.openblas.net/) subroutines.
While significantly more work is required before this is production ready, some simple programs can take shape.
    
Example (using Java):
    
    import org.adeb.matrix.DenseMatrix
    import org.adeb.matrix.Matrices
    
    DenseMatrix ones = Matrices.ones(4, 4)
    DenseMatrix twos = ones.multiply(2)
    
Principal Component Analysis:
    
    matrix = DenseMatrix.of(5, 4,
          0, 2, 0, 1,
          2, 2, 3, 2,
          4, -3, 0, 1.,
          6, 1, -6, -5,
          1, 2, 3, 4
    );
    
    // Uses SingularValueDecomposition to find the principal components
    PrincipalComponentAnalyzer pca = new PrincipalComponentAnalyzer()
    PrincipalComponentAnalysis a = pca.analyze(matrix)
    System.out.println(a.components())
    
Or simpler:
    
    import static org.adeb.matrix.math.LinearAlgebra.*
    
    pca(matrix).components()
    pinv(matrix)
    inv(matrix)
    rank(matrix)
    svd(matrix)
    
Singular Value Decomposition is implemented in terms of the `LAPACK` operations, i.e. using the
optimized Openblas library. Hence, it has state-of-the-art performance:

    public SingularValueDecomposition decompose(MutableTensor matrix) {
        int m = matrix.rows(), n = matrix.columns();
        double[] work = new double[Math.min(m, n)];
        double[] sigma = new double[n];
        double[] u = new double[m * m];
        double[] vt = new double[n * n];
        MutableTensor copy = new Matrix(matrix);

        int err = LAPACKE_dgesvd(LAPACK_COL_MAJOR, 'a', 'a', m, n, copy.array(), m, sigma, u, m, vt, n, work);
        if (err != 0) {
            throw new BlasException("LAPACKE_dgesvd", err, "SVD failed to converge.");
        }

        Diagonal sv = Diagonal.of(m, n, sigma);
        Matrix um = DenseMatrix.fromColumnOrder(m, m, u);
        Matrix vtm = DenseMatrix.fromRowOrder(n, n, vt);
        return new SingularValueDecomposition(sv, um, vtm);
    }
    
Using `LAPACK` makes the library easy to use for users familiar with other scientific computing libraries.

#### Installation instructions for the native library:

Windows:

  * Download mingw-builds project - native toolchains using trunk from [here](http://mingw-w64.sourceforge.net/download.php#win-builds).
    Pick the mingw-w64-install.exe. Go through the installation wizard.
  * Find the installation directory, mine were found at `C:\Program Files\mingw-w64\x86_64-4.9.0-win32-seh-rt_v3-rev2\mingw64`
  * Add `${installation directory}/bin` to your environment path, i.e., in my case
    `C:\Program Files\mingw-w64\x86_64-4.9.0-win32-seh-rt_v3-rev2\mingw64\bin` were added 
  * Download OpenBLAS binaries from [here](http://www.openblas.net/). Click this [direct link](http://sourceforge.net/projects/openblas/files/)
    and choose the most current version.
  * Unzip the files
  * Inside the `\lib` directory find a file named `libopenblas.dll`. Copy this file to the project root (i.e. where the build.gradle file is located)
 
Mac OSX:

 * Download and install MacPorts
 * In a terminal windows run `sudo port install openblas +lapack`
    
Ubuntu (Debian etc.):

 * In a terminal windows run: `sudo apt-get install libopenblas-dev liblapack-dev`


## Commit strategy

This project employs this [git branching model](http://nvie.com/posts/a-successful-git-branching-model/). 
In essence, work shall be committed to the `develop`-branch and once a release version is ready `master` and `develop` are merged. 
Major features reside in there own branches which are merged to `develop` once completed.

## TODO

 * Integrate with Hadoop
 * 
 
