/**
 * This software is the property of Atilika Inc.  It can only be used
 * as described in the Software License Agreement and in no other way.
 *
 * Copyright © 2012 Atilika Inc.  All rights reserved.
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
