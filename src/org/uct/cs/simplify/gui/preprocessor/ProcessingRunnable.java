package org.uct.cs.simplify.gui.preprocessor;

import org.uct.cs.simplify.FileBuilder;
import org.uct.cs.simplify.gui.util.ProgressBarProgressReporter;
import org.uct.cs.simplify.splitter.memberships.MultiwayVariableKDTreeMembershipBuilder;
import org.uct.cs.simplify.util.ICompletionListener;
import org.uct.cs.simplify.util.StatRecorder;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class ProcessingRunnable implements Runnable
{
    private final JProgressBar progressBar;
    private final File inputFile, outputFile;
    private final boolean swapYZ;
    private final ICompletionListener listener;

    public ProcessingRunnable(File inputFile, File outputFile, boolean swapYZ, JProgressBar progressBar, ICompletionListener listener)
    {
        this.progressBar = progressBar;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.swapYZ = swapYZ;
        this.listener = listener;
    }

    @Override
    public void run()
    {
        boolean success = false;
        try (StatRecorder ignored = new StatRecorder(500))
        {
            FileBuilder.run(
                this.inputFile,
                this.outputFile,
                false,
                this.swapYZ,
                false,
                new MultiwayVariableKDTreeMembershipBuilder(4),
                new ProgressBarProgressReporter(this.progressBar, "Processing")
            );
            success = true;
        }
        catch (IOException | InterruptedException | RuntimeException e)
        {
            e.printStackTrace();
        }

        if (this.listener != null) this.listener.callback(success);
    }
}
