package com.atilika.kuromoji.fst.vm;

import com.atilika.kuromoji.fst.FSTBuilder;
import com.atilika.kuromoji.fst.FSTTestHelper;
import org.junit.Ignore;
import org.junit.Test;

public class ProgramTest {

    @Ignore("Enable by providing external dictionary file")
    @Test
    public void testReadProgramFromFile() throws Exception {

        String resource = "ipadic-allwords_uniq_sorted.csv";

        FSTTestHelper fstTestHelper = new FSTTestHelper();
        FSTBuilder fstBuilder = fstTestHelper.readIncremental(resource);

        Program program = fstBuilder.getFstCompiler().getProgram();
        program.outputProgramToFile("fstbytebuffer");


        VirtualMachine vm = new VirtualMachine();
        fstTestHelper.checkOutputWordByWord(resource, program, vm);

        Program readProgram = new Program();
        readProgram.readProgramFromFile("fstbytebuffer");

        fstTestHelper.checkOutputWordByWord(resource, readProgram, vm);
    }
}
