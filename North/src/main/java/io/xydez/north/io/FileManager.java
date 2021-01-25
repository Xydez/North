package io.xydez.north.io;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class FileManager
{
    @NotNull
    public static String readClassFileToString(@NotNull String path) throws IOException
    {
        InputStream in = FileManager.class.getClassLoader().getResourceAsStream(path);

        if (in == null)
            throw new IOException(String.format("File \"%s\" not found!", path));

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in)))
        {
            int c = 0;
            while ((c = reader.read()) != -1)
            {
                builder.append((char)c);
            }
        }

        return builder.toString();
    }

    @NotNull
    public static ByteBuffer readClassFile(@NotNull String path) throws IOException
    {
        InputStream in = FileManager.class.getClassLoader().getResourceAsStream(path);

        if (in == null)
            throw new IOException(String.format("File \"%s\" not found!", path));

        byte[] bytes = in.readAllBytes();

        //ByteBuffer buffer = ByteBuffer.wrap(in.readAllBytes());
        //buffer.flip();

        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        buffer.put(bytes);
        buffer.flip();

        return buffer;
    }
}
