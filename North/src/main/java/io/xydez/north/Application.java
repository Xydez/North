package io.xydez.north;

import io.xydez.north.graphics.Renderer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public abstract class Application
{
    public static final int NULL = 0;

    private long handle = NULL;

    protected Application(String title, int width, int height)
    {
        /* Initialize GLFW and OpenGL */

        // Setup logging for GLFW
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize glfw
        if (!glfwInit())
        {
            throw new RuntimeException("Failed to initialize GLFW!");
        }

        // Create a basic glfw window
        glfwDefaultWindowHints();

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // MacOS only:
        //glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (this.handle == NULL)
            throw new RuntimeException("Failed to create GLFW window!");

        // Set the current GLFW context
        glfwMakeContextCurrent(this.handle);

        // Initialize an OpenGL context
        GL.createCapabilities();
        GLUtil.setupDebugMessageCallback(System.err);

        // Enable vsync
        glfwSwapInterval(1);

        /* Register GLFW callbacks */

        // Exit the application if the escape key is pressed
        glfwSetKeyCallback(this.handle, ((window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE)
            {
                this.close();
            }
        }));

        // Automatically resize the viewport to the window
        glfwSetFramebufferSizeCallback(this.handle, (window, width1, height1) -> glViewport(0, 0, width1, height1));
    }

    protected void initialize() {}
    protected void terminate() {}

    protected void update(double delta) {}
    protected void render(Renderer renderer) {}

    public final void run()
    {
        // Show the window
        glfwShowWindow(this.handle);

        // Initialize the application
        initialize();

        glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        Renderer renderer = new Renderer();

        double lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(this.handle))
        {
            /* Update the application */
            double delta = glfwGetTime() - lastTime;
            lastTime = glfwGetTime();

            update(delta);

            /* Render the application */

            // Clear the color and depth buffers
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            render(renderer);

            glfwSwapBuffers(this.handle);
            glfwPollEvents();
        }

        // Terminate the application
        terminate();

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