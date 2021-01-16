package io.xydez.north;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public abstract class Application
{
    protected static final int NULL = 0;

    private long handle = NULL;

    protected Application(String title, int width, int height)
    {
        // Setup logging for GLFW
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize glfw
        if (!glfwInit())
        {
            throw new RuntimeException("Failed to initialize GLFW!");
        }

        // Create a basic glfw window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (this.handle == NULL)
            throw new RuntimeException("Failed to create GLFW window!");

        // Exit the application if the escape key is pressed
        glfwSetKeyCallback(this.handle, ((window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE)
            {
                glfwSetWindowShouldClose(window, true);
            }
        }));

        // Center the application window
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        assert vidMode != null;
        glfwSetWindowPos(this.handle, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);

        // Set the current OpenGL context
        glfwMakeContextCurrent(this.handle);

        // Disable vsync
        glfwSwapInterval(0);

        // Initialize the application
        initialize();
    }

    protected void initialize() {}
    protected void termiate() {}

    protected void load() {}
    protected void unload() {}

	public final void run()
    {
        // Load the application
        load();

        // Show the window
        glfwShowWindow(this.handle);

        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        while (!glfwWindowShouldClose(this.handle))
        {
            // Clear the color and depth buffers
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glfwSwapBuffers(this.handle);
            glfwPollEvents();
        }

        // Unload the application
        unload();

        // Terminate the application
        termiate();

        // Free glfw resources
        glfwFreeCallbacks(this.handle);
        glfwDestroyWindow(this.handle);
        this.handle = NULL;

        glfwTerminate();

        //noinspection ConstantConditions
        glfwSetErrorCallback(null).free();
    }

    public final void close()
    {
        glfwSetWindowShouldClose(this.handle, true);
    }
}