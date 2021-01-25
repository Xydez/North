package io.xydez.north.graphics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.xydez.north.core.Application.NULL;
import static org.lwjgl.opengl.GL30.*;

public class Renderer
{
    public Renderer() {}

    public void render(@Nullable ShaderProgram shaderProgram, @NotNull VertexArray vertexArray, @NotNull IndexBuffer indexBuffer)
    {
        if (shaderProgram != null)
            shaderProgram.bind();

        vertexArray.bind();
        indexBuffer.bind();

        glDrawElements(GL_TRIANGLES, indexBuffer.getCount(), GL_UNSIGNED_INT, NULL);

        //indexBuffer.unbind();
        //vertexArray.unbind();
        //shaderProgram.unbind();
    }
}
