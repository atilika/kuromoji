/**
 * Copyright © 2010-2017 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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
package com.atilika.kuromoji.viterbi;

import java.util.ArrayList;
import java.util.List;

public class MultiSearchResult {
    private List<List<ViterbiNode>> tokenizedResults;
    private List<Integer> costs;

    public MultiSearchResult() {
        tokenizedResults = new ArrayList<>();
        costs = new ArrayList<>();
    }

    public void add(List<ViterbiNode> tokenizedResult, int cost) {
        tokenizedResults.add(tokenizedResult);
        costs.add(cost);
    }

    public List<ViterbiNode> getTokenizedResult(int index) {
        return tokenizedResults.get(index);
    }

    public List<List<ViterbiNode>> getTokenizedResultsList() {
        return tokenizedResults;
    }

    public int getCost(int index) {
        return costs.get(index);
    }

    public int size() {
        return costs.size();
    }
}
