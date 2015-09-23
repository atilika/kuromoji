package com.atilika.kuromoji.fst.vm;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    public Instruction getInstructionAt(int pc) {
        int internalIndex = pc * BYTES_PER_INSTRUCTIONS;
//            short opcode = (short) (instruction.get(internalIndex) << 8 | instruction.get(internalIndex + 1));
//            char arg1 = (char) (instruction.get(internalIndex + 2) << 8 | instruction.get(internalIndex + 3));
//            int arg2 = instruction.get(internalIndex + 4) << 24 | instruction.get(internalIndex + 5) << 16 | instruction.get(internalIndex + 6) << 8 | instruction.get(internalIndex + 7);
//            int arg3 = instruction.get(internalIndex + 8) << 24 | instruction.get(internalIndex + 9) << 16 | instruction.get(internalIndex + 10) << 8 | instruction.get(internalIndex + 11);

        instruction.position(internalIndex);

        Instruction i = new Instruction();
        i.setOpcode(instruction.get());
        i.setArg1(instruction.getChar());
        i.setArg2(instruction.getInt());
        i.setArg3(instruction.getInt());

        return i;
    }

    /**
     * Add an instruction to Bytebuffer. Doubling the size of buffer when the current size is not enough.
     *
     * @param i
     */
    public void addInstruction(Instruction i) {
        addInstruction(i.getOpcode(), i.getArg1(), i.getArg2(), i.getArg3());
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

    public void addInstructions(List<Instruction> instructions) {
        for (Instruction i : instructions) {
            addInstruction(i);
        }
    }

    public List<Instruction> dumpInstructions() {
        List<Instruction> instructions = new ArrayList<>();
        int numInstructions = this.getNumInstructions();
        for (int pc = 0; pc < numInstructions; pc++) {
            instructions.add(this.getInstructionAt(pc));
        }
        return instructions;
    }

    public int[] getCacheFirstAddresses() {return this.cacheFirstAddresses;}

    public int[] getCacheFirstOutputs() {return this.cacheFirstOutputs;}

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
        Instruction i = new Instruction();

        // Retrieving through the arcs from the starting state
        while (i.getOpcode() != Program.FAIL) {
            i = this.getInstructionAt(pc);
            int indice = i.getArg1();
            this.cacheFirstAddresses[indice] = i.getArg2();
            this.cacheFirstOutputs[indice] = i.getArg3();
            this.cacheFirstIsAccept[indice] = i.getOpcode() == Program.ACCEPT_OR_MATCH;
            pc--;
        }
    }
}
