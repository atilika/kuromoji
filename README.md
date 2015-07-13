# README

## Building

In order to build Kuromoji from source, please run the following command:

``` shell
    $ mvn clean package
```
   
This will download all source dictionary data and build Kuromoji with all dictionaries. The following Kuromoji jars will then be available:

```
./kuromoji-common/target/kuromoji-common-0.9-SNAPSHOT.jar
./kuromoji-unidic/target/kuromoji-ipadic-0.9-SNAPSHOT.jar
./kuromoji-unidic/target/kuromoji-unidic-0.9-SNAPSHOT.jar
./kuromoji-unidic-kanaaccent/target/kuromoji-unidic-kanaaccent-0.9-SNAPSHOT.jar
./kuromoji-naist-jdic/target/kuromoji-naist-jdic-0.9-SNAPSHOT.jar
./kuromoji-jumandic/target/kuromoji-jumandic-0.9-SNAPSHOT.jar


```

The following additional build options are available:

* `-DskipCompileDictionary`  Do not recompile the dictionaries
* `-DskipDownloadDictionary` Do not download source dictionaries
* `-DskipDownloadDictionary` Do not download source dictionaries
* `-DbenchmarkTokenizers` Profile each tokenizer during the package phase using content from Japanese Wikipedia
* `-DskipDownloadWikipedia` Prevent the compressed version of the Japanese Wikipedia (~765 MB) from being downloaded during profiling.

## Using  Kuromoji

To use Kuromoji, you must add `kuromoji-common-0.9-SNAPSHOT.jar` and your chosen dictionary's jar to your Project. For many projects, the IPADIC variant `kuromoji-ipadic-0.9-SNAPSHOT.jar` should suffice.

The following code snippet demonstrates how to use Kuromoji's tokenizer:

```java
...
import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
...
public class YourClass {
    public void yourMethod() {
        Tokenizer tokenizer = new Tokenizer() ;
        List<Token> tokens = tokenizer.tokenize("を寿司が食べたい。");
        for (Token token : tokens) {
            System.out.println(token.getSurfaceForm());
        }
    }
}
```

# Contact us

Please feel free to contact us on kuromoji@atilika.com if you have any questions.



