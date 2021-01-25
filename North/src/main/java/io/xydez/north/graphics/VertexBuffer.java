package io.xydez.north.graphics;

import io.xydez.north.core.Disposable;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static io.xydez.north.core.Application.NULL;
import static org.lwjgl.opengl.GL15.*;

public class VertexBuffer implements Disposable
{
    private int handle;

    public VertexBuffer(float[] vertices)
    {
        this(vertices, BufferUsage.Static);
    }

    public VertexBuffer(float[] vertices, BufferUsage usage)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer vertexBuffer = stack.mallocFloat(vertices.length);
            vertexBuffer.put(vertices);
            vertexBuffer.flip();

            this.handle = glGenBuffers();
            this.bind();
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, usage.glEnum);
            this.unbind();
        }
    }

    public void bind()
    {
        glBindBuffer(GL_ARRAY_BUFFER, this.handle);
    }

    public void unbind()
    {
        glBindBuffer(GL_ARRAY_BUFFER, NULL);
    }

    @Override
    public void dispose()
    {
        glDeleteBuffers(this.handle);
        this.handle = NULL;
    }
}
