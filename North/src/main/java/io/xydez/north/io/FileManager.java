package io.xydez.north.io;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileManager
{
    @NotNull
    public static String readClassFile(@NotNull String path) throws IOException
    {
        InputStream in = FileManager.class.getClassLoader().getResourceAsStream(path);

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
}
