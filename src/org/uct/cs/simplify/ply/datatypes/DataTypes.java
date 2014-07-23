package org.uct.cs.simplify.ply.datatypes;

public class DataTypes
{
    private static final int[] byteSize = { 1, 1, 2, 2, 4, 4, 4, 8 };

    public static DataType parseDataType(String input)
    {
        return DataType.valueOf(input.trim().toUpperCase());
    }

    public static int getBytesInType(DataType dt)
    {
        return byteSize[ dt.ordinal() ];
    }

    public enum DataType
    {
        CHAR("char"),
        UCHAR("uchar"),
        SHORT("short"),
        USHORT("ushort"),
        INT("int"),
        UINT("uint"),
        FLOAT("float"),
        DOUBLE("double");

        private final String name;

        DataType(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }
    }
}
