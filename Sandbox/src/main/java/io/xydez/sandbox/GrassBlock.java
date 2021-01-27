package io.xydez.sandbox;

import io.xydez.north.graphics.Texture;
import org.joml.Vector3i;

import java.io.IOException;

public class GrassBlock extends Block
{
	private static Texture topTexture;
	private static Texture sideTexture;
	private static Texture bottomTexture;

	public static void initialize()
	{
		try {
			topTexture = new Texture("textures/grass_top.png");
			sideTexture = new Texture("textures/grass_side.png");
			bottomTexture = new Texture("textures/dirt.png");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void terminate()
	{
		topTexture.close();
		sideTexture.close();
		bottomTexture.close();
	}

	public GrassBlock(Vector3i position)
	{
		super(position);
	}

	@Override
	public Texture getTexture(Face face)
	{
		return switch (face)
			{
				case Top -> topTexture;
				case Front, Right, Back, Left -> sideTexture;
				case Bottom -> bottomTexture;
			};
	}
}
