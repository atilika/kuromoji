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

package com.atilika.kuromoji.entities.dict;

import com.atilika.kuromoji.util.AbstractDictionaryBuilder;
import com.atilika.kuromoji.util.AbstractTokenInfoDictionaryBuilder;

import java.io.IOException;

public class DictionaryBuilder extends AbstractDictionaryBuilder {

    private boolean normalizeEntries;
    private boolean addUnnormalizedEntries;
    private String dictionaryFilter = "";

    @Override
    protected AbstractTokenInfoDictionaryBuilder getTokenInfoDictionaryBuilder(String encoding) {
        return new TokenInfoDictionaryBuilder(encoding, normalizeEntries, addUnnormalizedEntries, dictionaryFilter);
    }

    private void processAdditionalArguments(String[] args) {
        normalizeEntries = Boolean.parseBoolean(args[4]);
        addUnnormalizedEntries = Boolean.parseBoolean(args[5]);

        if (args.length == 7) {
            dictionaryFilter = args[6];
        }
        System.out.println("normalize entries: " + normalizeEntries);
        System.out.println("add unnormalised entries: " + addUnnormalizedEntries);
        System.out.println("dictionary filter: " + dictionaryFilter);
    }

    public static void main(String[] args) throws IOException {
        DictionaryBuilder dictionaryBuilder = new DictionaryBuilder();
        dictionaryBuilder.processAdditionalArguments(args);
        dictionaryBuilder.build(args);
    }
}
