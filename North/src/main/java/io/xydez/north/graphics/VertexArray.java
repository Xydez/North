package io.xydez.north.graphics;

import static io.xydez.north.core.Application.NULL;
import static org.lwjgl.opengl.GL30.*;

public class VertexArray implements AutoCloseable
{
    private int handle;

    public VertexArray(VertexBufferLayout vertexBufferLayout, VertexBuffer vertexBuffer, IndexBuffer indexBuffer)
    {
        this.handle = glGenVertexArrays();

        this.bind();
        vertexBuffer.bind();
        indexBuffer.bind();

        int stride = 0;
        for (VertexBufferLayout.VertexBufferElement element : vertexBufferLayout.getElements())
        {
            stride += element.getType().size * element.getCount();
        }

        int i = 0;
        int offset = 0;
        for (VertexBufferLayout.VertexBufferElement element : vertexBufferLayout.getElements())
        {
            glVertexAttribPointer(i, element.getCount(), element.getType().glEnum, false, stride, offset);
            glEnableVertexAttribArray(i);

            offset += element.getCount() * element.getType().size;
            i += 1;
        }

        this.unbind();
        vertexBuffer.unbind();
        indexBuffer.unbind();
    }

    public void bind()
    {
        glBindVertexArray(this.handle);
    }

    public void unbind()
    {
        glBindVertexArray(NULL);
    }

    @Override
    public void close()
    {
        glDeleteVertexArrays(this.handle);
        this.handle = NULL;
    }
}
