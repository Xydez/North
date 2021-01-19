package io.xydez.sandbox;

import io.xydez.north.Application;
import io.xydez.north.graphics.*;
import io.xydez.north.io.FileManager;
import org.joml.Vector2f;

public class SandboxApp extends Application
{
    private VertexBuffer vbo;
    private IndexBuffer ibo;
    private VertexArray vao;
    private ShaderProgram program;

    private double timer = 0.0;

    public SandboxApp()
    {
        super("LWJGL Test", 800, 600);
    }

    @Override
    protected void initialize()
    {
        getLogger().trace("Hello from App!");

        float[] vertices = new float[] {
            -0.5f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f
        };

        int[] indices = new int[] {
            0, 1, 2,
            0, 2, 3
        };

        this.vbo = new VertexBuffer(vertices);

        VertexBufferLayout layout = new VertexBufferLayout();
        layout.push(VertexBufferLayout.VertexBufferElement.ElementType.Float, 3);

        this.ibo = new IndexBuffer(indices);

        this.vao = new VertexArray(layout, this.vbo);

        try {
            String vertexSource = FileManager.readClassFile("shaders/vertex.glsl");
            Shader vertexShader = new Shader(vertexSource, Shader.Type.Vertex);

            String fragmentSource = FileManager.readClassFile("shaders/fragment.glsl");
            Shader fragmentShader = new Shader(fragmentSource, Shader.Type.Fragment);

            this.program = new ShaderProgram(vertexShader, fragmentShader);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void update(double delta)
    {
        this.timer += delta;
        this.program.setUniform("color", new Vector2f(((float)Math.sin(this.timer) + 1.0f) / 2.0f, ((float)Math.cos(this.timer) + 1.0f) / 2.0f));
    }

    @Override
    protected void render(Renderer renderer)
    {
        renderer.render(this.program, this.vao, this.ibo);
    }

    @Override
    protected void terminate()
    {
        this.program.dispose();
        this.vbo.dispose();
        this.vao.dispose();
    }
}
