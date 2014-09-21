package org.uct.cs.simplify;

import org.uct.cs.simplify.filebuilder.PLYDataCompressor;
import org.uct.cs.simplify.util.StatRecorder;

import java.io.File;
import java.io.IOException;

public class Compressor
{
    public static void main(String[] args) throws IOException, InterruptedException
    {
        File input = new File("temp/compressor/test.ply");
        File output = new File("temp/compressor/o.dat");

        try(StatRecorder ignored = new StatRecorder())
        {
            PLYDataCompressor.compress(input, output);
        }
    }
}
