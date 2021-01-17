package io.xydez.north.graphics;

import io.xydez.north.Disposable;

import static io.xydez.north.Application.NULL;
import static org.lwjgl.opengl.GL30.*;

public class VertexArray implements Disposable
{
    private final int handle;

    public VertexArray(VertexBufferLayout vertexBufferLayout, VertexBuffer vertexBuffer)
    {
        this.handle = glGenVertexArrays();

        this.bind();
        vertexBuffer.bind();

        int i = 0;
        for (VertexBufferLayout.VertexBufferElement element : vertexBufferLayout.getElements())
        {
            glVertexAttribPointer(i, element.getCount(), element.getType().glEnum, false, element.getCount() * element.getType().size, NULL);
            glEnableVertexAttribArray(i);

            i += 1;
        }
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
    public void dispose()
    {
        glDeleteVertexArrays(this.handle);
    }
}
