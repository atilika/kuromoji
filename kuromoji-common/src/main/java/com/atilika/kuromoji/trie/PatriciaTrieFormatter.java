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

import com.atilika.kuromoji.trie.PatriciaTrie.KeyMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Utility class to format a {@link PatriciaTrie} to dot format for debugging, inspection, etc.
 *
 * @param <V>
 *
 * See @see <a href="http://graphviz.org/">Graphviz</a>
 */
public class PatriciaTrieFormatter<V> {

    private final static String FONT_NAME = "Helvetica";

    /**
     * Constructor
     */
    public PatriciaTrieFormatter() {
    }

    /**
     * Format trie
     *
     * @param trie
     * @return formatted trie
     */
	public String format(PatriciaTrie<V> trie) {
    	return format(trie, true);
    }

    /**
     * Format trie
     *
     * @param trie
     * @param formatBitString
     * @return formatted trie
     */
    public String format(PatriciaTrie<V> trie, boolean formatBitString) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(formatHeader());
    	sb.append(formatNode(trie.getRoot().getLeft(), -1, trie.getKeyMapper(), formatBitString));
    	sb.append(formatTrailer());
    	return sb.toString();
    }

    /**
     * Format trie and write to file
     *
     * @param trie
     * @param file
     * @throws FileNotFoundException
     */
    public void format(PatriciaTrie<V> trie, File file) throws FileNotFoundException {
    	format(trie, file, false);
    }

    /**
     * Format trie and write to file
     *
     * @param trie
     * @param file
     * @param formatBitString
     * @throws FileNotFoundException
     */
    public void format(PatriciaTrie<V> trie, File file, boolean formatBitString) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(new FileOutputStream(file));
		writer.println(format(trie, formatBitString));
		writer.close();
    }

    /**
     * Format header
     *
     * @return formatted header
     */
    private String formatHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph patricia {\n");
//      sb.append("graph [ fontsize=30 labelloc=\"t\" label=\"\" splines=true overlap=false ];\n");
//      sb.append("# A2 paper size\n");
//      sb.append("size = \"34.4,16.5\";\n");
//      sb.append("# try to fill paper\n");
//      sb.append("ratio = fill;\n");
//      sb.append("edge [ fontname=\"" + FONT_NAME + "\" fontcolor=\"red\" color=\"#606060\" ]\n");
        sb.append("nodesep=1.5;");
        sb.append("node [ style=\"filled\" fillcolor=\"#e8e8f0\" shape=\"Mrecord\" fontname=\"" + FONT_NAME + "\" ]\n");
//      sb.append("edge [ fontname=\"" + FONT_NAME + "\" fontcolor=\"red\" color=\"#606060\" ]\n");
//      sb.append("node [ shape=\"circle\" ]\n");
        return sb.toString();
    }

    /**
     * Format trailer
     *
     * @return formatted trailer
     */
    private String formatTrailer() {
        return "}";
    }

    /**
     * Format node
     *
     * @param node
     * @param bit
     * @return formatted node
     */
    private String formatNode(PatriciaNode<V> node, int bit, KeyMapper<String> keyMapper, boolean formatBitString) {
		if (node.getBit() <= bit) {
			return "";
		} else {
			StringBuffer sb = new StringBuffer();
            sb.append("\"");
            sb.append(getNodeId(node));
            sb.append("\"");
            sb.append(" [ ");
            sb.append("label=");
            sb.append(formatNodeLabel(node, keyMapper, formatBitString));
            sb.append(" ]");
            sb.append("\n");

			sb.append(formatPointer(node, node.getLeft(), "l", "sw"));
			sb.append(formatPointer(node, node.getRight(), "r", "se"));

			sb.append(formatNode(node.getLeft(), node.getBit(), keyMapper, formatBitString));
			sb.append(formatNode(node.getRight(), node.getBit(), keyMapper, formatBitString));

			return sb.toString();
		}
	}

    /**
     * Format link between nodes
     *
     * @param from
     * @param to
     * @param label
     * @param tailport
     * @return formatter node
     */
    private String formatPointer(PatriciaNode<V> from, PatriciaNode<V> to, String label, String tailport) {
        StringBuilder sb = new StringBuilder();
        sb.append(getNodeId(from));
        sb.append(" -> ");
        sb.append(getNodeId(to));
        sb.append(" [ ");
        sb.append("label=\"");
        sb.append(label);
        sb.append(" \"");
        sb.append("tailport=\"");
        sb.append(tailport);
        sb.append(" \"");
        sb.append("fontcolor=\"#666666\" ");
        sb.append(" ]");
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Format node label
     *
     * @param node
     * @return formatted label
     */
    private String formatNodeLabel(PatriciaNode<V> node, KeyMapper<String> keyMapper, boolean formatBitString) {
        StringBuilder sb = new StringBuilder();
        sb.append("<<table border=\"0\" cellborder=\"0\">");
        // Key
        sb.append("<tr><td>");
        sb.append("key: <font color=\"#00a000\">");
        sb.append(getNodeLabel(node));
        sb.append("</font> </td></tr>");

        // Critical bit
        sb.append("<tr><td>");
        sb.append("bit: <font color=\"blue\">");
        sb.append(node.getBit());
        sb.append("</font> </td></tr>");

        // Bit string
        if (formatBitString) {
        	sb.append("<tr><td>");
        	sb.append("bitString: <font color=\"blue\">");
        	String bitString = keyMapper.toBitString(node.getKey());
        	int c = node.getBit() + node.getBit() / 4;
        	sb.append(bitString.substring(0, c));
        	sb.append("<font color=\"red\">");
        	sb.append(bitString.charAt(c));
        	sb.append("</font>");
        	sb.append(bitString.substring(c + 1));
        	sb.append("</font> </td></tr>");
        }

        // Value
        sb.append("<tr><td>");
        sb.append("value: <font color=\"#00a0a0\">");
        sb.append(node.getValue());
        sb.append("</font> </td></tr>");

        sb.append("</table>>");
        return sb.toString();
    }

    /**
     * @param node
     * @return label
     */
    private String getNodeLabel(PatriciaNode<V> node) {
    	return node.getKey();
    }

    /**
     * Get node id used to distinguish nodes internally
     *
     * @param node
     * @return node id
     */
    private String getNodeId(PatriciaNode<V> node) {
    	if (node == null) {
    		return "null";
    	} else {
    		return node.getKey();
    	}
    }
}
