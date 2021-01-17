package io.xydez.north.graphics;

import io.xydez.north.Disposable;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static io.xydez.north.Application.NULL;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram implements Disposable
{
    private int handle;

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

    @Override
    public void dispose()
    {
        glDeleteProgram(this.handle);
        this.handle = NULL;
    }
}
