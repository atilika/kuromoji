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
