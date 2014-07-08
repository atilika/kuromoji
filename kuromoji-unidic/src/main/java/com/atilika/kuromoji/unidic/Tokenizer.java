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

package com.atilika.kuromoji.unidic;

import com.atilika.kuromoji.AbstractTokenizer;
import com.atilika.kuromoji.TokenizerRunner;
import com.atilika.kuromoji.ClassLoaderResolver;
import com.atilika.kuromoji.PrefixDecoratorResolver;
import com.atilika.kuromoji.ResourceResolver;
import com.atilika.kuromoji.dict.DynamicDictionaries;
import com.atilika.kuromoji.dict.UserDictionary;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Tokenizer extends AbstractTokenizer {

    public static final String DEFAULT_DICT_PREFIX = "com/atilika/kuromoji/unidic/";

    protected Tokenizer(DynamicDictionaries dictionaries, UserDictionary userDictionary, Mode mode, boolean split) {
        super(dictionaries, userDictionary, mode, split);
    }

    protected Tokenizer(DynamicDictionaries dictionaries, UserDictionary userDictionary, Mode mode, boolean split, int searchModeKanjiLength, int searchModeKanjiPenalty, int searchModeOtherLength, int searchModeOtherPenalty) {
        super(dictionaries, userDictionary, mode, split, searchModeKanjiLength, searchModeKanjiPenalty, searchModeOtherLength, searchModeOtherPenalty);
    }

    private static Tokenizer init(String[] args) throws IOException {
        Tokenizer tokenizer;
        if (args.length == 1) {
            Tokenizer.Mode mode = AbstractTokenizer.Mode.valueOf(args[0].toUpperCase());
            tokenizer = new Tokenizer.Builder().mode(mode).build();
        } else if (args.length == 2) {
            AbstractTokenizer.Mode mode = AbstractTokenizer.Mode.valueOf(args[0].toUpperCase());
            tokenizer = new Tokenizer.Builder().mode(mode).userDictionary(args[1]).build();
        } else {
            tokenizer = new Tokenizer.Builder().build();
        }
        return tokenizer;
    }

    public static void main(String[] args) throws IOException {
        Tokenizer tokenizer = init(args);
        new TokenizerRunner().run(tokenizer);
    }

    /**
     * Builder class used to create AbstractTokenizer instance.
     */
    public static class Builder {
        private Mode mode = Mode.NORMAL;
        private boolean split = true;
        private UserDictionary userDictionary = null;
        private Integer searchModeKanjiLength;
        private Integer searchModeKanjiPenalty;
        private Integer searchModeOtherLength;
        private Integer searchModeOtherPenalty;

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
         * Set tokenization mode
         * Default: NORMAL
         *
         * @param mode tokenization mode
         * @return Builder
         */
        public synchronized Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        /**
         * Set if tokenizer should split input string at "。" and "、" before tokenize to increase performance.
         * Splitting shouldn't change the result of tokenization most of the cases.
         * Default: true
         *
         * @param split whether tokenizer should split input string
         * @return Builder
         */
        public synchronized Builder split(boolean split) {
            this.split = split;
            return this;
        }

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
         * Sets the default {@link ResourceResolver} used to locate dictionaries.
         * @see #prefix(String)
         */
        public void resolver(ResourceResolver resolver) {
            if (resolver == null) throw new IllegalArgumentException();
            this.resolver = resolver;
        }

        public synchronized Builder penalties(int kanjiLength, int kanjiPenalty, int otherLength, int otherPenalty) {
            this.searchModeKanjiLength = kanjiLength;
            this.searchModeKanjiPenalty = kanjiPenalty;
            this.searchModeOtherLength = otherLength;
            this.searchModeOtherPenalty = otherPenalty;
            return this;
        }

        /**
         * Create AbstractTokenizer instance
         *
         * @return AbstractTokenizer
         */
        public synchronized Tokenizer build() {
            if (defaultPrefix != null) {
                resolver = new PrefixDecoratorResolver(defaultPrefix, resolver);
            }

            DynamicDictionaries dictionaries = new DynamicDictionaries(resolver);

            if (this.mode != Mode.NORMAL
                && searchModeKanjiLength != null && searchModeKanjiPenalty != null
                && searchModeOtherLength != null && searchModeOtherPenalty != null) {

                return new Tokenizer(dictionaries, userDictionary, mode, split,
                    searchModeKanjiLength, searchModeKanjiPenalty,
                    searchModeOtherLength, searchModeOtherPenalty);
            } else {
                return new Tokenizer(dictionaries, userDictionary, mode, split);
            }
        }

    }


}
