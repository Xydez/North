package io.xydez.north.graphics;

import io.xydez.north.core.Disposable;
import io.xydez.north.io.FileManager;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static io.xydez.north.core.Application.NULL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture implements Disposable
{
	private int handle;

	public Texture(String path) throws IOException
	{
		this.handle = glGenTextures();
		this.bind();

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);

			ByteBuffer data = FileManager.readClassFile(path);
			stbi_set_flip_vertically_on_load(true);
			ByteBuffer image = stbi_load_from_memory(data, width, height, channels, 4);
			//ByteBuffer data = stbi_load("example.png", width, height, channels, 4);

			if (image == null)
				throw new IOException(String.format("Failed to load image \"%s\". Reason: " + stbi_failure_reason(), path));

			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), NULL, GL_RGBA, GL_UNSIGNED_BYTE, image);

			stbi_image_free(image);
		}

		this.unbind();
	}

	public void bind()
	{
		this.bind(0);
	}

	public void bind(int slot)
	{
		glActiveTexture(GL_TEXTURE0 + slot);
		glBindTexture(GL_TEXTURE_2D, this.handle);
	}

	public void unbind()
	{
		this.unbind(0);
	}

	public void unbind(int slot)
	{
		glActiveTexture(GL_TEXTURE0 + slot);
		glBindTexture(GL_TEXTURE_2D, NULL);
	}

	@Override
	public void dispose()
	{
		glDeleteTextures(this.handle);
		this.handle = NULL;
	}
}
