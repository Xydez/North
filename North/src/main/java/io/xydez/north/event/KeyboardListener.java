package io.xydez.north.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public interface KeyboardListener extends Listener
{
	void onKeyboard(KeyboardEvent event);

	enum KeyboardAction
	{
		Press(GLFW_PRESS), Release(GLFW_RELEASE), Repeat(GLFW_PRESS);

		private int glfwEnum;

		KeyboardAction(int glfwEnum)
		{
			this.glfwEnum = glfwEnum;
		}
	}

	class KeyboardEvent extends Event<KeyboardListener>
	{
		private final int keycode;
		private final int scancode;
		private final KeyboardAction action;

		public KeyboardEvent(int keycode, int scancode, KeyboardAction action)
		{
			this.keycode = keycode;
			this.scancode = scancode;
			this.action = action;
		}

		@Override
		public void fire(@Nullable ArrayList<KeyboardListener> listeners)
		{
			if (listeners == null)
				return;

			for (KeyboardListener listener : listeners)
				listener.onKeyboard(this);
		}

		public int getKeycode()
		{
			return keycode;
		}

		public int getScancode()
		{
			return scancode;
		}

		public KeyboardAction getAction()
		{
			return action;
		}
	}
}
