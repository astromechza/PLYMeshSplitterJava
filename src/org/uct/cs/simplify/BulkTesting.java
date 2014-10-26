package org.uct.cs.simplify;

import org.apache.commons.cli.*;
import org.uct.cs.simplify.splitter.memberships.MembershipBuilder;
import org.uct.cs.simplify.util.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BulkTesting
{
    public static void main(String[] args) throws InterruptedException
    {
        CommandLine cmd = getCmd(args);

        File fi = new File(cmd.getOptionValue("input"));
        File fo = new File(cmd.getOptionValue("output"));

        if (!fi.exists()) throw new RuntimeException("Input file does not exist.");
        if (!fi.isFile()) throw new RuntimeException("Input file is not a file!");
        if (!fo.isDirectory()) throw new RuntimeException("Output folder '" + fo + "' is not a folder");
        if (!fo.exists()) throw new RuntimeException("Output folder does not exist");

        String[] hierarchies = new String[]{
            "octree", "kdtree", "vkdtree", "mkdtree3", "mkdtree4", "mkdtree5"
        };

        if (cmd.hasOption("hierarchies"))
        {
            hierarchies = cmd.getOptionValue("hierarchies").trim().toLowerCase().split(" ");
        }

        System.out.println("Testing: " + String.join(", ", hierarchies));

        for (String hierarchy : hierarchies)
        {
            File o = new File(fo, Useful.getFilenameWithoutExt(fi.getName()) + "_" + hierarchy + ".phf");
            MembershipBuilder mb = MembershipBuilder.get(hierarchy);

            StatRecorder sr = new StatRecorder(500);
            try
            {
                String json = FileBuilder.run(fi, o, false, true, false, mb, new StdOutProgressReporter("process"));
                sr.close();

                File headerFile = new File(fo, Useful.getFilenameWithoutExt(fi.getName()) + "_" + hierarchy + ".json");
                try (FileWriter fw = new FileWriter(headerFile))
                {
                    fw.write(json);
                }

                sr.dump(new File(fo, Useful.getFilenameWithoutExt(fi.getName()) + "_" + hierarchy + ".memdump"));

                TempFileManager.clear(new File(fo, Useful.getFilenameWithoutExt(fi.getName()) + "_" + hierarchy + ".diskdump"));

                try
                {
                    OutputValidator.run(o, true);
                }
                catch (RuntimeException e)
                {
                    System.err.println("Validation Failed!");
                    e.printStackTrace();
                }
            }
            catch (IOException | InterruptedException | RuntimeException e)
            {
                sr.close();
                e.printStackTrace();
            }

            TempFileManager.resetAndForceClear();

            StateHolder.reset();

            System.gc();
            Thread.sleep(1000);
            System.gc();
        }
        Outputter.info1ln(StateHolder.getPrintableIDMapping());
    }

    public static CommandLine getCmd(String[] args)
    {
        CommandLineParser clp = new BasicParser();

        Options options = new Options();

        Option inputFile = new Option("i", "input", true, "path to first PLY file");
        inputFile.setRequired(true);
        options.addOption(inputFile);

        Option outputDir = new Option("o", "output", true, "path to output directory");
        outputDir.setRequired(true);
        options.addOption(outputDir);

        Option hierarchies = new Option("h", "hierarchies", true, "space seperated string of hierarchies");
        options.addOption(hierarchies);

        CommandLine cmd;
        try
        {
            cmd = clp.parse(options, args);
            return cmd;
        }
        catch (ParseException e)
        {
            Outputter.errorf("%s : %s%n%n", e.getClass().getName(), e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("--input <path> --output <folder>", options);
            System.exit(1);
            return null;
        }
    }
}
