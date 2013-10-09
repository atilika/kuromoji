CHANGES
=======

Snapshot 0.8-SNAPSHOT (2013-10-09)

* Added ability to discard non-normalized entries in DictionaryBuilder

* Fixed issue with broken Viterbi paths in certain cases with user dictionaries.

* Fixed user dictionary features format.

* Added compaction to DoubleArrayTrie for improved memory footprint.

* Split part of speech from feature to improve memory footprint.

* Added unit tests testing all features, as well as user dictionary entries.

* Added buffering to input streams for faster loading of files.

* Compacted duplicated fields in TokenInfoDictionary.

* Added DebugTokenizerRunner to generate Viterbi debug output

* Added ability to customise Viterbi penalities (expert feature). (Masaru Hasegawa)

* Performance improvements to TokenInfoDictionary#getAllFeaturesArray. (Masaru Hasegawa)

* Fixed issue with TokenInfoDictionary#getAllFeaturesArray not returning all features. (Masaru Hasegawa)

* Improvements to how dictionary data is located and read/written: No Java serialization
  is used so classes can be freely renamed or relocated.　 Custom resolvers can be passed
  to locate dictionary data (and manipulate it along the way). (Dawid Weiss)


Release 0.7.7 (2012-01-30)

* Improved search mode decompounding heuristic and added unit tests for IPADIC

* Resolved connection costs work-around issue

* Renamed GraphvizFormatter to ViterbiFormatter


Release 0.7.6 (2011-07-12)

* Added preliminary UniDic support.  Download UniDic and build using

    mvn -Dkuromoji.dict.format=unidic \
        -Dkuromoji.dict.dir=unidic-mecab1312src \
        -Dkuromoji.dict.encoding=utf-8 \
        clean package


Release 0.7.5 (2011-04-20)

* Added temporary workaround for IOB exception in ConnectionCosts.

* Made CSV parsing (source dictionary parsing) proper. It now handles quote properly.
  You can now add half-width comma as dictionary entry by quoting it.

* Quote and escape output of getFeature method if necessary when it returns comma separated value(to return more than one feature).

* Added option to DicitonaryBuilder to create NFKC normalized dictionary entry (disabled by default).

* Added a method to Token, which get all features as array rather than CSV.

* Added option to DicitonaryBuilder to specify source dictionary encoding.

* Reduced initial memory usage during dictionary compile.

* Known Issues

  - GraphvizFormatter doesn't count penalty cost in search mode and extended mode
  - GraphvizFormatter doesn't work properly in extended mode


Release 0.7.4 (2011-02-07)

* Added DebugTokenizer to analyze Viterbi

* Modified extended mode behavior so that it now unigrams only unknown words

* Made splitting configurable

* Modified search mode (including extended mode) behavior. It now adds extra cost to non-kanji only nodes too.

* Added boundary checking in DoubleArrayTrie.

* Known Issues

  - GraphvizFormatter doesn't count penalty cost in search mode and extended mode
  - GraphvizFormatter doesn't work properly in extended mode

 
Release 0.7.3 (2011-01-18)

* Modified Tokenizer to reuse dictionaries so that it doesn't throw OOM when
  several Tokenzer instances are created.

* Modified API to get Tokenizer instance. It is instanciated using build()
  method.
