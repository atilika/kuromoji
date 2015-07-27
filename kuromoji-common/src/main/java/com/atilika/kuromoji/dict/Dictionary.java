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
package com.atilika.kuromoji.dict;

public interface Dictionary {

    public static final char INTERNAL_SEPARATOR = '\u0000';

    /**
     * Get left id of specified word
     *
     * @param wordId  word id to get left id cost for
     * @return left id cost
     */
    public int getLeftId(int wordId);

    /**
     * Get right id of specified word
     *
     * @param wordId  word id to get right id cost for
     * @return right id cost
     */
    public int getRightId(int wordId);

    /**
     * Get word cost of specified word
     *
     * @param wordId   word id to get word cost for
     * @return word cost
     */
    public int getWordCost(int wordId);

    /**
     * Get all features of tokens
     *
     * @param wordId word ID of token
     * @return All features of the token
     */
    public String getAllFeatures(int wordId);

    /**
     * Get all features as array
     *
     * @param wordId word ID of token
     * @return Array containing all features of the token
     */
    public String[] getAllFeaturesArray(int wordId);

    /**
     * Get feature(s) of tokens
     *
     * @param wordId word ID token
     * @param fields array of index. If this is empty, return all features.
     * @return Features of the token
     */
    public String getFeature(int wordId, int... fields);
}
