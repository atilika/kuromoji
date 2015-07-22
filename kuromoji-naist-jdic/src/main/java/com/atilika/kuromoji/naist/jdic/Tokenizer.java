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

package com.atilika.kuromoji.naist.jdic;

import com.atilika.kuromoji.AbstractTokenizer;
import com.atilika.kuromoji.viterbi.ViterbiNode;

/**
 * A tokenizer based on the NAIST-jdic dictionary
 * <p>
 * See {@link Token} for details on the morphological features produced by this tokenizer
 * <p>
 * The following code example demonstrates how to use the Kuromoji tokenizer:
 * <pre>{@code
 * package com.atilika.kuromoji.example;
 * import com.atilika.kuromoji.naist.jdic.Token;
 * import com.atilika.kuromoji.naist.jdic.Tokenizer;
 * import java.util.List;
 *
 * public class KuromojiExample {
 *     public static void main(String[] args) {
 *         Tokenizer tokenizer = new Tokenizer() ;
 *         List<Token> tokens = tokenizer.tokenize("お寿司が食べたい。");
 *         for (Token token : tokens) {
 *             System.out.println(token.getSurfaceForm() + "\t" + token.getAllFeatures());
 *         }
 *     }
 * }
 * }
 * </pre>
 */
public class Tokenizer extends AbstractTokenizer {

    /**
     * Class constructor constructing a default tokenizer
     */
    public Tokenizer() {
        this(new Builder());
    }

    /**
     * Class constructor constructing a customized tokenizer
     * <p>
     * See {@see com.atilika.kuromoji.naist.jdic.Tokenizer#Builder}
     */
    private Tokenizer(Builder builder) {
        configure(builder);
    }

    public static class Builder extends AbstractTokenizer.Builder {

        /**
         * Creates a default builder
         */
        public Builder() {
            totalFeatures = 11;
            unknownDictionaryTotalFeatures = 11;
            readingFeature = 7;
            partOfSpeechFeature = 0;
            defaultPrefix = System.getProperty(DEFAULT_DICT_PREFIX_PROPERTY, "com/atilika/kuromoji/naist-jdic/");
        }

        /**
         * Creates the custom tokenizer instance
         *
         * @return tokenizer instance, not null
         */
        @Override
        public Tokenizer build() {
            return new Tokenizer(this);
        }
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
}
