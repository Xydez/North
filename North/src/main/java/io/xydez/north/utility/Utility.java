package io.xydez.north.utility;

import io.xydez.north.core.Application;
import io.xydez.north.io.Logger;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import static io.xydez.north.core.Application.NULL;
import static org.lwjgl.opengl.AMDDebugOutput.*;
import static org.lwjgl.opengl.ARBDebugOutput.*;
import static org.lwjgl.opengl.ARBDebugOutput.glDebugMessageCallbackARB;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glGetInteger;
import static org.lwjgl.opengl.GL30C.GL_CONTEXT_FLAGS;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL43C.GL_CONTEXT_FLAG_DEBUG_BIT;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_OUTPUT;

public class Utility
{
	// Remove constructor
	private Utility() {}

	public enum Align
	{
		Left, Middle, Right
	}

	public static String padString(String string, int length, Align align)
	{
		return padString(string, length, align, " ");
	}

	public static String padString(String string, int length, Align align, String padChar)
	{
		int amountToPad = length - string.length();

		if (amountToPad > 0)
		{
			if (align == Align.Left)
			{
				return string + padChar.repeat(amountToPad);
			}
			else if (align == Align.Middle)
			{
				return padChar.repeat((int) Math.floor(amountToPad / 2.0)) + string + padChar.repeat((int) Math.ceil(amountToPad / 2.0));
			}
			else if (align == Align.Right)
			{
				return padChar.repeat(amountToPad) + string;
			}
		}

		return string;
	}

