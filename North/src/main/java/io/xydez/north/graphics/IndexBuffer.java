package io.xydez.north.graphics;

import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static io.xydez.north.core.Application.NULL;
import static org.lwjgl.opengl.GL15.*;

public class IndexBuffer implements AutoCloseable
{
	private int handle;

	private final int count;

	public IndexBuffer(int[] indices)
	{
		this(indices, BufferUsage.Static);
	}

	public IndexBuffer(int[] indices, BufferUsage usage)
	{
		this.count = indices.length;

		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer indexBuffer = stack.mallocInt(indices.length);
			indexBuffer.put(indices);
			indexBuffer.flip();

			this.handle = glGenBuffers();
			this.bind();
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, usage.glEnum);
			this.unbind();
		}
	}

	public void bind()
	{
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.handle);
	}

	public void unbind()
	{
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, NULL);
	}

	public int getCount()
	{
		return count;
	}

	@Override
	public void close()
	{
		glDeleteBuffers(this.handle);
		this.handle = NULL;
	}
}
