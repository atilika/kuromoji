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
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class MultiSearchMerger {

    private int baseCost;
    private List<Integer> suffixCostLowerBounds;
    private int maxCount;
    private int costSlack;

    public MultiSearchMerger(int maxCount, int costSlack) {
        this.maxCount = maxCount;
        this.costSlack = costSlack;
    }

    public MultiSearchResult merge(List<MultiSearchResult> results) {
        if (results.isEmpty()) {
            return new MultiSearchResult();
        }

        suffixCostLowerBounds = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            suffixCostLowerBounds.add(0);
        }
        suffixCostLowerBounds.set(suffixCostLowerBounds.size() - 1, results.get(results.size() - 1).getCost(0));
        for (int i = results.size() - 2; i >= 0; i--) {
            suffixCostLowerBounds.set(i, results.get(i).getCost(0) + suffixCostLowerBounds.get(i + 1));
        }
        baseCost = suffixCostLowerBounds.get(0);

        MultiSearchResult ret = new MultiSearchResult();
        List<MergeBuilder> builders = new ArrayList<>();
        for (int i = 0; i < results.get(0).size(); i++) {
            if (getCostLowerBound(results.get(0).getCost(i), 0) - baseCost > costSlack || i == maxCount) {
                break;
            }

            MergeBuilder newBuilder = new MergeBuilder(results);
            newBuilder.add(i);
            builders.add(newBuilder);
        }

        for (int i = 1; i < results.size(); i++) {
            builders = mergeStep(builders, results, i);
        }

        for (MergeBuilder builder : builders) {
            ret.add(builder.buildList(), builder.getCost());
        }

        return ret;
    }

    private List<MergeBuilder> mergeStep(List<MergeBuilder> builders, List<MultiSearchResult> results, int currentIndex) {
        MultiSearchResult nextResult = results.get(currentIndex);
        PriorityQueue<MergePair> pairHeap = new PriorityQueue<>();
        List<MergeBuilder> ret = new ArrayList<>();

        if (builders.isEmpty() || nextResult.size() == 0) {
            return ret;
        }

        pairHeap.add(new MergePair(0, 0, builders.get(0).getCost() + nextResult.getCost(0)));

        Set<Integer> visited = new HashSet<>();

        while (ret.size() < maxCount && pairHeap.size() > 0) {
            MergePair top = pairHeap.poll();

            if (getCostLowerBound(top.getCost(), currentIndex) - baseCost > costSlack) {
                break;
            }

            int i = top.getLeftIndex(), j = top.getRightIndex();

            MergeBuilder nextBuilder = new MergeBuilder(results, builders.get(i).getIndices());
            nextBuilder.add(j);
            ret.add(nextBuilder);

            if (i + 1 < builders.size()) {
                MergePair newMergePair = new MergePair(i + 1, j, builders.get(i + 1).getCost() + nextResult.getCost(j));
                int positionValue = getPositionValue(i + 1, j);
                if (!visited.contains(positionValue)) {
                    pairHeap.add(newMergePair);
                    visited.add(positionValue);
                }
            }
            if (j + 1 < nextResult.size()) {
                MergePair newMergePair = new MergePair(i, j + 1, builders.get(i).getCost() + nextResult.getCost(j + 1));
                int positionValue = getPositionValue(i, j + 1);
                if (!visited.contains(positionValue)) {
                    pairHeap.add(newMergePair);
                    visited.add(positionValue);
                }
            }
        }

        return ret;
    }

    private int getPositionValue(int i, int j) {
        return (maxCount + 1) * i + j;
    }

    private int getCostLowerBound(int currentCost, int index) {
        if (index + 1 < suffixCostLowerBounds.size()) {
            return currentCost + suffixCostLowerBounds.get(index + 1);
        }
        return currentCost;
    }

    private class MergeBuilder implements Comparable<MergeBuilder> {
        private int cost;
        private List<Integer> indices;
        private List<MultiSearchResult> results;

        public MergeBuilder(List<MultiSearchResult> results) {
            this.results = results;
            cost = 0;
            indices = new ArrayList<>();
        }

        public MergeBuilder(List<MultiSearchResult> results, List<Integer> indices) {
            this(results);
            for (Integer index : indices) {
                add(index);
            }
        }

        public List<ViterbiNode> buildList() {
            List<ViterbiNode> ret = new ArrayList<>();
            for (int i = 0; i < indices.size(); i++) {
                ret.addAll(results.get(i).getTokenizedResult(indices.get(i)));
            }
            return ret;
        }

        public void add(int index) {
            indices.add(index);
            cost += results.get(indices.size() - 1).getCost(index);
        }

        public int getCost() {
            return cost;
        }

        public List<Integer> getIndices() {
            return indices;
        }

        public int compareTo(MergeBuilder o) {
            return cost - o.cost;
        }
    }

    private class MergePair implements Comparable<MergePair> {
        private int leftIndex;
        private int rightIndex;
        private int cost;

        public MergePair(int leftIndex, int rightIndex, int cost) {
            this.leftIndex = leftIndex;
            this.rightIndex = rightIndex;
            this.cost = cost;
        }

        public int getLeftIndex() {
            return leftIndex;
        }

        public int getRightIndex() {
            return rightIndex;
        }

        public int getCost() {
            return cost;
        }

        public int compareTo(MergePair o) {
            return cost - o.getCost();
        }
    }
}
