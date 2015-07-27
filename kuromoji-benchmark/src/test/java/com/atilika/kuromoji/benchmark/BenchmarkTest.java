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
        benchmark("com.atilika.kuromoji.ipadic.Tokenizer", "ipadic", "/Users/cm/Projects/kuromoji-cmoen/kuromoji-benchmark/jawiki/jawikiuserdict.txt");
        benchmark("com.atilika.kuromoji.ipadic.Tokenizer", "ipadic");
//        benchmark("com.atilika.kuromoji.jumandic.Tokenizer", "jumandic");
//        benchmark("com.atilika.kuromoji.naist.jdic.Tokenizer", "naist-jdic");
//        benchmark("com.atilika.kuromoji.unidic.Tokenizer", "unidic");
//        benchmark("com.atilika.kuromoji.unidic.kanaaccent.Tokenizer", "unidic-kanaaccent");
    }

    @Ignore("Enable during development")
    @Test
    public void testBocchanVariants() throws IOException {
        bocchan("com.atilika.kuromoji.ipadic.Tokenizer", "ipadic");
        bocchan("com.atilika.kuromoji.jumandic.Tokenizer", "jumandic");
        bocchan("com.atilika.kuromoji.naist.jdic.Tokenizer", "naist-jdic");
        bocchan("com.atilika.kuromoji.unidic.Tokenizer", "unidic");
        bocchan("com.atilika.kuromoji.unidic.kanaaccent.Tokenizer", "unidic-kanaaccent");
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
        args.add("/Users/cm/Projects/kuromoji-cmoen/jawiki-" + tokenizerName + "-benchmark.tsv");
        args.add("/Users/cm/Projects/kuromoji-cmoen/kuromoji-benchmark/jawiki/jawiki.tsv.gz");

        if (userDictionaryFilename != null) {
            args.add(0, "-u");
            args.add(1, userDictionaryFilename);
        }

        Benchmark.main(args.toArray(new String[args.size()]));
    }


    public void bocchan(String tokenizerClass, String tokenizerName) throws IOException {
        Benchmark.main(new String[]{
            "-t", tokenizerClass,
            "-o", "bocchan-" + tokenizerName + "-features.txt",
            "/Users/cm/Projects/kuromoji-cmoen/kuromoji-ipadic/src/test/resources/bocchan.txt",
        });
    }
}
