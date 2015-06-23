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
package com.atilika.kuromoji.trie;

import java.util.ArrayList;
import java.util.List;

/**
 * Normal Trie which is used to build DoubleArrayTrie
 */
public class Trie {

    /**
     * Root node of Trie
     */
    private Node root;

    /**
     * Constructor
     * Initialize Trie with empty root node
     */
    public Trie() {
        root = new Node();
    }

    /**
     * Add input value into Trie
     * Before adding, it adds terminating character(\u0001) to input string
     *
     * @param value String to add to Trie
     */
    public void add(String value) {
        root.add(value, true);
    }

    /**
     * Return root node which contains other nodes
     *
     * @return Node
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Trie Node
     */
    public class Node {
        char key; /// key(char) of this node

        List<Node> children = new ArrayList<Node>(); // Array to hold children nodes

        /**
         * Constructor
         */
        public Node() {
        }

        /**
         * Constructor
         *
         * @param key key for this node
         */
        public Node(char key) {
            this.key = key;
        }

        /**
         * Add string to Trie
         *
         * @param value String to add
         */
        public void add(String value) {
            add(value, false);
        }

        public void add(String value, boolean terminate) {
            if (value.length() == 0) {
                return;
            }

            Node node = addChild(new Node(value.charAt(0)));
            for (int i = 1; i < value.length(); i++) {
                node = node.addChild(new Node(value.charAt(i)));
            }
            if (terminate && (node != null)) {
                node.addChild(new Node(DoubleArrayTrie.TERMINATING_CHARACTER));
            }
        }

        /**
         * Add Node to this node as child
         *
         * @param newNode node to add
         * @return added node. If a node with same key already exists, return that node.
         */
        public Node addChild(Node newNode) {
            Node child = getChild(newNode.getKey());
            if (child == null) {
                children.add(newNode);
                child = newNode;
            }
            return child;
        }

        /**
         * Return the key of the node
         *
         * @return key
         */
        public char getKey() {
            return key;
        }

        /**
         * Check if children following this node has only single path.
         * For example, if you have "abcde" and "abfgh" in Trie, calling this method on node "a" and "b" returns false.
         * Calling this method on "c", "d", "e", "f", "g" and "h" returns true.
         *
         * @return true if it has only single path. false if it has multiple path.
         */
        public boolean hasSinglePath() {
            switch (children.size()) {
                case 0:
                    return true;
                case 1:
                    return children.get(0).hasSinglePath();
                default:
                    return false;
            }
        }

        /**
         * Return children node
         *
         * @return Array of children nodes
         */
        public List<Node> getChildren() {
            return children;
        }

        /**
         * Return node which has input key
         *
         * @param key key to look for
         * @return node which has input key. null if it doesn't exist.
         */
        private Node getChild(char key) {
            for (Node child : children) {
                if (child.getKey() == key) {
                    return child;
                }
            }
            return null;
        }
    }
}
