/**
 * Copyright © 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the
 * License is distributed with this work in the LICENSE.md file.  You may
 * also obtain a copy of the License from
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atilika.kuromoji.benchmark;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkTest {

    @Ignore("Enable during development")
    @Test
    public void testBenchmarkIpadics() throws IOException {
//        benchmark("com.atilika.kuromoji.ipadic.Tokenizer", "ipadic", "kuromoji-benchmark/jawiki/jawikiuserdict.txt");
        benchmark("com.atilika.kuromoji.ipadic.Tokenizer", "ipadic");
//        benchmark("com.atilika.kuromoji.jumandic.Tokenizer", "jumandic");
//        benchmark("com.atilika.kuromoji.naist.jdic.Tokenizer", "naist-jdic");
//        benchmark("com.atilika.kuromoji.unidic.Tokenizer", "unidic");
//        benchmark("com.atilika.kuromoji.unidic.kanaaccent.Tokenizer", "unidic-kanaaccent");
    }

    @Ignore("Enable during development")
    @Test
    public void testBocchanVariants() throws IOException {
        String bocchan = "../kuromoji-ipadic/src/test/resources/bocchan.txt";
        tokenize(bocchan, "bocchan-ipadic-features.txt", "com.atilika.kuromoji.ipadic.Tokenizer");
        tokenize(bocchan, "bocchan-jumandic-features.txt", "com.atilika.kuromoji.jumandic.Tokenizer");
        tokenize(bocchan, "bocchan-naist-jdic-features.txt", "com.atilika.kuromoji.naist.jdic.Tokenizer");
        tokenize(bocchan, "bocchan-unidic-features.txt", "com.atilika.kuromoji.unidic.Tokenizer");
        tokenize(bocchan, "bocchan-unidic-kanaaccent-features.txt", "com.atilika.kuromoji.unidic.kanaaccent.Tokenizer");
    }

    @Ignore("Enable during development")
    @Test
    public void testUserDictionaryVariants() throws IOException {
        String jawikisentences = "../kuromoji-ipadic/src/test/resources/jawikisentences.txt";
        String userDictionaryFilename = "../kuromoji-core/src/test/resources/userdict.txt";
        tokenizeUserDictionary(jawikisentences, "jawikisentences-ipadic-features.txt", "com.atilika.kuromoji.ipadic.Tokenizer", userDictionaryFilename);
        tokenizeUserDictionary(jawikisentences, "jawikisentences-jumandic-features.txt", "com.atilika.kuromoji.jumandic.Tokenizer", userDictionaryFilename);
        tokenizeUserDictionary(jawikisentences, "jawikisentences-naist-jdic-features.txt", "com.atilika.kuromoji.naist.jdic.Tokenizer", userDictionaryFilename);
        tokenizeUserDictionary(jawikisentences, "jawikisentences-unidic-features.txt", "com.atilika.kuromoji.unidic.Tokenizer", userDictionaryFilename);
        tokenizeUserDictionary(jawikisentences, "jawikisentences-unidic-kanaaccent-features.txt", "com.atilika.kuromoji.unidic.kanaaccent.Tokenizer", userDictionaryFilename);
    }

    public void benchmark(String tokenizerClass, String tokenizerName) throws IOException {
        benchmark(tokenizerClass, tokenizerName, null);
    }

    public void benchmark(String tokenizerClass, String tokenizerName, String userDictionaryFilename) throws IOException {
        List<String> args = new ArrayList<>();
        args.add("-t");
        args.add(tokenizerClass);
        args.add("-c");
        args.add("3000");
        args.add("--benchmark-output");
        args.add("jawiki-" + tokenizerName + "-benchmark.tsv");
        args.add("../kuromoji-benchmark/jawiki/jawiki.tsv.gz");

        if (userDictionaryFilename != null) {
            args.add(0, "-u");
            args.add(1, userDictionaryFilename);
        }

        Benchmark.main(args.toArray(new String[args.size()]));
    }


    public void tokenize(String inputFilename,
                         String outputFilename,
                         String tokenizerClass) throws IOException {
        Benchmark.main(new String[]{
            "-t", tokenizerClass,
            "-o", outputFilename,
            inputFilename
        });
    }

    public void tokenizeUserDictionary(String inputFilename,
                                       String outputFilename,
                                       String tokenizerClass,
                                       String userDictionaryFilename) throws IOException {
        Benchmark.main(new String[]{
            "-t", tokenizerClass,
            "-o", outputFilename,
            "-u", userDictionaryFilename,
            inputFilename
        });
    }

}
