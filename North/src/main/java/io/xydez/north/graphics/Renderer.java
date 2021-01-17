package io.xydez.north.graphics;

import static io.xydez.north.Application.NULL;
import static org.lwjgl.opengl.GL30.*;

public class Renderer
{
    public Renderer() {}

    public void render(ShaderProgram shaderProgram, VertexArray vertexArray, IndexBuffer indexBuffer)
    {
        shaderProgram.bind();
        vertexArray.bind();
        indexBuffer.bind();

        glDrawElements(GL_TRIANGLES, indexBuffer.getCount(), GL_UNSIGNED_INT, NULL);

        //indexBuffer.unbind();
        //vertexArray.unbind();
        //shaderProgram.unbind();
    }
}
