package com.atilika.kuromoji.fst.vm;

import com.atilika.kuromoji.fst.FSTCompiler;
import com.atilika.kuromoji.fst.vm.Instruction;
import com.atilika.kuromoji.fst.vm.Program;
import com.atilika.kuromoji.fst.vm.VirtualMachine;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class VirtualMachineTest {

    @Test
    public void testHelloVM()
    {
        VirtualMachine vm = new VirtualMachine(false);
        Program program = new Program();

        Instruction instruction = new Instruction();
        instruction.setOpcode(Program.HELLO);

        Instruction instructionFail = new Instruction();
        instructionFail.setOpcode(Program.FAIL);

        program.addInstruction(instruction);
        program.addInstruction(instructionFail);

        vm.run(program, "");

    }

    @Test
    public void testMatch() throws Exception {
        // testing the input string "a" being accepted or not
        VirtualMachine vm = new VirtualMachine(false);
        Program program = new Program();
        Instruction instructionMatch = new Instruction();
        instructionMatch.setOpcode(Program.MATCH);
        instructionMatch.setArg1('a'); // transition string
        instructionMatch.setArg2(1);  // target address, delta coded
        instructionMatch.setArg3(1); // output, value to be accumulated;

        Instruction instructionAccept = new Instruction();
        instructionAccept.setOpcode(Program.ACCEPT);

        program.addInstructions(
                Arrays.asList(instructionAccept, instructionMatch)
        );

        assertEquals(1, vm.run(program, "a"));
    }
}