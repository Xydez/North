package io.xydez.north.graphics;

import static org.lwjgl.opengl.GL30.*;

public class Renderer
{
    public Renderer() {}

    public void render(ShaderProgram shaderProgram, VertexArray vertexArray, int count)
    {
        shaderProgram.bind();
        vertexArray.bind();

        glDrawArrays(GL_TRIANGLES, 0, count);
    }
}
