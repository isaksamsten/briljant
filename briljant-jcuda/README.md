# Briljant Framework CUDA backend

This is an (extremely) early version of an `ArrayBackend` based on
[CUDA](http://https://developer.nvidia.com/cuda-zone), which is
currently implemented using [JCuda](http://jcuda.org). To allow
deployment, this module uses the bundled jar-files and native-binaries
and packages these at deployment time.

## Installation

Since this backend is experimental, it is hidden behind a Maven
profile and enabled using the `-P`-flag. To install, use `mvn clean
install -Pjcuda` and include
`org.briljantframework:briljant-jcuda:0.3-SNAPSHOT` in your build
file. Also don't forget to install appropriate driver for your
system.

Note that it is only tested columnKeys OSX (10.11), which has some issues
when SIP is enabled.

