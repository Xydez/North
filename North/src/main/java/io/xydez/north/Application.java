package io.xydez.north;

import io.xydez.north.event.EventManager;
import io.xydez.north.event.KeyboardListener;
import io.xydez.north.event.MouseButtonListener;
import io.xydez.north.event.MouseMoveListener;
import io.xydez.north.graphics.Renderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public abstract class Application
{
    public static final int NULL = 0;
    private static final Logger logger = LogManager.getRootLogger();
    private static final EventManager eventManager = new EventManager();

    private Vector2f lastMousePos;

    private long handle = NULL;

    protected Application(String title, int width, int height)
    {
        /* Initialize GLFW and OpenGL */

        // Setup logging for GLFW
        glfwSetErrorCallback((int error, long descriptionPointer) ->
        {
            String description = GLFWErrorCallback.getDescription(descriptionPointer);
            logger.error("[GLFW 0x{:x}] {}}", error, description);
        });

        // Initialize glfw
        logger.trace("Initializing GLFW...");
        if (!glfwInit())
        {
            throw new RuntimeException("Failed to initialize GLFW!");
        }

        // Create a basic glfw window
        logger.trace("Creating window...");

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
        logger.trace("Initializing OpenGL context...");
        GL.createCapabilities();
        Utility.initGLLogging();

        // Enable vsync
        glfwSwapInterval(1);

        /* Register GLFW callbacks */

        // Key listener
        glfwSetKeyCallback(this.handle, ((window, key, scancode, action, mods) -> {
            KeyboardListener.KeyboardEvent event = new KeyboardListener.KeyboardEvent(key, scancode, action == GLFW_PRESS ? KeyboardListener.KeyboardAction.Press : (action == GLFW_RELEASE ? KeyboardListener.KeyboardAction.Release : KeyboardListener.KeyboardAction.Repeat));
            getEventManager().fire(KeyboardListener.class, event);

            if (key == GLFW_KEY_ESCAPE)
            {
                this.close();
            }
        }));

        glfwSetMouseButtonCallback(this.handle, (long window, int button, int action, int mods) ->
        {
            MouseButtonListener.MouseAction mouseAction;
            switch(action)
            {
                case GLFW_PRESS -> mouseAction = MouseButtonListener.MouseAction.Press;
                case GLFW_RELEASE -> mouseAction = MouseButtonListener.MouseAction.Release;
                default -> {
                    getLogger().warn(String.format("Unknown mouse action %d. Ignoring event.", action));
                    return;
                }
            }

            MouseButtonListener.MouseButton mouseButton;
            switch (button)
            {
                case GLFW_MOUSE_BUTTON_LEFT -> mouseButton = MouseButtonListener.MouseButton.Left;
                case GLFW_MOUSE_BUTTON_MIDDLE -> mouseButton = MouseButtonListener.MouseButton.Middle;
                case GLFW_MOUSE_BUTTON_RIGHT -> mouseButton = MouseButtonListener.MouseButton.Right;
                default -> {
                    getLogger().warn(String.format("Mouse button %d was %s, but isn't currently supported. Ignoring event.", button, mouseAction == MouseButtonListener.MouseAction.Press ? "pressed" : "released"));
                    return;
                }
            }

            MouseButtonListener.MouseButtonEvent event = new MouseButtonListener.MouseButtonEvent(mouseButton, mouseAction);
            getEventManager().fire(MouseButtonListener.class, event);
        });

        // Before cursor listener
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            DoubleBuffer x = stack.mallocDouble(1);
            DoubleBuffer y = stack.mallocDouble(1);

            glfwGetCursorPos(this.handle, x, y);

            this.lastMousePos = new Vector2f((float)(x.get()), (float)(y.get()));
        }

        // Cursor listener
        glfwSetCursorPosCallback(this.handle, (long window, double xpos, double ypos) ->
        {
            Vector2f mousePos = new Vector2f((float)xpos, (float)ypos);
            MouseMoveListener.MouseMoveEvent event = new MouseMoveListener.MouseMoveEvent(this.lastMousePos, mousePos);
            getEventManager().fire(MouseMoveListener.class, event);
            this.lastMousePos = mousePos;
        });

        // Automatically resize the viewport to the window
        glfwSetFramebufferSizeCallback(this.handle, (window, width1, height1) -> glViewport(0, 0, width1, height1));
    }

    public static Logger getLogger()
    {
        return logger;
    }
    public static EventManager getEventManager() { return eventManager; }

    protected void initialize() {}
    protected void terminate() {}

    protected void update(double delta) {}
    protected void render(Renderer renderer) {}

    protected final Vector2f getWindowSize()
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);

            glfwGetWindowSize(this.handle, widthBuffer, heightBuffer);

            return new Vector2f(widthBuffer.get(0), heightBuffer.get());
        }
    }

    protected final void setMouseLocked(boolean value)
    {
        glfwSetInputMode(this.handle, GLFW_CURSOR, value ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    public final void run()
    {
        // Show the window
        glfwShowWindow(this.handle);

        // Initialize the application
        logger.trace("Initializing application");
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
        logger.trace("Terminating application...");
        terminate();

        // Free glfw resources
        logger.trace("Terminating glfw...");
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