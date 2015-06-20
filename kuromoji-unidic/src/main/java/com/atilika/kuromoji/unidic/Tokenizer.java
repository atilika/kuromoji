/**
 * Copyright 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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
import com.atilika.kuromoji.ClassLoaderResolver;
import com.atilika.kuromoji.PrefixDecoratorResolver;
import com.atilika.kuromoji.ResourceResolver;
import com.atilika.kuromoji.TokenizerRunner;
import com.atilika.kuromoji.dict.ConnectionCosts;
import com.atilika.kuromoji.dict.InsertedDictionary;
import com.atilika.kuromoji.dict.TokenInfoDictionary;
import com.atilika.kuromoji.dict.UnknownDictionary;
import com.atilika.kuromoji.dict.UserDictionary;
import com.atilika.kuromoji.trie.DoubleArrayTrie;
import com.atilika.kuromoji.viterbi.ViterbiNode;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class Tokenizer extends AbstractTokenizer {

    public static final String DEFAULT_DICT_PREFIX = "com/atilika/kuromoji/unidic/";

    public Tokenizer(Builder builder) {
        super(
            builder.doubleArrayTrie,
            builder.connectionCosts,
            builder.tokenInfoDictionary,
            builder.unknownDictionary,
            builder.userDictionary,
            builder.insertedDictionary,
            Mode.NORMAL,
            true, // split,
            Collections.EMPTY_LIST
        );
    }

    @Override
    protected Token createToken(int offset, ViterbiNode node, int wordId) {
        return new Token(
            wordId,
            node.getSurfaceForm(),
            node.getType(),
            offset + node.getStartIndex(),
            dictionaryMap.get(node.getType())
        );
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

    public static class Builder {

        private DoubleArrayTrie doubleArrayTrie;

        private ConnectionCosts connectionCosts;

        private TokenInfoDictionary tokenInfoDictionary;

        private UnknownDictionary unknownDictionary;

        private UserDictionary userDictionary = null;

        private InsertedDictionary insertedDictionary;

        private String defaultPrefix = System.getProperty(
            DEFAULT_DICT_PREFIX_PROPERTY,
            DEFAULT_DICT_PREFIX);

        private ResourceResolver resolver = new ClassLoaderResolver(this.getClass());

        public synchronized Builder userDictionary(InputStream userDictionaryInputStream) throws IOException {
            this.userDictionary = new UserDictionary(
                userDictionaryInputStream,
                13, 7, 0
            );
            return this;
        }

        public synchronized Builder userDictionary(String userDictionaryPath) throws IOException {
            if (userDictionaryPath != null && !userDictionaryPath.isEmpty()) {
                this.userDictionary(new BufferedInputStream(new FileInputStream(userDictionaryPath)));
            }
            return this;
        }

        public synchronized Builder prefix(String resourcePrefix) {
            this.defaultPrefix = resourcePrefix;
            return this;
        }

        public synchronized Tokenizer build() {
            if (defaultPrefix != null) {
                resolver = new PrefixDecoratorResolver(defaultPrefix, resolver);
            }

            try {
                doubleArrayTrie = DoubleArrayTrie.newInstance(resolver);
                connectionCosts = ConnectionCosts.newInstance(resolver);
                tokenInfoDictionary = TokenInfoDictionary.newInstance(resolver);
                unknownDictionary = UnknownDictionary.newInstance(resolver);
                insertedDictionary = new InsertedDictionary(13);
            } catch (Exception ouch) {
                throw new RuntimeException("Could not load dictionaries.", ouch);
            }

            return new Tokenizer(this);
        }
    }
}
