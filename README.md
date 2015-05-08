README
======

Building
--------

In order to build Kuromoji from source, please do as follows:

1. Provide Java with 2GB heap size

    `% export MAVEN_OPTS=-Xmx2g`
  
2. Run Maven using

    `% mvn -Ddownload=true clean package`

   to download the dictionary data and build Kuromoji with all dictionaries.


Contact us
----------

kuromoji@atilika.com

