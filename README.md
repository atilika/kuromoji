# Kuromoji [![Build Status](https://travis-ci.org/atilika/kuromoji.svg?branch=master)](https://travis-ci.org/atilika/kuromoji)

Kuromoji is an easy to use and self-contained Japanese morphological analyzer that does

- **Word segmentation.** Segmenting text into words (or morphemes)
- **Part-of-speech tagging.** Assign word-categories (nouns, verbs, particles, adjectives, etc.)
- **Lemmatization.** Get dictionary forms for inflected verbs and adjectives
- **Readings.** Extract readings for kanji

Several other features are supported. Please consult each dictionaries' `Token` class for details.

## Using Kuromoji

The example below shows how to use the Kuromoji morphological analyzer in its simlest form; to segment text into tokens and output features for each token.

```java
package com.atilika.kuromoji.example;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import java.util.List;

public class KuromojiExample {
    public static void main(String[] args) {
        Tokenizer tokenizer = new Tokenizer() ;
        List<Token> tokens = tokenizer.tokenize("お寿司が食べたい。");
        for (Token token : tokens) {
            System.out.println(token.getSurface() + "\t" + token.getAllFeatures());
        }
    }
}
```

Make sure you add the dependency below to your `pom.xml` before building your project.

```xml
<dependency>
  <groupId>com.atilika.kuromoji</groupId>
  <artifactId>kuromoji-ipadic</artifactId>
  <version>0.9.0</version>
</dependency>
```

When running the above program, you will get the following output:

```
お　　　接頭詞,名詞接続,*,*,*,*,お,オ,オ
寿司　　名詞,一般,*,*,*,*,寿司,スシ,スシ
が　　　助詞,格助詞,一般,*,*,*,が,ガ,ガ
食べ　　動詞,自立,*,*,一段,連用形,食べる,タベ,タベ
たい　　助動詞,*,*,*,特殊・タイ,基本形,たい,タイ,タイ
。　　　記号,句点,*,*,*,*,。,。,。
```

See the documentation for the `com.atilika.kuromoji.ipadic.Token` class for more information on the per-token features available.

## Supported dictionaries

