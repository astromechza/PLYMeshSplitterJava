package org.uct.cs.simplify.filebuilder;

import org.uct.cs.simplify.util.Outputter;
import org.uct.cs.simplify.util.TempFileManager;
import org.uct.cs.simplify.util.Useful;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

public class PHFBuilder
{
    public static String compile(PHFNode tree, File outputFile, Map<String, String> additionJSONKeys) throws IOException
    {
        Outputter.info3f("Compressing Hierarchical tree into %s%n", outputFile);
        File tempBlockFile = TempFileManager.provide(Useful.getFilenameWithoutExt(outputFile.getName()));

        List<PHFNode> nodes = tree.collectAllNodes();
        Outputter.info2f("Writing %d nodes to %s%n", nodes.size(), tempBlockFile.getPath());

        int max_depth = 0;
        try (BufferedOutputStream ostream = new BufferedOutputStream(new FileOutputStream(tempBlockFile)))
        {
            long position = 0;
            for (PHFNode node : nodes)
            {
                Outputter.info1f("Writing %s to %s%n", node.getLinkedFile().getPath(), tempBlockFile.getPath());

                PLYDataCompressor.CompressionResult r = PLYDataCompressor.compress(node.getLinkedFile(), ostream);

                long length = r.getLengthOfFaces() + r.getLengthOfVertices();
                node.setBlockOffset(position);
                node.setBlockLength(length);
                position += length;
                max_depth = Math.max(max_depth, node.getDepth());
            }
        }

        additionJSONKeys.put("vertex_colour", "true");
        additionJSONKeys.put("nodes", PHFNode.buildJSONHierarchy(tree));
        additionJSONKeys.put("max_depth", "" + max_depth);

        StringBuilder sb = new StringBuilder(1000);
        sb.append('{');
        for (Map.Entry<String, String> entry : additionJSONKeys.entrySet())
        {
            sb.append(String.format("\"%s\":%s,", entry.getKey(), entry.getValue()));
        }
        sb.append('}');
        String jsonheader = sb.toString();

        Outputter.info1f("%nWriting '%s' ..%n", outputFile.getPath());
        try (FileOutputStream fostream = new FileOutputStream(outputFile))
        {
            int l = jsonheader.length();
            Outputter.debugf("Header length: " + l);
            Useful.writeIntLE(fostream, l);
            fostream.write(jsonheader.getBytes());

            Outputter.debugf("Writing header (%s)%n", Useful.formatBytes(l));

            try (
                FileChannel fcOUT = fostream.getChannel();
                FileChannel fcIN = new FileInputStream(tempBlockFile).getChannel()
            )
            {
                Outputter.debugf("Writing data (%s)%n", Useful.formatBytes(fcIN.size()));
                fcOUT.transferFrom(fcIN, fcOUT.position(), fcIN.size());
            }
        }

        TempFileManager.release(tempBlockFile);

        return jsonheader;
    }
}
