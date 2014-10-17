package org.uct.cs.simplify.ply.datatypes;

import org.uct.cs.simplify.Constants;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ucharDataTypeReader implements IDataTypeReader
{
    @Override
    public double read(ByteBuffer b)
    {
        return (short) b.get() & Constants.BYTE_MASK;
    }

    @Override
    public double read(BufferedInputStream stream) throws IOException
    {
        return (short) stream.read() & Constants.BYTE_MASK;
    }

    @Override
    public int bytesAtATime()
    {
        return 1;
    }
}
