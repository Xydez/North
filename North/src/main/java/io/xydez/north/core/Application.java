package io.xydez.north.core;

import io.xydez.north.event.*;
import io.xydez.north.graphics.Renderer;
import io.xydez.north.io.Logger;
import io.xydez.north.utility.Utility;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

// TODO: Add javadocs for the entire engine

public abstract class Application
{
    public static final int NULL = 0;
    private static final Logger logger = new Logger(Logger.Level.Trace);
    private static final EventManager eventManager = new EventManager();

    private Vector2f lastMousePos;
    private Vector2f windowSize;
    private boolean mouseLocked = false;

    private final HashSet<Layer> layers = new HashSet<>();

    private long handle = NULL;

    /**
     * Create a new instance of this application. There can only be one Application instance per thread.
     * @param config The configuration for this application
     */
    protected Application(@NotNull ApplicationConfig config)
    {
        this.windowSize = new Vector2f(config.width, config.height);

        /* Initialize GLFW and OpenGL */

        // Setup logging for GLFW
        glfwSetErrorCallback((int error, long descriptionPointer) ->
        {
            String description = GLFWErrorCallback.getDescription(descriptionPointer);
            logger.error(String.format("[GLFW 0x%x] %s}", error, description));
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

        this.handle = glfwCreateWindow(config.width, config.height, config.title, NULL, NULL);
        if (this.handle == NULL)
            throw new RuntimeException("Failed to create GLFW window!");

        // Set the current GLFW context
        glfwMakeContextCurrent(this.handle);

        // Initialize an OpenGL context
        logger.trace("Initializing OpenGL context...");
        GL.createCapabilities();
        Utility.initGLLogging();

        // Enable vsync
        if (config.vsync)
            glfwSwapInterval(1);
        else
            glfwSwapInterval(0);

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

        // Scroll listener
        glfwSetScrollCallback(this.handle, (long window, double xoffset, double yoffset) ->
        {
            MouseScrollListener.MouseScrollEvent event = new MouseScrollListener.MouseScrollEvent(new Vector2f((float)xoffset, (float)yoffset));
            getEventManager().fire(MouseScrollListener.class, event);
        });

        // Automatically resize the viewport to the window
        glfwSetFramebufferSizeCallback(this.handle, (window, width1, height1) ->
        {
            Vector2f newSize = new Vector2f(width1, height1);
            WindowResizeListener.WindowResizeEvent event = new WindowResizeListener.WindowResizeEvent(this.windowSize, newSize);
            getEventManager().fire(WindowResizeListener.class, event);

            this.windowSize = newSize;

            glViewport(0, 0, width1, height1);
        });
    }

    /**
     * Get the logger for this application
     * @return The logger
     */
    public static Logger getLogger()
    {
        return logger;
    }

    /**
     * Get the event manager for this application
     * @return The event manager
     */
    public static EventManager getEventManager() { return eventManager; }

    /**
     * Called when this application should be initialized
     */
    protected void initialize() {}

    /**
     * Called when this application should be terminated
     */
    protected void terminate() {}

    /**
     * Called when this application should be updated
     * @param delta The time that has passed since the last frame
     */
    protected void update(double delta) {}

    /**
     * Called when this application should be rendered
     * @param renderer The renderer to render with
     */
    protected void render(@NotNull Renderer renderer) {}

    /**
     * Add a layer
     * @param layer The layer to be added
     */
    protected final void addLayer(@NotNull Layer layer)
    {
        this.layers.add(layer);
    }

    /**
     * Remove a layer
     * @param layer The layer to be removed
     */
    protected final void removeLayer(@NotNull Layer layer)
    {
        this.layers.remove(layer);
    }

    /**
     * Get the size of the window
     * @return A Vector2f containing the size of the window
     */
    @NotNull
    protected final Vector2f getWindowSize()
    {
        return this.windowSize;
    }

    /**
     * Lock or unlock the mouse
     * @param value Whether the mouse should be locked or unlocked
     */
    protected final void setMouseLocked(boolean value)
    {
        this.mouseLocked = value;
        glfwSetInputMode(this.handle, GLFW_CURSOR, value ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    /**
     * Get whether the mouse is locked or unlocked
     * @return True if the mosue is locked, otherwise false
     */
    public boolean isMouseLocked()
    {
        return mouseLocked;
    }

    /**
     * Get whether a key is pressed or not
     * @param key The keycode of the key to check
     * @return True if the key is pressed, otherwise false
     */
    public boolean isKeyPressed(int key)
    {
        return glfwGetKey(this.handle, key) == GLFW_PRESS;
    }

    /** Run this application
     * Initializes the application and starts the game loop, then terminates once Application#close is called.
     */
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
            List<Layer> myList = new ArrayList<Layer>(layers);
            myList.sort(Comparator.comparingInt(Layer::getIndex));

            /* Update the application */
            double delta = glfwGetTime() - lastTime;
            lastTime = glfwGetTime();
            update(delta);

            // Update all the enabled layers in order
            for (Layer layer : myList)
                if (layer.isEnabled())
                    layer.update(delta);

            /* Render the application */

            // Clear the color and depth buffers
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            render(renderer);

            // Render all the enabled layers in order
            for (Layer layer : myList)
                if (layer.isEnabled())
                    layer.render(renderer);

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

    /**
     * Terminate this application.
     */
    public final void close()
    {
        glfwSetWindowShouldClose(this.handle, true);
    }
}