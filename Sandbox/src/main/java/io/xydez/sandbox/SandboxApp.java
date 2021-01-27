package io.xydez.sandbox;

import io.xydez.north.core.Application;
import io.xydez.north.core.ApplicationConfig;
import io.xydez.north.event.KeyboardListener;
import io.xydez.north.event.MouseMoveListener;
import io.xydez.north.event.WindowResizeListener;
import io.xydez.north.graphics.*;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.io.IOException;

public class SandboxApp extends Application implements KeyboardListener, MouseMoveListener, WindowResizeListener
{
//    private VertexBuffer vbo;
//    private IndexBuffer ibo;
//    private VertexArray vao;
//    private ShaderProgram program;

    //private BlockRenderer blockRenderer;

    private Texture dirtTexture, grassSideTexture, grassTopTexture;

    private double timer = 0.0;
    private double renderTimer = 0.0;

    private final Vector3f velocity = new Vector3f().zero();
    private Chunk chunk;

    private Camera.PerspectiveCamera camera;

    public SandboxApp()
    {
        super(generateConfig());
    }

    private static ApplicationConfig generateConfig()
    {
        ApplicationConfig config = new ApplicationConfig();
        config.title = "Sandbox App";
        config.width = 1200;
        config.height = 800;
        config.vsync = true;

        return config;
    }

    @Override
    protected void initialize()
    {
        getLogger().trace("Hello from App!");
        getEventManager().addListener(KeyboardListener.class, this);
        getEventManager().addListener(MouseMoveListener.class, this);
        getEventManager().addListener(WindowResizeListener.class, this);

//        float[] vertices = new float[] {
//            -0.5f,  0.5f,  0.5f,   0.0f, 1.0f,
//             0.5f,  0.5f,  0.5f,   1.0f, 1.0f,
//             0.5f, -0.5f,  0.5f,   1.0f, 0.0f,
//            -0.5f, -0.5f,  0.5f,   0.0f, 0.0f,
//            -0.5f,  0.5f, -0.5f,   0.0f, 1.0f,
//             0.5f,  0.5f, -0.5f,   1.0f, 1.0f,
//             0.5f, -0.5f, -0.5f,   1.0f, 0.0f,
//            -0.5f, -0.5f, -0.5f,   0.0f, 0.0f
//        };
//
//        int[] indices = new int[] {
//            0, 1, 2,
//            0, 2, 3,
//
//            1, 5, 6,
//            1, 6, 2,
//
//            5, 4, 7,
//            5, 7, 6,
//
//            4, 0, 3,
//            4, 3, 7,
//
//            0, 1, 5,
//            0, 5, 4,
//
//            7, 3, 2,
//            7, 2, 6
//        };
//
//        VertexBufferLayout layout = new VertexBufferLayout();
//        layout.push(VertexBufferLayout.VertexBufferElement.ElementType.Float, 3);
//        layout.push(VertexBufferLayout.VertexBufferElement.ElementType.Float, 2);
//
//        this.vbo = new VertexBuffer(vertices);
//
//        this.ibo = new IndexBuffer(indices);
//
//        this.vao = new VertexArray(layout, this.vbo, this.ibo);
//
        try {
//            String vertexSource = FileManager.readClassFileToString("shaders/vertex.glsl");
//            String fragmentSource = FileManager.readClassFileToString("shaders/fragment.glsl");
//
//            Shader vertexShader = new Shader(vertexSource, Shader.Type.Vertex);
//            Shader fragmentShader = new Shader(fragmentSource, Shader.Type.Fragment);
//
//            this.program = new ShaderProgram(vertexShader, fragmentShader);
            this.dirtTexture = new Texture("textures/dirt.png");
            this.grassSideTexture = new Texture("textures/grass_side.png");
            this.grassTopTexture = new Texture("textures/grass_top.png");
//
//            vertexShader.close();
//            fragmentShader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        Vector2f windowSize = getWindowSize();
        this.camera = new Camera.PerspectiveCamera(new Vector3f(0.0f, 0.0f, 0.0f), windowSize.x / windowSize.y, 70);

        GrassBlock.initialize();
        //this.blockRenderer = new BlockRenderer();

        this.chunk = new Chunk();

        for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++)
                this.chunk.set(new Vector3i(x, 0, z), new GrassBlock(new Vector3i(x, 0, z)));

        // LOCK CAMERA
        //this.setMouseLocked(true);
    }

    private int fps = 0;

    @Override
    protected void update(double delta)
    {
        this.timer += delta;
        this.renderTimer += getLastRenderTime();
        if (timer >= 1.0)
        {
            getLogger().trace("%.3f ms/frame (%.3fms/frame / %dfps)", (renderTimer / fps) * 1000.0, (timer / fps) * 1000.0, fps);
            setTitle(String.format("%d FPS, %.3f ms/frame (%.1f%%)", fps, (renderTimer / fps) * 1000.0, 100.0 * ((renderTimer / fps) / (timer / fps))));
            timer -= 1.0;
            renderTimer = 0.0;
            fps = 0;
        }

        fps += 1;

        this.camera.move(velocity.mul((float)delta * 6.0f, new Vector3f()));
    }

