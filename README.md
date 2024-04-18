# Camel: Efficient Compression of Floating-Point Time Series

## Project Structure

This project mainly includes the following various compression algorithms:

- The main code for the ***Camel*** algorithm is in the *org/urbcomp/startdb/compress/camel* package.

- The main code for the ***Elf*** algorithm is in the *org/urbcomp/startdb/compress/elf* package.

- The main code for the ***Chimp*** algorithm is in the *gr/aueb/delorean/chimp* package.

- The main code for the ***Gorilla*** algorithm is in the *fi/iki/yak/ts/compression/gorilla* package.

- The main code for the ***FPC*** algorithm is in the *com/github/kutschkem/fpc* package.

- The main code for other general compression algorithms is in the *org/apache/hadoop/hbase/io/compress* package.


Camel includes *compressor* and *decompressor* packages as well as *xorcompressor* and *xordecompressor*.

#### compressor package

This package includes 5 different XOR-based compression algorithms and provides a standard **ICompressor** interface. 

- CamelCompressor: This class is the complete Camel compression algorithm.
- ElfCompressor: This class is the complete elf compression algorithm.
- GorillaCompressorOS: This class is the Gorilla algorithm using Bitstream I/O optimization.
- ChimpCompressor: This class is the original chimp algorithm.
- ChimpNCompressor: This class is the original chimp128 algorithm.

#### decompressor package

This package includes the decompressors corresponding to the above 5 compressors and gives the standard **IDecompressor** interface

#### xorcompressor package

This package is a compressed encoding of post-erase data designed for XOR-based operations

#### dexorcompressor package

This package is a decompression of the erased data designed based on the XOR-based operation code.

## TEST Camel

We recommend IntelliJ IDEA for developing this project. In our experiment, the default data block size is 1000. That is, 1000
pieces of data are read in each time for compression testing. If the size of the data set is less than 1000, we will not read it. The final experimental result is an average calculation of the compression of all data blocks.

- Camel compressor test: src/test/java/org/urbcomp/startdb/compress/elf/doubleprecision/TestCamel.java
- Camel Index building test: src/test/java/org/urbcomp/startdb/compress/elf/doubleprecision/TestCamelTree.java

### Prerequisites for testing

The following resources need to be downloaded and installed:

- Java 8 download: https://www.oracle.com/java/technologies/downloads/#java8
- IntelliJ IDEA download: https://www.jetbrains.com/idea/
- git download:https://git-scm.com/download
- maven download: https://archive.apache.org/dist/maven/maven-3/

Download and install jdk-8, IntelliJ IDEA and git. IntelliJ IDEA's maven project comes with maven, you can also use your
own maven environment, just change it in the settings.


### Set JDK

File -> Project Structure -> Project -> Project SDK -> *add SDK*

Click *JDK* to select the address where you want to download jdk-8



