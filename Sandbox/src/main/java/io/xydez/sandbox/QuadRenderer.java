package io.xydez.sandbox;

import io.xydez.north.graphics.*;
import io.xydez.north.io.FileManager;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;

public class QuadRenderer implements AutoCloseable
{
	private VertexArray vao;
	private VertexBuffer vbo;
	private IndexBuffer ibo;
	private ShaderProgram program;

	public QuadRenderer()
	{
		float[] vertices = new float[] {
			-0.5f,  0.5f,  0.0f,   0.0f, 1.0f,
		 	 0.5f,  0.5f,  0.0f,   1.0f, 1.0f,
		 	 0.5f, -0.5f,  0.0f,   1.0f, 0.0f,
			-0.5f, -0.5f,  0.0f,   0.0f, 0.0f
		};

		int[] indices = new int[] {
			0, 1, 2,
			0, 2, 3
		};

		VertexBufferLayout layout = new VertexBufferLayout();
		layout.push(VertexBufferLayout.VertexBufferElement.ElementType.Float, 3);
		layout.push(VertexBufferLayout.VertexBufferElement.ElementType.Float, 2);

		this.vbo = new VertexBuffer(vertices);

		this.ibo = new IndexBuffer(indices);

		this.vao = new VertexArray(layout, this.vbo, this.ibo);

		try {
			String vertexSource = FileManager.readClassFileToString("shaders/vertex.glsl");
			String fragmentSource = FileManager.readClassFileToString("shaders/fragment.glsl");

			Shader vertexShader = new Shader(vertexSource, Shader.Type.Vertex);
			Shader fragmentShader = new Shader(fragmentSource, Shader.Type.Fragment);

			this.program = new ShaderProgram(vertexShader, fragmentShader);

			vertexShader.close();
			fragmentShader.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void render(Renderer renderer, Camera camera, Vector3f position, Quaternionf orientation, Texture texture)
	{
		Matrix4f model = new Matrix4f().translate(position).rotate(orientation);
		Matrix4f mvp = camera.getProjectionMatrix().mul(camera.getViewMatrix(), new Matrix4f()).mul(model);

		this.program.bind();
		this.program.setUniform("mvp", mvp);
		this.program.setUniform("testTexture", 0);

		texture.bind(0);

		renderer.render(null, this.vao, this.ibo);
	}

	@Override
	public void close()
	{
		this.program.close();
		this.program = null;

		this.vao.close();
		this.vao = null;

		this.vbo.close();
		this.vbo = null;

		this.ibo.close();
		this.ibo = null;
	}
}
