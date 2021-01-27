package io.xydez.north.graphics;

import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import static io.xydez.north.core.Application.NULL;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram implements AutoCloseable
{
    private int handle;
    private final HashMap<String, Integer> uniformLocationCache = new HashMap<>();

    public ShaderProgram(@NotNull Shader... shaders)
    {
        this.handle = glCreateProgram();
        for (Shader shader : shaders)
            glAttachShader(this.handle, shader.getHandle());

        glLinkProgram(this.handle);

        try (MemoryStack memoryStack = MemoryStack.stackPush())
        {
            IntBuffer buffer = memoryStack.mallocInt(1);
            glGetProgramiv(this.handle, GL_LINK_STATUS, buffer);

            if (buffer.get(0) == GL_FALSE)
                throw new RuntimeException(glGetProgramInfoLog(this.handle));
        }
    }

    public void bind()
    {
        glUseProgram(this.handle);
    }

    public void unbind()
    {
        glUseProgram(NULL);
    }

    public void setUniform(String name, int i)
    {
        glUniform1i(getUniformLocation(name), i);
    }

    public void setUniform(String name, float f)
    {
        glUniform1f(getUniformLocation(name), f);
    }

    public void setUniform(String name, Vector2f vector)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer buffer = vector.get(stack.mallocFloat(2));
            glUniform2fv(getUniformLocation(name), buffer);
        }
    }

    public void setUniform(String name, Vector3f vector)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer buffer = vector.get(stack.mallocFloat(3));
            glUniform3fv(getUniformLocation(name), buffer);
        }
    }

    public void setUniform(String name, Vector4f vector)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer buffer = vector.get(stack.mallocFloat(4));
            glUniform4fv(getUniformLocation(name), buffer);
        }
    }

    public void setUniform(String name, Matrix2f matrix)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer buffer = matrix.get(stack.mallocFloat(4));
            glUniformMatrix2fv(getUniformLocation(name), false, buffer);
        }
    }

    public void setUniform(String name, Matrix3f matrix)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer buffer = matrix.get(stack.mallocFloat(9));
            glUniformMatrix3fv(getUniformLocation(name), false, buffer);
        }
    }

    public void setUniform(String name, Matrix4f matrix)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer buffer = matrix.get(stack.mallocFloat(16));
            glUniformMatrix4fv(getUniformLocation(name), false, buffer);
        }
    }

    private int getUniformLocation(String name)
    {
        if (this.uniformLocationCache.containsKey(name))
            return this.uniformLocationCache.get(name);

        int location = glGetUniformLocation(this.handle, name);
        this.uniformLocationCache.put(name, location);

        if (location == -1)
            throw new RuntimeException("Location of uniform \"" + name + "\" could not be found");

        return location;
    }

    @Override
    public void close()
    {
        glDeleteProgram(this.handle);
        this.handle = NULL;
    }
}
