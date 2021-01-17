package io.xydez.north.graphics;

import static org.lwjgl.opengl.GL15.*;

public enum BufferUsage
{
	Stream(GL_STREAM_DRAW), Static(GL_STATIC_DRAW), Dynamic(GL_DYNAMIC_DRAW);

	protected final int glEnum;

	BufferUsage(int glEnum)
	{
		this.glEnum = glEnum;
	}
}