    @Override
    protected void render(@NotNull Renderer renderer)
    {
        // getProjectionMatrix getViewMatrix
        //Matrix4f mvp = this.camera.getProjectionMatrix().mul(this.camera.getViewMatrix(), new Matrix4f());
        //getLogger().trace(Utility.stringify(mvp));
        //this.close();

        //this.program.bind();
        //this.program.setUniform("mvp", mvp);
        //this.program.setUniform("testTexture", 0);

        //this.testTexture.bind();

        //this.program.setUniform("color", new Vector2f(((float)Math.sin(this.timer) + 1.0f) / 2.0f, ((float)Math.cos(this.timer) + 1.0f) / 2.0f));
        //renderer.render(null, this.vao, this.ibo);

        //Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);
        //Vector3f RIGHT = new Vector3f(1.0f, 0.0f, 0.0f);

        /*

        // Front
        this.quadRenderer.render(renderer, this.camera, new Vector3f(0.0f, 0.0f, 0.5f), new Quaternionf(), this.grassSideTexture);

        // Left
        this.quadRenderer.render(renderer, this.camera, new Vector3f(-0.5f, 0.0f, 0.0f), new Quaternionf().rotateAxis((float)(-(Math.PI) / 2.0), UP), this.grassSideTexture);

        // Back
        this.quadRenderer.render(renderer, this.camera, new Vector3f(0.0f, 0.0f, -0.5f), new Quaternionf().rotateAxis((float)(-(Math.PI * 2) / 2.0), UP), this.grassSideTexture);

        // Right
        this.quadRenderer.render(renderer, this.camera, new Vector3f(0.5f, 0.0f, 0.0f), new Quaternionf().rotateAxis((float)(-(Math.PI * 3) / 2.0), UP), this.grassSideTexture);

        // Top
        this.quadRenderer.render(renderer, this.camera, new Vector3f(0.0f, 0.5f, 0.0f), new Quaternionf().rotateAxis((float)(-(Math.PI) / 2.0), RIGHT), this.grassTopTexture);

        // Bottom
        this.quadRenderer.render(renderer, this.camera, new Vector3f(0.0f, -0.5f, 0.0f), new Quaternionf().rotateAxis((float)((Math.PI) / 2.0), RIGHT), this.dirtTexture);

        */

        this.chunk.render(renderer, camera);
    }

    @Override
    protected void terminate()
    {
        GrassBlock.terminate();

        this.chunk.close();
        //this.blockRenderer.close();

        this.dirtTexture.close();
        this.grassSideTexture.close();
        this.grassTopTexture.close();
        //this.program.close();

        //this.vbo.close();
        //this.ibo.close();
        //this.vao.close();
    }

    @Override
    public void onKeyboard(KeyboardEvent event)
    {
        //if (event.getAction() == KeyboardAction.Press)
        //    getLogger().debug(event.getKeycode());

        if (event.getKeycode() == 65)
        {
            if (event.getAction() == KeyboardAction.Press)
                this.velocity.x += 1;
            else if (event.getAction() == KeyboardAction.Release)
                this.velocity.x -= 1;
        }
        else if (event.getKeycode() == 68)
        {
            if (event.getAction() == KeyboardAction.Press)
                this.velocity.x -= 1;
            else if (event.getAction() == KeyboardAction.Release)
                this.velocity.x += 1;
        }
        else if (event.getKeycode() == 87)
        {
            if (event.getAction() == KeyboardAction.Press)
                this.velocity.z -= 1;
            else if (event.getAction() == KeyboardAction.Release)
                this.velocity.z += 1;
        }
        else if (event.getKeycode() == 83)
        {
            if (event.getAction() == KeyboardAction.Press)
                this.velocity.z += 1;
            else if (event.getAction() == KeyboardAction.Release)
                this.velocity.z -= 1;
        }
        else if (event.getKeycode() == 32)
        {
            if (event.getAction() == KeyboardAction.Press)
                this.velocity.y += 1;
            else if (event.getAction() == KeyboardAction.Release)
                this.velocity.y -= 1;
        }
        else if (event.getKeycode() == 341)
        {
            if (event.getAction() == KeyboardAction.Press)
                this.velocity.y -= 1;
            else if (event.getAction() == KeyboardAction.Release)
                this.velocity.y += 1;
        }
        else if (event.getKeycode() == 76 && event.getAction() == KeyboardAction.Release)
        {
            setMouseLocked(!isMouseLocked());
        }

        // W: 87, S: 83
        // UP: 265, DOWN: 264
        // SPACE 32, LCONTROL 341
    }

    @Override
    public void onMouseMove(MouseMoveEvent event)
    {
        if (!isMouseLocked())
            return;

        Vector2f vector = event.getNewPosition().sub(event.getOldPosition(), new Vector2f()).mul(1.0f / 360.0f);
        //this.camera.rotate(vector.y, vector.x);

        //this.camera.rotate(0.0f, 0.01f);

        this.camera.rotate(vector.y, vector.x);
    }

    @Override
    public void onWindowResize(WindowResizeEvent event)
    {
        Vector2f size = event.getNewSize();
        this.camera.setAspectRatio(size.x / size.y);
    }
}
