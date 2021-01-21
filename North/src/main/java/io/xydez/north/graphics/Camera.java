package io.xydez.north.graphics;

import org.joml.*;

public abstract class Camera
{
	protected static final Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);

	protected final Vector3f position;
	protected final Vector3f orientation = new Vector3f(0.0f, 0.2f, 0.0f);

	public Camera(Vector3f position)
	{
		this.position = position;
	}

	public Matrix4f getViewMatrix()
	{
		//Quaternionf quaternion = new Quaternionf().rotateXYZ(this.pitch, this.yaw, this.roll);

		//Quaternionf rotation = new Quaternionf().rotateXYZ(orientation.x, orientation.y, orientation.z);
		//Quaternionf rotation = new Quaternionf().rotateYXZ(orientation.y, orientation.x, orientation.z);

		Vector3f viewDirection = getViewDirection();
		Vector3f pos = new Vector3f(this.position.x, this.position.y, this.position.z);

		//return new Matrix4f().rotate(rotation).translate(-this.position.x, -this.position.y, -this.position.z);
		return new Matrix4f().lookAt(pos, pos.add(viewDirection, new Vector3f()), UP);
	}

	public void rotate(float pitch, float yaw)
	{
		rotate(pitch, yaw, 0.0f);
	}

	public void rotate(float pitch, float yaw, float roll)
	{
		this.orientation.add(new Vector3f(pitch, yaw, roll));
	}

	public void move(Vector3f vector)
	{
		/*
		Quaternionf rotation = new Quaternionf().rotateXYZ(orientation.x, orientation.y, orientation.z);
		Vector3f viewDirection = new Vector3f(rotation.x, 0.0f, rotation.z);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f left = viewDirection.cross(up, new Vector3f());

		// Add X-axis
		this.position.add(left.mul(vector.x, new Vector3f()));

		// Add Y-axis
		this.position.add(up.mul(vector.y, new Vector3f()));

		// Add Z-axis
		this.position.add((viewDirection.mul(vector.z, new Vector3f())));
		*/

		Vector3f viewDirection = getViewDirection();
		Vector3f left = viewDirection.cross(UP, new Vector3f());

		// Add X-axis
		this.position.add(left.mul(-vector.x, new Vector3f()));

		// Add Y-axis
		this.position.add(UP.mul(vector.y, new Vector3f()));

		// Add Z-axis
		this.position.add((viewDirection.mul(-vector.z, new Vector3f())));
	}

	private Vector3f getViewDirection()
	{
		Vector3f viewDirection = new Vector3f(0.0f, 0.0f, -1.0f);
		viewDirection.mul(new Matrix3f().rotate(-this.orientation.y, UP));

		Vector3f left = viewDirection.cross(UP, new Vector3f()); // normalize()
		viewDirection.mul(new Matrix3f().rotate(-this.orientation.x, left));

		viewDirection.mul(new Matrix3f().rotate(-this.orientation.z, viewDirection));

		viewDirection.normalize();

		return viewDirection;
	}

	public abstract Matrix4f getProjectionMatrix();

	public static class OrthographicCamera extends Camera
	{
		private float aspectRatio;

		public OrthographicCamera(Vector3f position, float aspectRatio)
		{
			super(position);
			this.aspectRatio = aspectRatio;
		}

		public void setAspectRatio(float aspectRatio)
		{
			this.aspectRatio = aspectRatio;
		}

		@Override
		public Matrix4f getProjectionMatrix()
		{
			// -this.aspectRatio, this.aspectRatio
			return new Matrix4f().ortho(-this.aspectRatio, this.aspectRatio, -1, 1, 0.1f, 100.0f);
		}
	}

	public static class PerspectiveCamera extends Camera
	{
		private float aspectRatio;
		private float fieldOfView;

		public PerspectiveCamera(Vector3f position, float aspectRatio, float fieldOfView)
		{
			super(position);

			this.aspectRatio = aspectRatio;
			this.fieldOfView = fieldOfView;
		}

		public void setAspectRatio(float aspectRatio)
		{
			this.aspectRatio = aspectRatio;
		}

		public void setFieldOfView(float fieldOfView)
		{
			this.fieldOfView = fieldOfView;
		}

		@Override
		public Matrix4f getProjectionMatrix()
		{
			return new Matrix4f().perspective(this.fieldOfView, this.aspectRatio, 0.1f, 100.0f);
		}
	}
}
