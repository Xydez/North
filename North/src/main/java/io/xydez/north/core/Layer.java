package io.xydez.north.core;

import io.xydez.north.graphics.Renderer;

public abstract class Layer
{
	protected boolean enabled;
	protected int index;

	public Layer(int index)
	{
		this.index = index;
	}

	public void initialize() {}
	public abstract void update(double delta);
	public abstract void render(Renderer renderer);
	public void terminate() {}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}
}
