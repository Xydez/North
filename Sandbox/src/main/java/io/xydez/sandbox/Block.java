package io.xydez.sandbox;

import io.xydez.north.graphics.Texture;
import org.joml.Vector3i;

public abstract class Block
{
	public Vector3i position;

	public Block(Vector3i position)
	{
		this.position = position;
	}

	public abstract Texture getTexture(Face face);
}
