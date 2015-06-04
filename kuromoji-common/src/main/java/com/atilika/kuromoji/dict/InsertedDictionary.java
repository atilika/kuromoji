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

public class InsertedDictionary implements Dictionary {

    private static final String INSERTED_FEATURE_SET = "*,*,*,*,*,*,*,*,*";

    @Override
    public int getLeftId(int wordId) {
        return 0;
    }

    @Override
    public int getRightId(int wordId) {
        return 0;
    }

    @Override
    public int getWordCost(int wordId) {
        return 0;
    }

    @Override
    public String getAllFeatures(int wordId) {
        return INSERTED_FEATURE_SET;
    }

    @Override
    public String[] getAllFeaturesArray(int wordId) {
        String[] features = new String[9];
        for (int i = 0; i < features.length; i++) {
            features[i] = "*";
        }
        return features;
    }

    @Override
    public String getPartOfSpeech(int wordId) {
        return null;
    }

    @Override
    public String getReading(int wordId) {
        return null;
    }

    @Override
    public String getBaseForm(int wordId) {
        return null;
    }

    @Override
    public String getFeature(int wordId, int... fields) {
        return null;
    }
}
