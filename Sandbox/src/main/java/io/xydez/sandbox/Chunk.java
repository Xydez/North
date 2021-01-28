package io.xydez.sandbox;

import io.xydez.north.graphics.Camera;
import io.xydez.north.graphics.Renderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.ArrayList;

public class Chunk implements AutoCloseable
{
	private static final Vector3i CHUNK_SIZE = new Vector3i(16, 16, 16);

	private final Block[][][] blocks;

	private BlockRenderer blockRenderer;

	public Chunk()
	{
		this.blocks = new Block[CHUNK_SIZE.x][CHUNK_SIZE.y][CHUNK_SIZE.z];
		this.blockRenderer = new BlockRenderer();
	}

	public void set(Vector3i position, Block block)
	{
		this.blocks[position.x][position.y][position.z] = block;
	}

	@Nullable
	public Block get(Vector3i position)
	{
		// If the block is outside the chunk, return null
		if (position.x < 0 || position.x >= CHUNK_SIZE.x || position.y < 0 || position.y >= CHUNK_SIZE.y || position.z < 0 || position.z >= CHUNK_SIZE.z)
			return null;

		return this.blocks[position.x][position.y][position.z];
	}

	public void render(Renderer renderer, Camera camera)
	{
		for (int x = 0; x < CHUNK_SIZE.x; x++)
			for (int y = 0; y < CHUNK_SIZE.y; y++)
				for (int z = 0; z < CHUNK_SIZE.z; z++)
				{
					Vector3i position = new Vector3i(x, y, z);

					Block block = this.get(position);

					// If there's no block, continue
					if (block == null)
						continue;

					ArrayList<Face> faces = new ArrayList<>();

					if (this.get(new Vector3i(position.x - 1, position.y, position.z)) == null)
						faces.add(Face.Left);

					if (this.get(new Vector3i(position.x + 1, position.y, position.z)) == null)
						faces.add(Face.Right);

					if (this.get(new Vector3i(position.x, position.y, position.z - 1)) == null)
						faces.add(Face.Front);

					if (this.get(new Vector3i(position.x, position.y, position.z + 1)) == null)
						faces.add(Face.Back);

					if (this.get(new Vector3i(position.x, position.y + 1, position.z)) == null)
						faces.add(Face.Top);

					if (this.get(new Vector3i(position.x, position.y - 1, position.z)) == null)
						faces.add(Face.Bottom);

					this.blockRenderer.render(renderer, camera, block, faces.toArray(new Face[0]));
				}
	}

	@Override
	public void close()
	{
		this.blockRenderer.close();
		this.blockRenderer = null;
	}
}
