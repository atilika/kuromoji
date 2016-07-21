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
package com.atilika.kuromoji.viterbi;

import com.atilika.kuromoji.TokenizerBase;
import com.atilika.kuromoji.dict.ConnectionCosts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class MultiSearcher {

    private final ConnectionCosts costs;
    private final TokenizerBase.Mode mode;
    private final ViterbiSearcher viterbiSearcher;
    private int baseCost;
    private List<Integer> pathCosts;

    public MultiSearcher(ConnectionCosts costs, TokenizerBase.Mode mode, ViterbiSearcher viterbiSearcher) {
        this.costs = costs;
        this.mode = mode;
        this.viterbiSearcher = viterbiSearcher;
        pathCosts = new ArrayList<>();
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
        MultiSearchResult multiSearchResult = new MultiSearchResult();
        buildSidetrackTrees(lattice);
        ViterbiNode eos = lattice.getEndIndexArr()[0][0];
        baseCost = eos.getPathCost();
        List<SidetrackTreeNode> sidetracks = getPaths(eos.getSidetrackTreeNode(), maxCount, costSlack);
        int i = 0;
        for (SidetrackTreeNode sidetrack : sidetracks) {
            List<ViterbiNode> path = generatePath(eos, sidetrack);
            multiSearchResult.add(path, pathCosts.get(i));
            i += 1;
        }
        return multiSearchResult;
    }

    private List<SidetrackTreeNode> getPaths(SidetrackTreeNode root, int maxCount, int costSlack) {
        List<SidetrackTreeNode> result = new ArrayList<>();
        PriorityQueue<SidetrackTreeNode> sidetrackHeap = new PriorityQueue<>();
        sidetrackHeap.add(root);
        for (int i = 0; i < maxCount; i++) {
            if (sidetrackHeap.isEmpty()) {
                break;
            }
            SidetrackTreeNode node = sidetrackHeap.poll();
            if (node.getCost() > costSlack) {
                break;
            }
            result.add(node);
            pathCosts.add(baseCost + node.getCost());
            for (SidetrackTreeNode child : node.getChildren()) {
                SidetrackTreeNode modifiedChild = new SidetrackTreeNode(child.getSidetrackEdge());
                modifiedChild.addChildren(child.getChildren());
                modifiedChild.setParent(node);
                sidetrackHeap.add(modifiedChild);
            }
        }
        return result;
    }

    private List<ViterbiNode> generatePath(ViterbiNode eos, SidetrackTreeNode sidetrackNode) {
        LinkedList<ViterbiNode> result = new LinkedList<>();
        ViterbiNode node = eos;
        result.add(node);
        while (true) {
            if (node.getLeftNode() == null) {
                break;
            }
            ViterbiNode leftNode = node.getLeftNode();
            if (sidetrackNode != null && sidetrackNode.getSidetrackEdge().getHead() == node) {
                leftNode = sidetrackNode.getSidetrackEdge().getTail();
                sidetrackNode = sidetrackNode.getParent();
            }
            node = leftNode;
            result.addFirst(node);
        }
        return result;
    }

    private void buildSidetrackTrees(ViterbiLattice lattice) {
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

                buildSidetrackTreeNode(endIndexArr[i], node);
            }
        }
    }

    private void buildSidetrackTreeNode(ViterbiNode[] leftNodes, ViterbiNode node) {
        int backwardConnectionId = node.getLeftId();
        int wordCost = node.getWordCost();

        node.setSidetrackTreeNode(new SidetrackTreeNode(new SidetrackEdge(0, null, null)));

        for (ViterbiNode leftNode : leftNodes) {
            if (leftNode == null) {
                return;
            }

            if (leftNode.getType() == ViterbiNode.Type.KNOWN && leftNode.getWordId() == -1) { // Ignore BOS
                continue;
            }

            int sideTrackCost = leftNode.getPathCost() - node.getPathCost() + wordCost + costs.get(leftNode.getRightId(), backwardConnectionId);
            if (mode == TokenizerBase.Mode.SEARCH || mode == TokenizerBase.Mode.EXTENDED) {
                sideTrackCost += viterbiSearcher.getPenaltyCost(node);
            }

            if (leftNode == node.getLeftNode()) {   // Follow optimal path
                node.getSidetrackTreeNode().addChildren(leftNode.getSidetrackTreeNode().getChildren());
            } else {    // Sidetrack
                SidetrackEdge sideTrackEdge = new SidetrackEdge(sideTrackCost, leftNode, node);
                SidetrackTreeNode sideTrackTreeNode = new SidetrackTreeNode(sideTrackEdge);
                sideTrackTreeNode.addChildren(leftNode.getSidetrackTreeNode().getChildren());
                node.getSidetrackTreeNode().addChild(sideTrackTreeNode);
            }

        }
    }

    private class SidetrackEdge {
        private int cost;
        private ViterbiNode tail, head;

        SidetrackEdge(int cost, ViterbiNode tail, ViterbiNode head) {
            this.cost = cost;
            this.tail = tail;
            this.head = head;
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
    }

    class SidetrackTreeNode implements Comparable<SidetrackTreeNode> {
        private SidetrackEdge sidetrackEdge;
        private List<SidetrackTreeNode> children;
        private SidetrackTreeNode parent;
        private int cost;

        SidetrackTreeNode(SidetrackEdge sidetrackEdge) {
            this.sidetrackEdge = sidetrackEdge;
            cost = sidetrackEdge.getCost();
            children = new ArrayList<>();
        }

        SidetrackEdge getSidetrackEdge() {
            return sidetrackEdge;
        }

        void addChild(SidetrackTreeNode child) {
            children.add(child);
        }

        void addChildren(List<SidetrackTreeNode> children) {
            this.children.addAll(children);
        }

        List<SidetrackTreeNode> getChildren() {
            return children;
        }

        public SidetrackTreeNode getParent() {
            return parent;
        }

        public void setParent(SidetrackTreeNode parent) {
            this.cost = parent.getCost() + sidetrackEdge.getCost();
            this.parent = parent;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public int getCost() {
            return cost;
        }

        public int compareTo(SidetrackTreeNode o) {
            return cost - o.getCost();
        }
    }
}