Kuromoji currently supports the following dictionaries:
- IPADIC ([2.7.0-20070801](http://atilika.com/releases/mecab-ipadic/mecab-ipadic-2.7.0-20070801.tar.gz))
- IPADIC NEologd ([2.7.0-20070801-neologd-20171113](http://atilika.com/releases/mecab-ipadic-neologd/mecab-ipadic-2.7.0-20070801-neologd-20171113.tar.gz))
- JUMANDIC ([7.0-20130310](http://atilika.com/releases/mecab-jumandic/mecab-jumandic-7.0-20130310.tar.gz))
- NAIST jdic ([0.6.3b-20111013](http://atilika.com/releases/mecab-naist-jdic/mecab-naist-jdic-0.6.3b-20111013.tar.gz))
- UniDic ([2.1.2](http://atilika.com/releases/unidic-mecab/unidic-mecab-2.1.2_src.zip))
- UniDic Kana Accent ([2.1.2](http://atilika.com/releases/unidic-mecab/unidic-mecab_kana-accent-2.1.2_src.zip))
- UniDic NEologd ([2.1.2-neologd-20171002](http://atilika.com/releases/unidic-mecab-neologd/unidic-mecab-2.1.2_src-neologd-20171002.zip))

**Question:** So which of these dictionaries should I use?

**Answer**: That depends on your application. Yes, we know - it's a boring answer... :)

If you are not sure about which dictionary you should use, `kuromoji-ipadic` is a good starting point for many applications.

See the getters in the per-dictionary `Token` classes for some more information on available token features - or consult the technical dictionary documentation elsewhere. (We plan on adding better guidance on choosing a dictionary.)

### Maven coordinates and user classes

Each dictionary has its own Maven coordinates, and a `Tokenizer` and a `Token` class similar to that in the above example.  These classes live in a designated packaged space indicated by the dictionary type.

The sections below list fully qualified class names and the Maven coordinates for each dictionary supported.

### kuromoji-ipadic

- `com.atilika.kuromoji.ipadic.Tokenizer`
- `com.atilika.kuromoji.ipadic.Token`

```xml
<dependency>
  <groupId>com.atilika.kuromoji</groupId>
  <artifactId>kuromoji-ipadic</artifactId>
  <version>0.9.0</version>
</dependency>
```

### kuromoji-ipadic-neologd

- `com.atilika.kuromoji.ipadic.neologd.Tokenizer`
- `com.atilika.kuromoji.ipadic.neologd.Token`

This dictionary will be available from Maven Central in a future version.


### kuromoji-jumandic

- `com.atilika.kuromoji.jumandic.Tokenizer`
- `com.atilika.kuromoji.jumandic.Token`

```xml
<dependency>
  <groupId>com.atilika.kuromoji</groupId>
  <artifactId>kuromoji-jumandic</artifactId>
  <version>0.9.0</version>
</dependency>
```

### kuromoji-naist-jdic

- `com.atilika.kuromoji.naist.jdic.Tokenizer`
- `com.atilika.kuromoji.naist.jdic.Token`

```xml
<dependency>
  <groupId>com.atilika.kuromoji</groupId>
  <artifactId>kuromoji-naist-jdic</artifactId>
  <version>0.9.0</version>
</dependency>
```

### kuromoji-unidic

- `com.atilika.kuromoji.unidic.Tokenizer`
- `com.atilika.kuromoji.unidic.Token`

```xml
<dependency>
  <groupId>com.atilika.kuromoji</groupId>
  <artifactId>kuromoji-unidic</artifactId>
  <version>0.9.0</version>
</dependency>
```

### kuromoji-unidic-kanaaccent

- `com.atilika.kuromoji.unidic.kanaaccent.Tokenizer`
- `com.atilika.kuromoji.unidic.kanaaccent.Token`

```xml
<dependency>
  <groupId>com.atilika.kuromoji</groupId>
  <artifactId>kuromoji-unidic-kanaaccent</artifactId>
  <version>0.9.0</version>
</dependency>
```

### kuromoji-unidic-neologd

- `com.atilika.kuromoji.unidic.neologd.Tokenizer`
- `com.atilika.kuromoji.unidic.kanaaneologdcent.Token`

This dictionary will be available from Maven Central in a future version.


## Building Kuromoji from source code

Released version of Kuromoji are available from Maven Central.

 If you want to build Kuromoji from source code, run the following command:

```shell
$ mvn clean package
```

This will download all source dictionary data and build Kuromoji with all dictionaries. The following jars will then be available:

```
kuromoji-core/target/kuromoji-core-1.0-SNAPSHOT.jar
kuromoji-ipadic/target/kuromoji-ipadic-1.0-SNAPSHOT.jar
kuromoji-ipadic-neologd/target/kuromoji-ipadic-neologd-1.0-SNAPSHOT.jar
kuromoji-jumandic/target/kuromoji-jumandic-1.0-SNAPSHOT.jar
kuromoji-naist-jdic/target/kuromoji-naist-jdic-1.0-SNAPSHOT.jar
kuromoji-unidic/target/kuromoji-unidic-1.0-SNAPSHOT.jar
kuromoji-unidic-kanaaccent/target/kuromoji-unidic-kanaaccent-1.0-SNAPSHOT.jar
kuromoji-unidic-neologd/target/kuromoji-unidic-neologd-1.0-SNAPSHOT.jar
```

The following additional build options are available:

* `-DskipCompileDictionary`  Do not recompile the dictionaries
* `-DskipDownloadDictionary` Do not download source dictionaries
* `-DbenchmarkTokenizers` Profile each tokenizer during the package phase using content from Japanese Wikipedia
* `-DskipDownloadWikipedia` Prevent the compressed version of the Japanese Wikipedia (~765 MB) from being downloaded during profiling, i.e. if it has already been downloaded.

## License

Kuromoji is licensed under the Apache License, Version 2.0.  See `LICENSE.md` for details.

This software also includes a binary and/or source version of data from various 3rd party dictionaries.  See `NOTICE.md` for these details.

## Contributing

Please open up issues if you have a feature request.  We also welcome contributions through pull requests.

You will retain copyright to your own contributions, but you need to license them using the Apache License, Version 2.0.
All contributors will be mentioned in the `CONTRIBUTORS.md` file.

# About us

We are a small team of experienced software engineers based in Tokyo who offers technologies and good advice in the field of search, natural language processing and big data analytics.

Please feel free to contact us at kuromoji@atilika.com if you have any questions or need help.
