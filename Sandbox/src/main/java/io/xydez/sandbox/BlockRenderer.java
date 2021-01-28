package io.xydez.sandbox;

import io.xydez.north.graphics.Camera;
import io.xydez.north.graphics.Renderer;
import io.xydez.north.graphics.Texture;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BlockRenderer implements AutoCloseable
{
	private static final Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);
	private static final Vector3f RIGHT = new Vector3f(1.0f, 0.0f, 0.0f);

	private QuadRenderer quadRenderer;

	public BlockRenderer()
	{
		this.quadRenderer = new QuadRenderer();
	}

	public void render(Renderer renderer, Camera camera, Block block, @NotNull Face... faces)
	{
		for (Face face : faces)
		{
			this.renderFace(renderer, camera, new Vector3f(block.position), face, block.getTexture(face));
		}
	}

	public void renderFace(Renderer renderer, Camera camera, Vector3f position, Face face, Texture texture)
	{
		Quaternionf orientation = new Quaternionf();

		Vector3f facePos = null;
		switch (face)
		{
			case Top -> {
				facePos = new Vector3f(0.0f, 0.5f, 0.0f);
				orientation.rotateAxis((float)(-(Math.PI) / 2.0), RIGHT);
			}
			case Bottom -> {
				facePos = new Vector3f(0.0f, -0.5f, 0.0f);
				orientation.rotateAxis((float)((Math.PI) / 2.0), RIGHT);
			}
			case Front -> {
				facePos = new Vector3f(0.0f, 0.0f, -0.5f);
				orientation.rotateAxis((float)(-(Math.PI * 2) / 2.0), UP);
			}
			case Back -> {
				facePos = new Vector3f(0.0f, 0.0f, 0.5f);
			}
			case Right -> {
				facePos = new Vector3f(0.5f, 0.0f, 0.0f);
				orientation.rotateAxis((float)(-(Math.PI * 3) / 2.0), UP);
			}
			case Left -> {
				facePos = new Vector3f(-0.5f, 0.0f, 0.0f);
				orientation.rotateAxis((float)(-(Math.PI) / 2.0), UP);
			}
		}

		this.quadRenderer.render(renderer, camera, facePos.add(position, new Vector3f()), orientation, texture);
	}

	@Override
	public void close()
	{
		this.quadRenderer.close();
		this.quadRenderer = null;
	}
}
