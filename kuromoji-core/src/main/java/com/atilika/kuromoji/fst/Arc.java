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

public class Arc {
    char label;
    int output = 0;
    State destination;

    public Arc(int output, State destination, char label) {
        this.output = output;
        this.destination = destination;
        this.label = label;
    }

    public Arc(State destination) {
        this.destination = destination;
    }

    public State getDestination() {
        return this.destination;
    }

    public int getOutput() {
        return this.output;
    }

    public char getLabel() {
        return this.label;
    }

    public void setOutput(Integer output) {this.output = output;}

    public void setLabel(char label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Arc arc = (Arc) o;

        if (label != arc.label) return false;
        if (output != arc.output) return false;
        if (destination != null) {
            if (!destination.equals(arc.destination)) return false;
        } else {
            if (arc.destination != null) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) label;
        result = 31 * result + output;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        return result;
    }
}
