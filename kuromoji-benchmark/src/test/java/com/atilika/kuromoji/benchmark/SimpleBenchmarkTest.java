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

import com.atilika.kuromoji.TokenizerBase;
import com.atilika.kuromoji.TestUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleBenchmarkTest {

    private static final String IPADIC = "com.atilika.kuromoji.ipadic.Tokenizer";
    private static final String IPADIC_NEOLOGD = "com.atilika.kuromoji.ipadic.neologd.Tokenizer";
    private static final String JUMAN_DIC = "com.atilika.kuromoji.jumandic.Tokenizer";
    private static final String NAIST_JDIC = "com.atilika.kuromoji.naist.jdic.Tokenizer";
    private static final String UNIDIC = "com.atilika.kuromoji.unidic.Tokenizer";
    private static final String UNIDIC_KANAACCENT = "com.atilika.kuromoji.unidic.kanaaccent.Tokenizer";
    private static final String UNIDIC_NEOLOGD = "com.atilika.kuromoji.unidic.neologd.Tokenizer";

    private List<String> tokenizerClasses = Arrays.asList(
        IPADIC, IPADIC_NEOLOGD, JUMAN_DIC, NAIST_JDIC, UNIDIC, UNIDIC_KANAACCENT, UNIDIC_NEOLOGD
    );

    private Map<String, TokenizerBase> tokenizers = new HashMap<>();

    @Test
    public void testSimpleBenchmark() throws Exception {
        for (String classname : tokenizerClasses) {
            long starttime = System.currentTimeMillis();
            TokenizerBase t = tokenizeForName(classname);

            System.out.println("Created " + classname
                + " in " + (System.currentTimeMillis() - starttime)
                + " msec"
            );
            tokenizers.put(classname, t);
        }

        for (String classname : tokenizerClasses) {
            TokenizerBase tokenizer = tokenizers.get(classname);
            long starttime = System.currentTimeMillis();

            TestUtils.assertCanTokenizeStream(
                getClass().getResourceAsStream("/bocchan.txt"),
                tokenizer
            );
            System.out.println("Tokenized bocchan.txt using " + classname
                + " in " + (System.currentTimeMillis() - starttime)
                + " msec"
            );
        }
    }

    @Ignore("Distabled as it often depletes heap-size")
    @Test
    public void testInstantiationBenchmark() throws Exception {
        for (String classname : tokenizerClasses) {
            long starttime = System.currentTimeMillis();
            int count = 5;

            for (int i = 0; i < count; i++) {
                tokenizeForName(classname);
            }

            System.out.println("Created " + count
                + " instances of " + classname
                + " in " + (System.currentTimeMillis() - starttime)
                + " msec"
            );
        }
    }

    private TokenizerBase tokenizeForName(String classname) throws Exception {
        Class clazz = Class.forName(classname);
        return (TokenizerBase) clazz.newInstance();
    }
}
