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
package com.atilika.kuromoji;

import com.atilika.kuromoji.AbstractTokenizer.Mode;
import com.atilika.kuromoji.dict.ConnectionCosts;
import com.atilika.kuromoji.dict.InsertedDictionary;
import com.atilika.kuromoji.dict.TokenInfoDictionary;
import com.atilika.kuromoji.dict.UnknownDictionary;
import com.atilika.kuromoji.dict.UserDictionary;
import com.atilika.kuromoji.trie.DoubleArrayTrie;
import com.atilika.kuromoji.viterbi.ViterbiBuilder;
import com.atilika.kuromoji.viterbi.ViterbiFormatter;
import com.atilika.kuromoji.viterbi.ViterbiLattice;
import com.atilika.kuromoji.viterbi.ViterbiNode;
import com.atilika.kuromoji.viterbi.ViterbiSearcher;

import java.util.Collections;
import java.util.List;

public class DebugTokenizer {

    private ViterbiFormatter formatter;

    private ViterbiBuilder viterbiBuilder;

    private ViterbiSearcher viterbiSearcher;

    private final DoubleArrayTrie doubleArrayTrie;

    private final ConnectionCosts connectionCosts;

    private final TokenInfoDictionary tokenInfoDictionary;

    private final UnknownDictionary unknownDictionary;

    private final UserDictionary userDictionary;

    private final InsertedDictionary insertedDictionary;

    protected DebugTokenizer(Builder builder) {
        this.doubleArrayTrie = builder.doubleArrayTrie;
        this.connectionCosts = builder.connectionCosts;
        this.tokenInfoDictionary = builder.tokenInfoDictionary;
        this.unknownDictionary = builder.unknownDictionary;
        this.userDictionary = builder.userDictionary;
        this.insertedDictionary = builder.insertedDictionary;

        this.viterbiBuilder = new ViterbiBuilder(
            doubleArrayTrie,
            tokenInfoDictionary,
            unknownDictionary,
            userDictionary,
            Mode.NORMAL);

        this.viterbiSearcher = new ViterbiSearcher(
            Mode.NORMAL,
            connectionCosts,
            unknownDictionary,
            Collections.EMPTY_LIST
        );
        this.formatter = new ViterbiFormatter(connectionCosts);
    }

    public String debugTokenize(String text) {
        ViterbiLattice lattice = this.viterbiBuilder.build(text);
        List<ViterbiNode> bestPath = this.viterbiSearcher.search(lattice);
        return this.formatter.format(lattice, bestPath);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private DoubleArrayTrie doubleArrayTrie;

        private ConnectionCosts connectionCosts;

        private TokenInfoDictionary tokenInfoDictionary;

        private UnknownDictionary unknownDictionary;

        private UserDictionary userDictionary = null;

        private InsertedDictionary insertedDictionary;

        private String defaultPrefix = System.getProperty(
            "Dummy"
        );

        private ResourceResolver resolver = new ClassLoaderResolver(this.getClass());

        public synchronized Builder prefix(String resourcePrefix) {
            this.defaultPrefix = resourcePrefix;
            return this;
        }

        public synchronized DebugTokenizer build() {
            if (defaultPrefix != null) {
                resolver = new PrefixDecoratorResolver(defaultPrefix, resolver);
            }

//            try {
//                doubleArrayTrie = DoubleArrayTrie.newInstance(resolver);
//                connectionCosts = ConnectionCosts.newInstance(resolver);
//                tokenInfoDictionary = TokenInfoDictionary.newInstance(resolver);
//                unknownDictionary = UnknownDictionary.newInstance(resolver);
//                insertedDictionary = new InsertedDictionary(9);
//            } catch (Exception ouch) {
//                throw new RuntimeException("Could not load dictionaries.", ouch);
//            }

//            return new DebugTokenizer(this);
            throw new RuntimeException("Sorry -- not yet implemented...");
        }
    }
}
