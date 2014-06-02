package org.uct.cs.simplify.ply.header;

public class PLYPropertyBase
{
    private static int[] byteSize = { 1, 1, 2, 2, 4, 4, 4, 8 };
    private String name;

    public PLYPropertyBase(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public String toString()
    {
        return String.format("PLYPropertyBase(%s)", this.name);
    }

    public static PLYPropertyBase FromString(String line)
    {
        line = line.trim();
        String[] parts = line.split(" ");
        if (parts[ 1 ].equals("list"))
            return new PLYListProperty(parts[ 4 ], parseDataType(parts[ 3 ]), parseDataType(parts[ 2 ]));
        return new PLYProperty(parts[ 2 ], parseDataType(parts[ 1 ]));
    }

    public static DataType parseDataType(String input)
    {
        return DataType.valueOf(input.trim().toUpperCase());
    }

    public static int bytesInType(DataType dt)
    {
        return byteSize[ dt.ordinal() ];
    }
    public enum DataType
    {
        CHAR,
        UCHAR,
        SHORT,
        USHORT,
        INT,
        UINT,
        FLOAT,
        DOUBLE
    }

}
