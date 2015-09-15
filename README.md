# Briljant Framework

Briljant is a [MIT](http://https://opensource.org/licenses/MIT)
licensed framework for [Numpy](http://www.numpy.org/)-like nd-arrays
and [R](https://www.r-project.org/)-like data frames and vectors for
the [JVM](https://en.wikipedia.org/wiki/Java_virtual_machine) written
in [Java](https://www.java.com) with bindings for languages such as
[Groovy](http://www.groovy-lang.org/) and
[Kotlin](http://kotlinlang.org/).

## Main features

* versatile, simple to use and fast r-like data frame abstraction
(supporting *split-apply-combine* and other common idioms)

* fast and easy to use n-dimensional arrays for both primitive and
reference types with bindings to native BLAS and LAPACK routines

* experimental support for machine learning methods such as random
forest, random shapelet forest

## Example

```
CsvParser parser = new CsvParser();
parser.getSettings().setUrl(new URL("https://www.quandl.com/api/v1/datasets/GOOG/NASDAQ_AAPL.csv"));

DataFrame df = parser.parse();
df.groupBy(LocalDate.class, "Date", LocalDate::getYear)
  .collect(Vector::mean)
  .sort(SortOrder.DESC)
```

```
Open     High     Low      Close    Volume         
2015  122.715  123.807  121.366  122.559  54701792.000   
2014  92.216   93.009   91.482   92.264   63235262.818   
2013  67.587   68.234   66.893   67.519   101612690.714  
2012  82.379   83.118   81.412   82.293   131976305.232  
2011  52.007   52.487   51.467   52.000   123447489.857  
2010  37.137   37.482   36.693   37.008   145805263.861  
2009  20.946   21.213   20.708   20.986   139497489.556  
2008  20.330   20.730   19.837   20.226   280518140.298  
2007  18.341   18.585   18.027   18.325   246412550.853  
2006  10.142   10.279   9.973    10.115   215070884.749  
2005  6.661    6.758    6.566    6.668    181754270.762  
2004  2.529    2.573    2.495    2.537    121116211.286  
2003  1.323    1.346    1.301    1.325    70685000.016   
2002  1.366    1.395    1.336    1.367    73738366.698   
2001  1.441    1.483    1.401    1.444    92302462.742   
2000  3.167    3.380    3.160    3.265    110160044.540  
1999  0.000    2.203    2.099    2.154    130986650.063  
1998  0.000    3.450    3.288    3.378    55339069.349   
1997  0.000    2.623    2.518    2.567    16394428.822   
1996  0.000    3.631    3.501    3.560    13081872.835   
1995  0.000    5.897    5.700    5.791    18502022.222   
1994  0.000    4.963    4.773    4.869    14175136.111   
1993  0.000    5.969    5.752    5.861    13948507.115   
1992  0.000    7.943    7.709    7.829    10138857.874   
1991  0.000    7.632    7.358    7.499    14482723.336   
1990  0.000    5.460    5.260    5.366    10965262.055   
1989  0.000    6.043    5.854    5.952    12626672.222   
1988  0.000    6.026    5.841    5.934    10213406.719   
1987  0.000    7.862    7.528    7.696    10691949.407   
1986  0.000    4.705    4.551    4.637    6583743.478    
1985  0.000    2.942    2.875    2.885    5619956.917    
1984  0.000    3.913    3.816    3.829    5158288.933    
1983  0.000    5.474    5.333    5.353    5264802.372    
1982  0.000    2.785    2.732    2.739    2638958.498    
1981  0.000    3.496    3.473    3.473    1012468.379    
1980  0.000    4.365    4.349    4.349    3232815.385    

[36 rows x 5 columns]
```

## Installation

### Pre-compiled binaries

Not yet available

### Building from source

Since Briljant is built using [Gradle](https://gradle.org/) it is
simple to build from source and reference the binaries from your
project. First we need to clone the repository

    git clone https://github.com/isakkarlsson/briljant.git

Then building the source code is as simple as

    gradle install

In your `build.gradle` or `pom.xml` reference

    <dependency>
        <groupId>org.briljantframework</groupId>
        <artifactId>briljant-core</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>

## Contribute

We would love your contributions! Come talk to us in our [chat
room](https://gitter.im/isakkarlsson/briljant). Beware that in the current phase
of development the code is changing rapidly so please talk to use before
commiting to som major work!

We prefer developing in [IntellijIDEA](http://www.jetbrains.com/idea/), to
import the project use the ``gradle`` importer:

* File > Import Project
* Import project from external module (select Gradle)
* Check "use default gradle wrapper"

### Commit strategy

This project employs this [git branching
model](http://nvie.com/posts/a-successful-git-branching-model/).  In essence,
the major work is committed to the `develop`-branch and once a release version
is ready `master` and `develop` are merged.  Major features reside in there own
branches which are merged to `develop` once completed.

## TODO

 * Improve testing
 * Assess the performance
 * Clean up the API for first release
