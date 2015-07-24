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
package com.atilika.kuromoji.trie;

/**
 * Nodes used in a {@link PatriciaTrie} containing a String key and associated value data
 *
 * @param <V>  value type
 */
public class PatriciaNode<V> {

    /** This node's key */
    private String key;

    /** This node's value */
    private V value;

    /** Critical bit */
    private int bit;

    /** Left node */
    private PatriciaNode<V> left = null;

    /** Right node */
    private PatriciaNode<V> right = null;

    /**
     * Constructs a new node
     *
     * @param key  this node's key
     * @param value  this node's value
     * @param bit  this node's critical bit
     */
    public PatriciaNode(String key, V value, int bit) {
        this.key = key;
        this.value = value;
        this.bit = bit;
    }

    /**
     * Get this node's key
     *
     * @return key, not null
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns this node's value
     *
     * @return payload value
     */
    public V getValue() {
        return value;
    }

    /**
     * Sets this node's value
     *
     * @param value  value to set
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Returns this node's critical bit index
     *
     * @return critical bit index (from left/MSB)
     */
    public int getBit() {
        return bit;
    }

     /**
     * Returns this node's left node
     *
     * @return left node
     */
    public PatriciaNode<V> getLeft() {
        return left;
    }

    /**
     * Returns this node's right node
     *
     * @return right node
     */
    public PatriciaNode<V> getRight() {
        return right;
    }

    /**
     * Set this node's left node
     *
     * @param left  left node
     */
    public void setLeft(PatriciaNode<V> left) {
        this.left = left;
    }

    /**
     * Set this node's right node
     *
     * @param right  right node
     */
    public void setRight(PatriciaNode<V> right) {
        this.right = right;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("key: " + key);
        builder.append(", ");
        builder.append("bit: " + bit);
        builder.append(", ");
//		builder.append("bitString: " + StringKeyMapper.toBitString(key));
//		builder.append(", ");
        builder.append("value: " + value);
        builder.append(", ");
        if (left != null) {
            builder.append("left: " + left.getKey());
        } else {
            builder.append("left: null");
        }
        builder.append(", ");
        if (right != null) {
            builder.append("right: " + right.getKey());
        } else {
            builder.append("right: null");
        }
        return builder.toString();
    }
}
