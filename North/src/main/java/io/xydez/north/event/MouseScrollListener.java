package io.xydez.north.event;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.ArrayList;

public interface MouseScrollListener extends Listener
{
	void onMouseScroll(MouseScrollEvent event);

	class MouseScrollEvent extends Event<MouseScrollListener>
	{
		private final Vector2f offset;

		public MouseScrollEvent(Vector2f offset)
		{
			this.offset = offset;
		}

		public Vector2f getOffset()
		{
			return offset;
		}

		@Override
		public void fire(@Nullable ArrayList<MouseScrollListener> listeners)
		{
			if (listeners == null)
				return;

			for (MouseScrollListener listener : listeners)
				listener.onMouseScroll(this);
		}
	}
}
