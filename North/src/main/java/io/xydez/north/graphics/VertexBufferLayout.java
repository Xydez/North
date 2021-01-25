package io.xydez.north.graphics;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;

public class VertexBufferLayout
{
    private final ArrayList<VertexBufferElement> elements = new ArrayList<>();

    public void push(VertexBufferElement.ElementType type, int count)
    {
        elements.add(new VertexBufferElement(type, count));
    }

    protected ArrayList<VertexBufferElement> getElements()
    {
        return elements;
    }

    public static class VertexBufferElement
    {
        public enum ElementType
        {
            Float(GL_FLOAT, 4), Integer(GL_INT, 4);

            protected final int glEnum;
            protected final int size;

            ElementType(int glEnum, int size)
            {
                this.glEnum = glEnum;
                this.size = size;
            }
        }

        private final ElementType type;
        private final int count;

        public VertexBufferElement(ElementType type, int count)
        {
            this.type = type;
            this.count = count;
        }

        public ElementType getType()
        {
            return type;
        }

        public int getCount()
        {
            return count;
        }
    }
}
