package io.xydez.north.event;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.ArrayList;

public interface WindowResizeListener extends Listener
{
	void onWindowResize(WindowResizeEvent event);

	class WindowResizeEvent extends Event<WindowResizeListener>
	{
		private final Vector2f oldSize;
		private final Vector2f newSize;

		public WindowResizeEvent(Vector2f oldSize, Vector2f newSize)
		{
			this.oldSize = oldSize;
			this.newSize = newSize;
		}

		public Vector2f getOldSize()
		{
			return oldSize;
		}

		public Vector2f getNewSize()
		{
			return newSize;
		}

		@Override
		public void fire(@Nullable ArrayList<WindowResizeListener> listeners)
		{
			if (listeners == null)
				return;

			for (WindowResizeListener listener : listeners)
				listener.onWindowResize(this);
		}
	}
}
