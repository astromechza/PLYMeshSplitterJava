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
    public static PackagedHierarchicalNode prepare(PackagedHierarchicalNode inputNode, int maxdepth)
    throws IOException, InterruptedException
    {
        return prepare(inputNode, 0, maxdepth, false);
    }

    public static PackagedHierarchicalNode prepare(PackagedHierarchicalNode inputNode, int maxdepth, boolean tightBoundingBoxes)
        throws IOException, InterruptedException
    {
        return prepare(inputNode, 0, maxdepth, tightBoundingBoxes);
    }

    public static PackagedHierarchicalNode prepare(PackagedHierarchicalNode inputNode, int depth, int maxdepth)
    throws IOException, InterruptedException
    {
        return prepare(inputNode, depth, maxdepth, false);
    }

    public static PackagedHierarchicalNode prepare(PackagedHierarchicalNode inputNode, int depth, int maxdepth, boolean tightBoundingBoxes)
        throws IOException, InterruptedException
    {
        // stopping condition
        if (depth == maxdepth)
        {
            // simply copy the node and return
            File leafFile = inputNode.getLinkedFile();
            return (tightBoundingBoxes)
                ? new PackagedHierarchicalNode(leafFile)
                : new PackagedHierarchicalNode(leafFile, inputNode.getBoundingBox());

        }
        else
        {
            IMembershipBuilder splitType = new VariableKDTreeMembershipBuilder();

            // split current node into a list of subnodes
            ArrayList<PackagedHierarchicalNode> childNodes = NodeSplitter.split(inputNode, splitType);

            // pre process child nodes
            List<PackagedHierarchicalNode> processedNodes = new ArrayList<>(childNodes.size());
            for (PackagedHierarchicalNode childNode : childNodes)
            {
                processedNodes.add(prepare(childNode, depth + 1, maxdepth));
            }

            List<File> processedFiles = new ArrayList<>(processedNodes.size());
            for (PackagedHierarchicalNode n : processedNodes)
            {
                processedFiles.add(n.getLinkedFile());
            }

            File stitchedModel = NaiveMeshStitcher.stitch(processedFiles);

            PLYHeader stitchedHeader = new PLYHeader(stitchedModel);
            long totalFaces = stitchedHeader.getElement("face").getCount();
            long targetFaces = totalFaces / splitType.getSplitRatio();

            File simplifiedFile = SimplifierWrapper.simplify(stitchedModel, targetFaces, false);

            PackagedHierarchicalNode outputNode = (tightBoundingBoxes)
                ? new PackagedHierarchicalNode(simplifiedFile)
                : new PackagedHierarchicalNode(simplifiedFile, inputNode.getBoundingBox());
            outputNode.addChildren(processedNodes);

            System.out.printf("Simplified from %d to %d faces.%n", totalFaces, outputNode.getNumFaces());

            return outputNode;
        }
    }

}
