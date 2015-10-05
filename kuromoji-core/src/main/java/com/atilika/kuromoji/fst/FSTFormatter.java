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
package com.atilika.kuromoji.fst;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FSTFormatter {

    private final static String FONT_NAME = "Helvetica";

    public String format(Builder builder, String outFileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatHeader());
        sb.append(formatHashedNodes(builder));
        sb.append(formatTrailer());

        try {
            FileWriter fw = new FileWriter(outFileName, false);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e){
            System.out.println(e);
        }

        return "";
    }

    private String formatHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph fst {\n");
        sb.append("graph [ fontsize=30 labelloc=\"t\" label=\"\" splines=true overlap=false rankdir = \"LR\" ];\n");
        sb.append("# A2 paper size\n");
        sb.append("size = \"34.4,16.5\";\n");
        sb.append("# try to fill paper\n");
        sb.append("ratio = fill;\n");
        sb.append("edge [ fontname=\"" + FONT_NAME + "\" fontcolor=\"red\" color=\"#606060\" ]\n");
        sb.append("node [ peripheries=2 style=\"filled\" fillcolor=\"#e8e8f0\" shape=\"Mrecord\" fontsize=40 fontname=\"" + FONT_NAME + "\" ]\n");

        return sb.toString();
    }

    private String formatTrailer() {
        return "}";
    }

    private String formatHashedNodes(Builder builder) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatState(builder.getStartState())); // format the start state

        ArrayList<State> stateArrayList = new ArrayList<State>();
        stateArrayList.add(builder.getStartState());

        while (!stateArrayList.isEmpty()) {
            State state = stateArrayList.get(0);
            if (state.arcs.size() == 0 || state.visited) {
                stateArrayList.remove(0);
                continue;
            }
            for (char transition : state.getAllTransitionStrings()) {
                Arc arc = state.findArc(transition);
                State toState = arc.getDestination();
                stateArrayList.add(toState);

                if (toState.isFinal()) {
                    sb.append(formatFinalState(toState));
                }
                else {
                    sb.append(formatState(toState));
                }
                Integer arcOutput = arc.getOutput();
                sb.append(formatEdge(state, toState, transition, arcOutput.toString(), "fontsize=40"));
            }
            state.visited = true;
            stateArrayList.remove(0);
        }
        return sb.toString();

    }

    private String formatState(State state) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(getNodeId(state));
        sb.append("\"");
        sb.append(" [ ");
        sb.append("label=");
        sb.append(formatStateLabel(state));
        sb.append(" ]");
        return sb.toString();
    }

    private String formatFinalState(State state) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(getNodeId(state));
        sb.append("\"");
        sb.append(" [ ");
        sb.append("fillcolor=pink ");
        sb.append("label=");
        sb.append(formatFinalStateLabel(state));
        sb.append(" ]");
        return sb.toString();
    }

    private String formatStateLabel(State state) {
        StringBuilder sb = new StringBuilder();
        sb.append("<<table border=\"0\" cellborder=\"0\">");
        sb.append("<tr><td>");
        sb.append("Node");
        sb.append("</td></tr>");
        sb.append("<tr><td>");
        sb.append("<font color=\"blue\">");
        sb.append("Normal State");
        sb.append("</font>");
        sb.append("</td></tr>");
        sb.append("</table>>");
        return sb.toString();
    }

    private String formatFinalStateLabel(State state) {
        StringBuilder sb = new StringBuilder();
        sb.append("<<table border=\"0\" cellborder=\"0\">");
        sb.append("<tr><td>");
        sb.append("Node");
        sb.append("</td></tr>");
        sb.append("<tr><td>");
        sb.append("<font color=\"blue\">");
        sb.append("Accepting State");
        sb.append("</font>");
        sb.append("</td></tr>");
        sb.append("</table>>");
        return sb.toString();
    }


    private String formatEdge(State from, State to, char transition, String output, String attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append(getNodeId(from));
        sb.append(" -> ");
        sb.append(getNodeId(to));
        sb.append(" [ ");
        sb.append("label=\"");
        sb.append(transition + "/");
        sb.append(output);
        sb.append("\"");
        sb.append(" ");
        sb.append(attributes);
        sb.append(" ");
        sb.append(" ]");
        sb.append("\n");
        return sb.toString();
    }

    private String getNodeId(State node) {
        return String.valueOf(node.hashCode());
    }

}
