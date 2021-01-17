package io.xydez.north.graphics;

import io.xydez.north.Disposable;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static io.xydez.north.Application.NULL;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public class Shader implements Disposable
{
    // TODO: Support more types
    public enum Type
    {
        Vertex(GL_VERTEX_SHADER), Fragment(GL_FRAGMENT_SHADER), Geometry(GL_GEOMETRY_SHADER);

        protected final int glEnum;

        Type(int glEnum)
        {
            this.glEnum = glEnum;
        }
    }

    private int handle;
    private final Type type;

    public Shader(String source, Type type)
    {
        this.type = type;
        this.handle = glCreateShader(type.glEnum);
        if (this.handle == NULL)
            throw new RuntimeException("Failed to create shader!");

        glShaderSource(this.handle, source);
        glCompileShader(this.handle);

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer buffer = stack.mallocInt(1);
            glGetShaderiv(this.handle, GL_COMPILE_STATUS, buffer);

            if (buffer.get(0) == GL_FALSE)
            {
                String infoLog = glGetShaderInfoLog(this.handle);
                throw new RuntimeException("Failed to compile shader:\n" + infoLog + "\nSource:\n\"" + source + "\"");
            }
        }
    }

    public Type getType()
    {
        return type;
    }

    protected int getHandle()
    {
        return handle;
    }

    @Override
    public void dispose()
    {
        glDeleteShader(this.handle);
        this.handle = NULL;
    }
}
