package org.uct.cs.simplify.ply.utilities;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;

public class OctetFinder
{
    public enum Octet
    {
        XYZ, XYz, XyZ, Xyz, xYZ, xYz, xyZ, xyz
    }

    private final Point3D center;
    private final Octet[] by_i;

    public OctetFinder(BoundingBox bb)
    {
        this.center = new Point3D(
                (bb.getMaxX() + bb.getMinX())/2,
                (bb.getMaxY() + bb.getMinY())/2,
                (bb.getMaxZ() + bb.getMinZ())/2
        );
        this.by_i = Octet.values();
    }

    public OctetFinder(Point3D center)
    {
        this.center = center;
        this.by_i = Octet.values();
    }

    public Octet getOctet(double x, double y, double z)
    {
        int i = 0;
        if (x < this.center.getX()) i += 4;
        if (x < this.center.getY()) i += 2;
        if (x < this.center.getZ()) i += 1;
        return this.by_i[i];
    }
}
