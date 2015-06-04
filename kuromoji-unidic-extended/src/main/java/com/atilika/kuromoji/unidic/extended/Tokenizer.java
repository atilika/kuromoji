/**
 * Copyright 2010-2013 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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

package com.atilika.kuromoji.unidic.extended;

import com.atilika.kuromoji.AbstractTokenizer;
import com.atilika.kuromoji.ClassLoaderResolver;
import com.atilika.kuromoji.PrefixDecoratorResolver;
import com.atilika.kuromoji.ResourceResolver;
import com.atilika.kuromoji.TokenizerRunner;
import com.atilika.kuromoji.dict.DynamicDictionaries;
import com.atilika.kuromoji.dict.UserDictionary;
import com.atilika.kuromoji.viterbi.ViterbiNode;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Tokenizer extends AbstractTokenizer {

    public static final String DEFAULT_DICT_PREFIX = "com/atilika/kuromoji/unidic-extended/";

    public Tokenizer(Builder builder) {
        super(builder.getDictionaries(), builder.getUserDictionary());
    }

    @Override
    protected Token createToken(int offset, ViterbiNode node, int wordId) {
        return new Token(wordId, node.getSurfaceForm(), node.getType(), offset + node.getStartIndex(), dictionaryMap.get(node.getType()));
    }

    private static Tokenizer init(String[] args) throws IOException {
        if (args.length == 1) {
            return new Builder().userDictionary(args[0]).build();
        }

        return new Builder().build();
    }

    public static void main(String[] args) throws IOException {
        Tokenizer tokenizer = init(args);
        new TokenizerRunner().run(tokenizer);
    }

    /**
     * Builder class used to create Tokenizer instance.
     */
    public static class Builder {
        private UserDictionary userDictionary = null;

        /**
         * The default resource prefix, also configurable via
         * system property <code>com.atilika.kuromoji.dict.targetdir</code>.
         */
        private String defaultPrefix = System.getProperty(
            DEFAULT_DICT_PREFIX_PROPERTY,
            DEFAULT_DICT_PREFIX);

        /**
         * The default resource resolver (relative to this class).
         */
        private ResourceResolver resolver = new ClassLoaderResolver(this.getClass());

        /**
         * Set user dictionary input stream
         *
         * @param userDictionaryInputStream dictionary file as input stream
         * @return Builder
         * @throws java.io.IOException
         */
        public synchronized Builder userDictionary(InputStream userDictionaryInputStream) throws IOException {
            this.userDictionary = UserDictionary.read(userDictionaryInputStream);
            return this;
        }

        /**
         * Set user dictionary path
         *
         * @param userDictionaryPath path to dictionary file
         * @return Builder
         * @throws IOException
         * @throws java.io.FileNotFoundException
         */
        public synchronized Builder userDictionary(String userDictionaryPath) throws IOException {
            if (userDictionaryPath != null && !userDictionaryPath.isEmpty()) {
                this.userDictionary(new BufferedInputStream(new FileInputStream(userDictionaryPath)));
            }
            return this;
        }

        /**
         * Sets the default prefix applied to resources at lookup time if classloader-relative
         * {@link ResourceResolver} is used.
         */
        public synchronized Builder prefix(String resourcePrefix) {
            this.defaultPrefix = resourcePrefix;
            return this;
        }

        /**
         * Create Tokenizer instance
         *
         * @return Tokenizer
         */
        public synchronized Tokenizer build() {
            if (defaultPrefix != null) {
                resolver = new PrefixDecoratorResolver(defaultPrefix, resolver);
            }

            return new Tokenizer(this);
        }

        public DynamicDictionaries getDictionaries() {
            return new DynamicDictionaries(resolver);
        }

        public UserDictionary getUserDictionary() {
            return userDictionary;
        }
    }
}
