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
