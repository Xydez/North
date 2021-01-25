package io.xydez.sandbox;

import io.xydez.north.core.Application;
import io.xydez.north.core.ApplicationConfig;
import io.xydez.north.event.KeyboardListener;
import io.xydez.north.event.MouseMoveListener;
import io.xydez.north.event.WindowResizeListener;
import io.xydez.north.graphics.*;
import io.xydez.north.io.FileManager;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class SandboxApp extends Application implements KeyboardListener, MouseMoveListener, WindowResizeListener
{
    private VertexBuffer vbo;
    private IndexBuffer ibo;
    private VertexArray vao;
    private ShaderProgram program;

    private Texture testTexture;

    private double timer = 0.0;

    private final Vector3f velocity = new Vector3f().zero();

    private Camera.PerspectiveCamera camera;

    public SandboxApp()
    {
        super(generateConfig());
    }

    private static ApplicationConfig generateConfig()
    {
        ApplicationConfig config = new ApplicationConfig();
        config.title = "Sandbox App";
        config.width = 800;
        config.height = 600;

        return config;
    }

    @Override
    protected void initialize()
    {
        getLogger().trace("Hello from App!");
        getEventManager().addListener(KeyboardListener.class, this);
        getEventManager().addListener(MouseMoveListener.class, this);

        float[] vertices = new float[] {
            -0.5f,  0.5f, 0.0f,   0.0f, 1.0f,
             0.5f,  0.5f, 0.0f,   1.0f, 1.0f,
             0.5f, -0.5f, 0.0f,   1.0f, 0.0f,
            -0.5f, -0.5f, 0.0f,   0.0f, 0.0f
        };

        int[] indices = new int[] {
            0, 1, 2,
            0, 2, 3
        };

        VertexBufferLayout layout = new VertexBufferLayout();
        layout.push(VertexBufferLayout.VertexBufferElement.ElementType.Float, 3);
        layout.push(VertexBufferLayout.VertexBufferElement.ElementType.Float, 2);

        this.vbo = new VertexBuffer(vertices);

        this.ibo = new IndexBuffer(indices);

        this.vao = new VertexArray(layout, this.vbo, this.ibo);

        try {
            String vertexSource = FileManager.readClassFileToString("shaders/vertex.glsl");
            Shader vertexShader = new Shader(vertexSource, Shader.Type.Vertex);

            String fragmentSource = FileManager.readClassFileToString("shaders/fragment.glsl");
            Shader fragmentShader = new Shader(fragmentSource, Shader.Type.Fragment);

            this.program = new ShaderProgram(vertexShader, fragmentShader);

            this.testTexture = new Texture("textures/grass_side.png");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Vector2f windowSize = getWindowSize();
        this.camera = new Camera.PerspectiveCamera(new Vector3f(-1.0f, 1.0f, 2.0f), windowSize.x / windowSize.y, 70);

        // LOCK CAMERA
        this.setMouseLocked(true);
    }

    private int fps = 0;

    @Override
    protected void update(double delta)
    {
        this.timer += delta;
        if (timer >= 1.0)
        {
            //getLogger().debug(String.format("%.3f ms/frame (%dfps)", (timer / fps) * 1000.0, fps));
            timer -= 1.0;
            fps = 0;
        }

        fps += 1;

        this.camera.move(velocity.mul((float)delta, new Vector3f()));
    }

    @Override
    protected void render(Renderer renderer)
    {
        // getProjectionMatrix getViewMatrix
        Matrix4f mvp = this.camera.getProjectionMatrix().mul(this.camera.getViewMatrix(), new Matrix4f());
        //getLogger().trace(Utility.stringify(mvp));
        //this.close();

        this.program.bind();
        this.program.setUniform("mvp", mvp);
        this.program.setUniform("testTexture", 0);

        this.testTexture.bind();

        //this.program.setUniform("color", new Vector2f(((float)Math.sin(this.timer) + 1.0f) / 2.0f, ((float)Math.cos(this.timer) + 1.0f) / 2.0f));
        renderer.render(null, this.vao, this.ibo);
    }

    @Override
    protected void terminate()
    {
        this.testTexture.dispose();
        this.program.dispose();
        this.vbo.dispose();
        this.vao.dispose();
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
