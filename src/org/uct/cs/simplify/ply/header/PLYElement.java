package org.uct.cs.simplify.ply.header;

import java.util.ArrayList;
import java.util.List;

/**
 * PLYElement
 * <p>
 * An element in a PLYHeader indicates an object name and a count. eg: "element vertex 30000"
 */
public class PLYElement
{
    private final String name;
    private final long count;
    private final List<PLYPropertyBase> properties;
    private Integer itemSize;

    public PLYElement(String name, long count)
    {
        this.name = name.toLowerCase();
        this.count = count;
        this.itemSize = 0;
        //noinspection CollectionWithoutInitialCapacity
        this.properties = new ArrayList<>();
    }

    public static PLYElement FromString(String s)
    {
        s = s.trim();
        String[] parts = s.split(" ");
        return new PLYElement(parts[ 1 ], Long.parseLong(parts[ 2 ]));
    }

    public void addProperty(PLYPropertyBase p)
    {
        this.properties.add(p);
        if (this.itemSize != null)
        {
            if (p instanceof PLYListProperty)
            {
                this.itemSize = null;
            }
            else if (p instanceof PLYProperty)
            {
                this.itemSize += ((PLYProperty) p).getTypeReader().bytesAtATime();
            }
        }
    }

    public String getName()
    {
        return this.name;
    }

    public long getCount()
    {
        return this.count;
    }

    public Integer getItemSize()
    {
        return this.itemSize;
    }

    public List<PLYPropertyBase> getProperties()
    {
        return this.properties;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(100);
        sb.append(String.format("element %s %d %n", this.name, this.count));
        for (PLYPropertyBase p : this.properties)
        {
            sb.append(p);
            sb.append('\n');
        }
        return sb.toString();
    }

}

