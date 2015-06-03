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
package com.atilika.kuromoji.dict;

import com.atilika.kuromoji.ResourceResolver;
import com.atilika.kuromoji.trie.DoubleArrayTrie;

public class DynamicDictionaries {
    
    private final TokenInfoDictionary tokenInfoDictionary;
    private final UnknownDictionary unknownDictionary;
    private final ConnectionCosts connectionCosts;
    private final DoubleArrayTrie doubleArrayTrie;

    public DynamicDictionaries(ResourceResolver resolver) {
        try {
            tokenInfoDictionary = TokenInfoDictionary.newInstance(resolver);
            unknownDictionary = UnknownDictionary.newInstance(resolver);
            connectionCosts = ConnectionCosts.newInstance(resolver);
            doubleArrayTrie = DoubleArrayTrie.newInstance(resolver);
        } catch (Exception ex) {
            throw new RuntimeException("Could not load dictionaries.", ex);
        }
    }

    public DoubleArrayTrie getTrie() {
        return doubleArrayTrie;
    }

    public TokenInfoDictionary getDictionary() {
        return tokenInfoDictionary;
    }

    public UnknownDictionary getUnknownDictionary() {
        return unknownDictionary;
    }

    public ConnectionCosts getCosts() {
        return connectionCosts;
    }
}
