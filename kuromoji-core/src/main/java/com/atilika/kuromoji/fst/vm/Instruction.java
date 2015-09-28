package com.atilika.kuromoji.fst.vm;

public class Instruction {

    private byte opcode;

    private char arg1;

    private int arg2;

    private int arg3; // used as a output for a FST arc

    @Override
    public String toString() {
        return "Instruction{" +
                "opcode=" + opcode +
                ", arg1=" + arg1 +
                ", arg2=" + arg2 +
                ", arg3=" + arg3 +
                '}';
    }

    public void setOpcode(byte opcode) {
        this.opcode = opcode;
    }

    public void setArg1(char arg1) {
        this.arg1 = arg1;
    }

    public void setArg2(int arg2) {
        this.arg2 = arg2;
    }

    public void setArg3(int arg3) {
        this.arg3 = arg3;
    }

    public byte getOpcode() {
        return this.opcode;
    }

    public char getArg1() {
        return arg1;
    }

    public int getArg2() {
        return arg2;
    }

    public int getArg3() {
        return arg3;
    }
}
