/**
 * Copyright © 2010-2018 Atilika Inc. and contributors (see CONTRIBUTORS.md)
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

import com.atilika.kuromoji.TokenizerBase;
import com.atilika.kuromoji.dict.ConnectionCosts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * An instance of MultiSearcher can be used to extend the search functionality of ViterbiSearcher to find multiple paths ordered by cost.
 * Note that the MultiSearcher uses the value of ViterbiNode.getPathCost() to evaluate the cost of possible path.
 * Therefore, the ViterbiLattice should be updated by ViterbiSearcher.calculatePathCosts() before being used by the MultiSearcher.
 *
 * The implementation is based on Eppstein's algorithm for finding n shortest paths in a weighted directed graph.
 */
public class MultiSearcher {
    private final ConnectionCosts costs;
    private final TokenizerBase.Mode mode;
    private final ViterbiSearcher viterbiSearcher;
    private int baseCost;
    private List<Integer> pathCosts;
    private Map<ViterbiNode, SidetrackEdge> sidetracks;

    public MultiSearcher(ConnectionCosts costs, TokenizerBase.Mode mode, ViterbiSearcher viterbiSearcher) {
        this.costs = costs;
        this.mode = mode;
        this.viterbiSearcher = viterbiSearcher;
    }

    /**
     * Get up to maxCount shortest paths with cost at most OPT + costSlack, where OPT is the optimal solution. The results are ordered in ascending order by cost.
     *
     * @param lattice  an instance of ViterbiLattice prosecced by a ViterbiSearcher
     * @param maxCount  the maximum number of results
     * @param costSlack  the maximum cost slack of a path
     * @return  the shortest paths and their costs
     */
    public MultiSearchResult getShortestPaths(ViterbiLattice lattice, int maxCount, int costSlack) {
        pathCosts = new ArrayList<>();
        sidetracks = new HashMap<>();
        MultiSearchResult multiSearchResult = new MultiSearchResult();
        buildSidetracks(lattice);
        ViterbiNode eos = lattice.getEndIndexArr()[0][0];
        baseCost = eos.getPathCost();
        List<SidetrackEdge> paths = getPaths(eos, maxCount, costSlack);
        int i = 0;
        for (SidetrackEdge path : paths) {
            List<ViterbiNode> nodes = generatePath(eos, path);
            multiSearchResult.add(nodes, pathCosts.get(i));
            i += 1;
        }
        return multiSearchResult;
    }

    private List<ViterbiNode> generatePath(ViterbiNode eos, SidetrackEdge sidetrackEdge) {
        LinkedList<ViterbiNode> result = new LinkedList<>();
        ViterbiNode node = eos;
        result.add(node);
        while (node.getLeftNode() != null) {
            ViterbiNode leftNode = node.getLeftNode();
            if (sidetrackEdge != null && sidetrackEdge.getHead() == node) {
                leftNode = sidetrackEdge.getTail();
                sidetrackEdge = sidetrackEdge.getParent();
            }
            node = leftNode;
            result.addFirst(node);
        }
        return result;
    }

    private List<SidetrackEdge> getPaths(ViterbiNode eos, int maxCount, int costSlack) {
        List<SidetrackEdge> result = new ArrayList<>();
        result.add(null);
        pathCosts.add(baseCost);
        PriorityQueue<SidetrackEdge> sidetrackHeap = new PriorityQueue<>();

        SidetrackEdge sideTrackEdge = sidetracks.get(eos);
        while (sideTrackEdge != null) {
            sidetrackHeap.add(sideTrackEdge);
            sideTrackEdge = sideTrackEdge.getNextOption();
        }

        for (int i = 1; i < maxCount; i++) {
            if (sidetrackHeap.isEmpty()) {
                break;
            }

            sideTrackEdge = sidetrackHeap.poll();
            if (sideTrackEdge.getCost() > costSlack) {
                break;
            }
            result.add(sideTrackEdge);
            pathCosts.add(baseCost + sideTrackEdge.getCost());
            SidetrackEdge nextSidetrack = sidetracks.get(sideTrackEdge.getTail());

            while (nextSidetrack != null) {
                SidetrackEdge next = new SidetrackEdge(nextSidetrack.getCost(), nextSidetrack.getTail(), nextSidetrack.getHead());
                next.setParent(sideTrackEdge);
                sidetrackHeap.add(next);
                nextSidetrack = nextSidetrack.getNextOption();
            }
        }
        return result;
    }

    private void buildSidetracks(ViterbiLattice lattice) {
        ViterbiNode[][] startIndexArr = lattice.getStartIndexArr();
        ViterbiNode[][] endIndexArr = lattice.getEndIndexArr();

        for (int i = 1; i < startIndexArr.length; i++) {
            if (startIndexArr[i] == null || endIndexArr[i] == null) {
                continue;
            }

            for (ViterbiNode node : startIndexArr[i]) {
                if (node == null) {
                    break;
                }

                buildSidetracksForNode(endIndexArr[i], node);
            }
        }
    }

    private void buildSidetracksForNode(ViterbiNode[] leftNodes, ViterbiNode node) {
        int backwardConnectionId = node.getLeftId();
        int wordCost = node.getWordCost();

        List<SidetrackEdge> sidetrackEdges = new ArrayList<>();
        SidetrackEdge nextOption = sidetracks.get(node.getLeftNode());

        for (ViterbiNode leftNode : leftNodes) {
            if (leftNode == null) {
                break;
            }

            if (leftNode.getType() == ViterbiNode.Type.KNOWN && leftNode.getWordId() == -1) { // Ignore BOS
                continue;
            }

            int sideTrackCost = leftNode.getPathCost() - node.getPathCost() + wordCost + costs.get(leftNode.getRightId(), backwardConnectionId);
            if (mode == TokenizerBase.Mode.SEARCH || mode == TokenizerBase.Mode.EXTENDED) {
                sideTrackCost += viterbiSearcher.getPenaltyCost(node);
            }

            if (leftNode != node.getLeftNode()) {
                sidetrackEdges.add(new SidetrackEdge(sideTrackCost, leftNode, node));
            }
        }

        if (sidetrackEdges.isEmpty()) {
            sidetracks.put(node, nextOption);
        } else {
            for (int i = 0; i < sidetrackEdges.size() - 1; i++) {
                sidetrackEdges.get(i).setNextOption(sidetrackEdges.get(i + 1));
            }
            sidetrackEdges.get(sidetrackEdges.size() - 1).setNextOption(nextOption);
            sidetracks.put(node, sidetrackEdges.get(0));
        }
    }

    private class SidetrackEdge implements Comparable<SidetrackEdge> {
        private int cost;
        private ViterbiNode tail, head;
        private SidetrackEdge nextOption;
        private SidetrackEdge parent;

        SidetrackEdge(int cost, ViterbiNode tail, ViterbiNode head) {
            this.cost = cost;
            this.tail = tail;
            this.head = head;
            nextOption = null;
            parent = null;
        }

        public int getCost() {
            return cost;
        }

        ViterbiNode getTail() {
            return tail;
        }

        ViterbiNode getHead() {
            return head;
        }

        public void setNextOption(SidetrackEdge nextOption) {
            this.nextOption = nextOption;
        }

        public SidetrackEdge getNextOption() {
            return nextOption;
        }

        public void setParent(SidetrackEdge parent) {
            this.parent = parent;
            cost += parent.cost;
        }

        public SidetrackEdge getParent() {
            return parent;
        }

        public int compareTo(SidetrackEdge o) {
            return cost - o.getCost();
        }
    }
}
