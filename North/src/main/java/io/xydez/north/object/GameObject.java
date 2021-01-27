package io.xydez.north.object;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GameObject
{
	public Vector3f position;
	public Quaternionf orientation;

	public GameObject(Vector3f position, Quaternionf orientation)
	{
		this.position = position;
		this.orientation = orientation;
	}
}
