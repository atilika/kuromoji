package com.atilika.kuromoji.fst.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

public class Program {

    public static final byte MATCH = 1;
    public static final byte FAIL = 3;
    public static final byte HELLO = 4;
    public static final byte ACCEPT = 5;
    public static final byte ACCEPT_OR_MATCH = 6;
    public final static int BYTES_PER_INSTRUCTIONS = 11;

    int endOfTheProgram; // place of the end of the byte buffer;
    int numInstructionsAllocated = 100000; // counting the first 4 bytes as one psuedo instruction
    public ByteBuffer instruction = ByteBuffer.allocate(BYTES_PER_INSTRUCTIONS * numInstructionsAllocated); // init

    public static final int CACHED_CHAR_RANGE = 1 << 16; // 2bytes, range of whole char type.
    public int[] cacheFirstAddresses; // 4 bytes * 66536 = 262,144 ~= 262KB
    public int[] cacheFirstOutputs;  // 262KB
    public boolean[] cacheFirstIsAccept; // 1 bit * 66536 = 66536 bits = 8317 bits ~= 8KB

    public Program() {
        this.cacheFirstAddresses = new int[CACHED_CHAR_RANGE];
        Arrays.fill(this.cacheFirstAddresses, -1);
        this.cacheFirstOutputs = new int[CACHED_CHAR_RANGE];
        this.cacheFirstIsAccept = new boolean[CACHED_CHAR_RANGE];
    }


    public byte getOpcode(int pc) {
        int internalIndex = pc * BYTES_PER_INSTRUCTIONS;
        return instruction.get(internalIndex);
    }

    public char getArg1(int pc) {
        int internalIndex = pc * BYTES_PER_INSTRUCTIONS;
        return instruction.getChar(internalIndex + 1);
    }

    public int getArg2(int pc) {
        int internalIndex = pc * BYTES_PER_INSTRUCTIONS;
        return instruction.getInt(internalIndex + 1 + 2);
    }

    public int getArg3(int pc) {
        int internalIndex = pc * BYTES_PER_INSTRUCTIONS;
        return instruction.getInt(internalIndex + 1 + 2 + 4);
    }

    public void addInstruction(byte op) {
        addInstruction(op, ' ', -1, 0);
    }

    public void addInstruction(byte op, char label, int targetAddress, int output) {
        int currentSizePlusOneInstruction = (this.getNumInstructions() + 1) * BYTES_PER_INSTRUCTIONS;
        if (currentSizePlusOneInstruction >= BYTES_PER_INSTRUCTIONS * numInstructionsAllocated) {
            doubleBufferSize();
        }

        instruction.put(op);
        instruction.putChar(label);
        instruction.putInt(targetAddress);
        instruction.putInt(output);

        endOfTheProgram += BYTES_PER_INSTRUCTIONS;
    }

    public void addInstructionFail() {
        addInstruction(FAIL, ' ', -1, 0); // Ideally, compress this
    }

    public void addInstructionMatch(char label, int targetAddress, int output) {
        addInstruction(MATCH, label, targetAddress, output);
    }

    public void addInstructionMatchOrAccept(char label, int targetAddress, int output) {
        addInstruction(ACCEPT_OR_MATCH, label, targetAddress, output);
    }

    private void doubleBufferSize() {
        // grow byte array by doubling the size of it.
        numInstructionsAllocated = numInstructionsAllocated << 1;
        ByteBuffer newInstructions = ByteBuffer.allocate(BYTES_PER_INSTRUCTIONS * numInstructionsAllocated);
        instruction.flip(); // limit ← position, position ← 0
        newInstructions.put(instruction);
        instruction = newInstructions;
    }

    public int[] getCacheFirstAddresses() {
        return this.cacheFirstAddresses;
    }

    public int[] getCacheFirstOutputs() {
        return this.cacheFirstOutputs;
    }

    public int getNumInstructions() {
        return this.endOfTheProgram / Program.BYTES_PER_INSTRUCTIONS;
    }

    /**
     * Output the stored bytebuffer FST as a file
     *
     * @param filename
     * @throws IOException
     */
    public void outputProgramToFile(String filename) throws IOException {
        outputProgramToStream(new FileOutputStream(filename));
    }

    public void outputProgramToStream(OutputStream output) throws IOException {
        ByteBuffer bbuf = this.instruction;
        bbuf.rewind();

        bbuf.rewind();
        bbuf.limit(endOfTheProgram);

        DataOutputStream dos = new DataOutputStream(output);
        dos.writeInt(endOfTheProgram);

        // Appeding the whole bytebuffer to the end of the file
        WritableByteChannel wChannel = Channels.newChannel(dos);
        wChannel.write(bbuf);
        wChannel.close();
    }

    /**
     * Read saved FST from a file
     *
     * @param filename
     * @throws IOException
     */
    public void readProgramFromFile(String filename) throws IOException {
        readProgramFromFile(new FileInputStream(filename));
    }

    public void readProgramFromFile(InputStream input) throws IOException {

        DataInputStream dis = new DataInputStream(input);
        int instructionSize = dis.readInt();    // Read size of bytebuffer
        ByteBuffer bbuf = ByteBuffer.allocate(instructionSize);

        // Reading the rest of the bytes
        ReadableByteChannel rChannel = Channels.newChannel(dis);
        rChannel.read(bbuf);
        this.instruction = bbuf;
        this.endOfTheProgram = instructionSize;

        rChannel.close();

        storeCache();
    }

    /**
     * Cache outgoing arcs from the starting state
     */
    public void storeCache() {
        int pc = this.endOfTheProgram / Program.BYTES_PER_INSTRUCTIONS - 1;
        byte opcode = Program.HELLO;

        // Retrieving through the arcs from the starting state
        while (opcode != Program.FAIL) {
            int indice = this.getArg1(pc);
            this.cacheFirstAddresses[indice] = this.getArg2(pc);
            this.cacheFirstOutputs[indice] = this.getArg3(pc);
            opcode = this.getOpcode(pc);
            this.cacheFirstIsAccept[indice] = opcode == Program.ACCEPT_OR_MATCH;
            pc--;
        }
    }
}
