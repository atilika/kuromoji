README.txt
==========

Building
--------

In order to build kuromoji from source, please do as follows:

1. Provide Java with 512MB heap size

    % export MAVEN_OPTS=-Xmx512m
  
2. Run Maven using

    % mvn -Ddownload=true package

   to download the dictionary data and build a distribution

3. A distribution should now be available as

    target/kuromoji-<version>.tar.gz (and .zip)

  and a jar file should also be available in the target directory


Contact us
----------

kuromoji@atilika.com
