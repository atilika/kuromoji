package com.atilika.kuromoji.fst.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Program {

    public static final int CACHED_CHAR_RANGE = 1 << 16; // 2bytes, range of whole char type.

    private List<Long> instructions = new ArrayList<>(); // list of instructions
    public int[] cacheFirstAddresses; // 4 bytes * 66536 = 262,144 ~= 262KB
    public int[] cacheFirstOutputs;  // 262KB
    public boolean[] cacheFirstIsAccept; // 1 bit * 66536 = 66536 bits = 8317 bits ~= 8KB

    public Program() {
        this.cacheFirstAddresses = new int[CACHED_CHAR_RANGE];
        Arrays.fill(this.cacheFirstAddresses, -1);
        this.cacheFirstOutputs = new int[CACHED_CHAR_RANGE];
        this.cacheFirstIsAccept = new boolean[CACHED_CHAR_RANGE];
    }

    public long getInstruction(int pc) {
        return instructions.get(pc);
    }

    public boolean isAccept(long instruction) {
        return (instruction & 0x0000008000000000L) != 0;
    }

    public boolean isFail(long instruction) {
        return (instruction & 0x8000000000000000L) != 0;
    }

    public char getLabel(long instruction) {
        return (char) (instruction & 0x000000000000FFFFL);
    }

    public int getTargetAddress(long instruction) {
        return isFail(instruction) ? -1 : (int) ((instruction >> 40) & 0x00000000007FFFFFL);
    }

    public int getOutput(long instruction) {
        return (instruction & 0x0000004000000000L) != 0 ? -1 : (int) ((instruction >> 16) & 0x00000000007FFFFFL);
    }

    public void addInstruction(boolean fail, boolean accept, char label, int targetAddress, int output) {

        assert (targetAddress < 0x7FFFFFFF);
        assert (output < 0x7FFFFFFF);

        // encode the instruction into a 8 byte long
        // 3 bytes == target address for 23 bits, msb == fail
        // 3 bytes == output for 23 bits, msb == accept
        // 2 bytes == label
        long value = fail ? 0x8000000000000000L : 0x0000000000000000L;
        value |= accept ? 0x0000008000000000L : 0x0000000000000000L;
        value |= ((long) label & 0x000000000000FFFFL);
        value |= ((long) targetAddress & 0x00000000007FFFFFL) << 40;
        value |= ((long) output & 0x00000000007FFFFFL) << 16;

        instructions.add(value);
    }

    public void addInstructionFail() {
        addInstruction(true, false, ' ', -1, 0); // Ideally, compress this
    }

    public void addInstructionMatch(char label, int targetAddress, int output) {
        addInstruction(false, false, label, targetAddress, output);
    }

    public void addInstructionMatchOrAccept(char label, int targetAddress, int output) {
        addInstruction(false, true, label, targetAddress, output);
    }

    public int[] getCacheFirstAddresses() {
        return this.cacheFirstAddresses;
    }

    public int[] getCacheFirstOutputs() {
        return this.cacheFirstOutputs;
    }

    public int getInstructionCount() {
        return instructions.size();
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
        DataOutputStream dos = new DataOutputStream(output);
        dos.writeInt(instructions.size());
        for (Long instruction : instructions) {
            dos.writeLong(instruction);
        }
        dos.close();
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
        instructions.clear();
        dis.readInt();

        // TODO: replace with fastest nio long array read technique
        /*
        byte[] byteArray = new byte[longCount * 8];
        FileInputStream fis = new FileInputStream("lotsoflongs");
        fis.read(byteArray);
        fis.close();
        for (int i = 0; i < longCount; i += 8)
            longArray[i >> 3] = ((long) byteArray[0+i]        << 56) +
                ((long)(byteArray[1+i] & 255) << 48) +
                ((long)(byteArray[2+i] & 255) << 40) +
                ((long)(byteArray[3+i] & 255) << 32) +
                ((long)(byteArray[4+i] & 255) << 24) +
                ((byteArray[5+i] & 255) << 16) +
                ((byteArray[6+i] & 255) <<  8) +
                ((byteArray[7+i] & 255) <<  0);
        */

        try {
            while (true) {
                instructions.add(dis.readLong());
            }
        } catch (EOFException e) {
            // ignored
        }
        dis.close();

        storeCache();
    }

    /**
     * How big is the program
     *
     * @return
     */
    public int size() {
        return instructions.size();
    }

    /**
     * Cache outgoing arcs from the starting state
     */
    public void storeCache() {
        int pc = instructions.size() - 1;

        // Retrieving through the arcs from the starting state
        while (true) {
            long instruction = getInstruction(pc);
            if (isFail(instruction)) {
                break;
            }
            int indice = getLabel(instruction);
            cacheFirstAddresses[indice] = getTargetAddress(instruction);
            cacheFirstOutputs[indice] = getOutput(instruction);
            cacheFirstIsAccept[indice] = isAccept(instruction);
            pc--;
        }
    }
}
