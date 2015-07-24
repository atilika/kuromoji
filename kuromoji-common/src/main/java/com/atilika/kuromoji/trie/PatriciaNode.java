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
 * Nodes used in a {@link PatriciaTrie} containing a String key and payload data
 *
 * @param <V>
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
	 * Constructor
	 *
	 * @param key
	 * @param value
	 * @param bit
	 */
	public PatriciaNode(String key, V value, int bit) {
		this.key = key;
		this.value = value;
		this.bit = bit;
	}

	/**
	 * @return key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return payload value
	 */
	public V getValue() {
		return value;
	}

	/**
	 * Set value
	 *
	 * @param value
	 */
	public void setValue(V value) {
		this.value = value;
	}

	/**
	 * @return critical bit index (from left)
	 */
	public int getBit() {
		return bit;
	}

	/**
	 * @return left node
	 */
	public PatriciaNode<V> getLeft() {
		return left;
	}

	/**
	 * @return right node
	 */
	public PatriciaNode<V> getRight() {
		return right;
	}

	/**
	 * Set left node
	 *
	 * @param left node
	 */
	public void setLeft(PatriciaNode<V> left) {
		this.left = left;
	}

	/**
	 * Set right node
	 *
	 * @param right node
	 */
	public void setRight(PatriciaNode<V> right) {
		this.right = right;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("key: " + key);
		sb.append(", ");
		sb.append("bit: " + bit);
		sb.append(", ");
//		sb.append("bitString: " + StringKeyMapper.toBitString(key));
//		sb.append(", ");
		sb.append("value: " + value);
		sb.append(", ");
		if (left != null) {
			sb.append("left: " + left.getKey());
		} else {
			sb.append("left: null");
		}
		sb.append(", ");
		if (right != null) {
			sb.append("right: " + right.getKey());
		} else {
			sb.append("right: null");
		}
		return sb.toString();
	}
}
