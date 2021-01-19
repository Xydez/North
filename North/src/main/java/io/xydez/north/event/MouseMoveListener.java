package io.xydez.north.event;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.ArrayList;

public interface MouseMoveListener extends Listener
{
	void onMouseMove(MouseMoveEvent event);

	class MouseMoveEvent extends Event<MouseMoveListener>
	{
		private final Vector2f oldPosition;
		private final Vector2f newPosition;

		public MouseMoveEvent(Vector2f oldPosition, Vector2f newPosition)
		{
			this.oldPosition = oldPosition;
			this.newPosition = newPosition;
		}

		public Vector2f getOldPosition()
		{
			return oldPosition;
		}

		public Vector2f getNewPosition()
		{
			return newPosition;
		}

		@Override
		public void fire(@Nullable ArrayList<MouseMoveListener> listeners)
		{
			if (listeners == null)
				return;

			for (MouseMoveListener listener : listeners)
				listener.onMouseMove(this);
		}
	}
}
