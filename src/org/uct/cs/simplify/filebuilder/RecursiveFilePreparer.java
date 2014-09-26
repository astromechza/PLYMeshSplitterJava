package org.uct.cs.simplify.filebuilder;

import org.uct.cs.simplify.ply.header.PLYHeader;
import org.uct.cs.simplify.simplifier.SimplifierWrapper;
import org.uct.cs.simplify.splitter.NodeSplitter;
import org.uct.cs.simplify.splitter.memberships.IMembershipBuilder;
import org.uct.cs.simplify.splitter.memberships.VariableKDTreeMembershipBuilder;
import org.uct.cs.simplify.stitcher.NaiveMeshStitcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecursiveFilePreparer
{
    public static PHFNode prepare(PHFNode inputNode, int maxdepth)
    throws IOException, InterruptedException
    {
        return prepare(inputNode, 0, maxdepth);
    }

    public static PHFNode prepare(PHFNode inputNode, int depth, int maxdepth)
        throws IOException, InterruptedException
    {
        // stopping condition
        if (depth == maxdepth)
        {
            // simply copy the node and return
            return new PHFNode(inputNode.getLinkedFile());
        }
        else
        {
            IMembershipBuilder splitType = new VariableKDTreeMembershipBuilder();

            // split current node into a list of subnodes
            ArrayList<PHFNode> childNodes = NodeSplitter.split(inputNode, splitType);

            // pre process child nodes
            List<PHFNode> processedNodes = new ArrayList<>(childNodes.size());
            for (PHFNode childNode : childNodes) processedNodes.add(prepare(childNode, depth + 1, maxdepth));

            List<File> processedFiles = new ArrayList<>(processedNodes.size());
            for (PHFNode n : processedNodes) processedFiles.add(n.getLinkedFile());

            File stitchedModel = NaiveMeshStitcher.stitch(processedFiles);

            PLYHeader stitchedHeader = new PLYHeader(stitchedModel);
            long totalFaces = stitchedHeader.getElement("face").getCount();
            long targetFaces = totalFaces / splitType.getSplitRatio();

            File simplifiedFile = SimplifierWrapper.simplify(stitchedModel, targetFaces, false);

            PHFNode outputNode = new PHFNode(simplifiedFile);
            outputNode.addChildren(processedNodes);

            System.out.printf("Simplified from %d to %d faces.%n", totalFaces, outputNode.getNumFaces());
            return outputNode;
        }
    }

}
