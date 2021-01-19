package io.xydez.north.event;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public interface MouseButtonListener extends Listener
{
	void onMouseButton(MouseButtonEvent event);

	enum MouseAction
	{
		Press(GLFW_PRESS), Release(GLFW_RELEASE);

		private final int glfwEnum;

		MouseAction(int glfwEnum)
		{
			this.glfwEnum = glfwEnum;
		}
	}

	enum MouseButton
	{
		Left(GLFW_MOUSE_BUTTON_LEFT), Middle(GLFW_MOUSE_BUTTON_MIDDLE), Right(GLFW_MOUSE_BUTTON_RIGHT);

		private final int glfwEnum;

		MouseButton(int glfwEnum)
		{
			this.glfwEnum = glfwEnum;
		}
	}

	class MouseButtonEvent extends Event<MouseButtonListener>
	{
		private final MouseButton button;
		private final MouseAction action;

		public MouseButtonEvent(MouseButton button, MouseAction action)
		{
			this.button = button;
			this.action = action;
		}

		@Override
		public void fire(@Nullable ArrayList<MouseButtonListener> listeners)
		{
			if (listeners == null)
				return;

			for (MouseButtonListener listener : listeners)
				listener.onMouseButton(this);
		}

		public MouseButton getButton()
		{
			return button;
		}

		public MouseAction getAction()
		{
			return action;
		}
	}
}