	public static void initGLLogging()
	{
		Logger logger = Application.getLogger();

		GLCapabilities capabilities = GL.getCapabilities();

		// If the graphics card supports OpenGL 4.3, use that for logging
		if (capabilities.OpenGL43 || capabilities.GL_KHR_debug)
		{
			if (capabilities.OpenGL30 && (glGetInteger(GL_CONTEXT_FLAGS) & GL_CONTEXT_FLAG_DEBUG_BIT) == 0)
			{
				logger.warn("A non-debug context may not produce any debug output. Enabling GL_DEBUG...");
				glEnable(GL_DEBUG_OUTPUT);
			}

			GLDebugMessageCallback callback = GLDebugMessageCallback.create((int source, int type, int id, int severity, int length, long messagePointer, long userParam) ->
			{
				String messageSeverity = "NULL";
				switch (severity)
				{
					case GL_DEBUG_SEVERITY_HIGH -> messageSeverity = "High";
					case GL_DEBUG_SEVERITY_MEDIUM -> messageSeverity = "Medium";
					case GL_DEBUG_SEVERITY_LOW -> messageSeverity = "Low";
					case GL_DEBUG_SEVERITY_NOTIFICATION -> messageSeverity = "Notification";
				}

				String messageSource = "NULL";
				switch (source)
				{
					case GL_DEBUG_SOURCE_API -> messageSource = "API";
					case GL_DEBUG_SOURCE_APPLICATION -> messageSource = "Application";
					case GL_DEBUG_SOURCE_OTHER -> messageSource = "Other";
					case GL_DEBUG_SOURCE_SHADER_COMPILER -> messageSource = "Shader";
					case GL_DEBUG_SOURCE_THIRD_PARTY -> messageSource = "Third party";
					case GL_DEBUG_SOURCE_WINDOW_SYSTEM -> messageSource = "Window System";
				}

				Logger.Level messageLevel = Logger.Level.Trace;
				switch (type)
				{
					case GL_DEBUG_TYPE_ERROR -> messageLevel = Logger.Level.Error;
					case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR, GL_DEBUG_TYPE_PERFORMANCE, GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> messageLevel = Logger.Level.Warning;
					case GL_DEBUG_TYPE_OTHER, GL_DEBUG_TYPE_PORTABILITY -> messageLevel = Logger.Level.Info;
				}

				String message = GLDebugMessageCallback.getMessage(length, messagePointer);

				logger.log(messageLevel, "[OpenGL 0x%s] [%s] (%s) %s", Integer.toHexString(id), messageSource, messageSeverity, message);
			});

			if (capabilities.OpenGL43)
				glDebugMessageCallback(callback, NULL);
			else
				KHRDebug.glDebugMessageCallback(callback, NULL);
		}
		else if (capabilities.GL_ARB_debug_output)
		{
			GLDebugMessageARBCallback callback = GLDebugMessageARBCallback.create((int source, int type, int id, int severity, int length, long messagePointer, long userParam) ->
			{
				String messageSeverity = "NULL";
				switch (severity)
				{
					case GL_DEBUG_SEVERITY_HIGH_ARB -> messageSeverity = "High";
					case GL_DEBUG_SEVERITY_MEDIUM_ARB -> messageSeverity = "Medium";
					case GL_DEBUG_SEVERITY_LOW_ARB -> messageSeverity = "Low";
				}

				String messageSource = "NULL";
				switch (source)
				{
					case GL_DEBUG_SOURCE_API_ARB -> messageSource = "API";
					case GL_DEBUG_SOURCE_APPLICATION_ARB -> messageSource = "Application";
					case GL_DEBUG_SOURCE_OTHER_ARB -> messageSource = "Other";
					case GL_DEBUG_SOURCE_SHADER_COMPILER_ARB -> messageSource = "Shader";
					case GL_DEBUG_SOURCE_THIRD_PARTY_ARB -> messageSource = "Third party";
					case GL_DEBUG_SOURCE_WINDOW_SYSTEM_ARB -> messageSource = "Window System";
				}

				Logger.Level messageLevel = Logger.Level.Trace;
				switch (type)
				{
					case GL_DEBUG_TYPE_ERROR_ARB -> messageLevel = Logger.Level.Error;
					case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR_ARB, GL_DEBUG_TYPE_PERFORMANCE_ARB, GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR_ARB -> messageLevel = Logger.Level.Warning;
					case GL_DEBUG_TYPE_OTHER_ARB, GL_DEBUG_TYPE_PORTABILITY_ARB -> messageLevel = Logger.Level.Info;
				}

				String message = GLDebugMessageARBCallback.getMessage(length, messagePointer);

				logger.log(messageLevel, "[OpenGL 0x%s] [%s] (%s) %s", Integer.toHexString(id), messageSource, messageSeverity, message);
			});

			glDebugMessageCallbackARB(callback, NULL);
		}
		else if (capabilities.GL_AMD_debug_output)
		{
			GLDebugMessageAMDCallback callback = GLDebugMessageAMDCallback.create((id, category, severity, length, messagePointer, userParam) ->
			{
				String messageSeverity = "NULL";
				switch (severity) {
					case GL_DEBUG_SEVERITY_HIGH_AMD -> messageSeverity = "High";
					case GL_DEBUG_SEVERITY_MEDIUM_AMD -> messageSeverity = "Medium";
					case GL_DEBUG_SEVERITY_LOW_AMD -> messageSeverity = "Low";
				}

				String messageCategory = "NULL";
				switch (category) {
					case GL_DEBUG_CATEGORY_API_ERROR_AMD -> messageCategory = "API ERROR";
					case GL_DEBUG_CATEGORY_WINDOW_SYSTEM_AMD -> messageCategory = "WINDOW SYSTEM";
					case GL_DEBUG_CATEGORY_DEPRECATION_AMD -> messageCategory = "DEPRECATION";
					case GL_DEBUG_CATEGORY_UNDEFINED_BEHAVIOR_AMD -> messageCategory = "UNDEFINED BEHAVIOR";
					case GL_DEBUG_CATEGORY_PERFORMANCE_AMD -> messageCategory = "PERFORMANCE";
					case GL_DEBUG_CATEGORY_SHADER_COMPILER_AMD -> messageCategory = "SHADER COMPILER";
					case GL_DEBUG_CATEGORY_APPLICATION_AMD -> messageCategory = "APPLICATION";
					case GL_DEBUG_CATEGORY_OTHER_AMD -> messageCategory = "OTHER";
				}

				String message = GLDebugMessageAMDCallback.getMessage(length, messagePointer);

				logger.error("[OpenGL 0x%s] [%s] (%s) %s", Integer.toHexString(id), messageCategory, messageSeverity, message);
			});
		}
		else
		{
			logger.warn("OpenGL logging could not be enabled!");
		}
	}

	public static String stringify(Vector2f vector)
	{
		return String.format("{ x: %.2f, y: %.2f }", vector.x, vector.y);
	}

	public static String stringify(Vector3f vector)
	{
		return String.format("{ x: %.2f, y: %.2f, z: %.2f }", vector.x, vector.y, vector.z);
	}

	public static String stringify(Matrix4f matrix)
	{
		return String.format("[[%.2f, %.2f, %.2f, %.2f], [%.2f, %.2f, %.2f, %.2f], [%.2f, %.2f, %.2f, %.2f], [%.2f, %.2f, %.2f, %.2f]]", matrix.get(0, 0), matrix.get(0, 1), matrix.get(0, 2), matrix.get(0, 3), matrix.get(1, 0), matrix.get(1, 1), matrix.get(1, 2), matrix.get(1, 3), matrix.get(2, 0), matrix.get(2, 1), matrix.get(2, 2), matrix.get(2, 3), matrix.get(3, 0), matrix.get(3, 1), matrix.get(3, 2), matrix.get(3, 3));
	}
}
