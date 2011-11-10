package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;

import java.awt.*;
import java.util.Random;

public class CubeGeometry extends Geometry {

    private static final Random random = new Random();

	public CubeGeometry() {
		super(3);
		create();
	}
	
	public void create() {
		
		// Front face
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, 0.5f, 0.5f), Color.RED, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, -0.5f, 0.5f), Color.RED, 0, 1));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, -0.5f, 0.5f), Color.RED, 1, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, 0.5f, 0.5f), Color.RED, 1, 1));

        // Back face
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, 0.5f, -0.5f), Color.RED, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, -0.5f, -0.5f), Color.RED, 0, 1));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, -0.5f, -0.5f), Color.RED, 1, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, 0.5f, -0.5f), Color.RED, 1, 1));

        // Top face
        this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, 0.5f, -0.5f), Color.RED, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, 0.5f, 0.5f), Color.RED, 0, 1));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, 0.5f, 0.5f), Color.RED, 1, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, 0.5f, -0.5f), Color.RED, 1, 1));

        // Bottom face
        this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, -0.5f, 0.5f), Color.RED, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, -0.5f, -0.5f), Color.RED, 0, 1));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, -0.5f, -0.5f), Color.RED, 1, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, -0.5f, 0.5f), Color.RED, 1, 1));

        // Left face
        this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, 0.5f, -0.5f), Color.RED, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, -0.5f, -0.5f), Color.RED, 0, 1));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, -0.5f, 0.5f), Color.RED, 1, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, 0.5f, 0.5f), Color.RED, 1, 1));

        // Right face
        this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, 0.5f, 0.5f), Color.RED, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, -0.5f, 0.5f), Color.RED, 0, 1));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, -0.5f, -0.5f), Color.RED, 1, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, 0.5f, -0.5f), Color.RED, 1, 1));

        int vertexCounter = 0;
        for (int i = 0; i < 6; i++) {
            this.indexBuffer.addIndex(3, vertexCounter);
            this.indexBuffer.addIndex(3, vertexCounter + 1);
            this.indexBuffer.addIndex(3, vertexCounter + 2);

            this.indexBuffer.addIndex(3, vertexCounter + 2);
            this.indexBuffer.addIndex(3, vertexCounter + 3);
            this.indexBuffer.addIndex(3, vertexCounter);

            vertexCounter += 4;
        }
	}

    @Override
    protected void generateTextureCoords(Effect effect) {

        for (int i = 0; i < effect.nbTextures(); i++) {

            Texture texture = effect.getTexture(i);
            int cell = random.nextInt(texture.getNbCellsWidth() * texture.getNbCellsHeight());

            Vector3D min = texture.getMinBounds(cell);
            Vector3D max = texture.getMaxBounds(cell);
            float w = max.x() - min.x();
            float h = max.y() - min.y();

            TextureCoords texture00 = new TextureCoords(min.x() + 0 * w, min.y() + 0 * h);
		    TextureCoords texture10 = new TextureCoords(min.x() + 1 * w, min.y() + 0 * h);
		    TextureCoords texture01 = new TextureCoords(min.x() + 0 * w, min.y() + 1 * h);
		    TextureCoords texture11 = new TextureCoords(min.x() + 1 * w, min.y() + 1 * h);

            effect.clearTextureCoordinates(i, 3);
            for (int j = 0; j <= 6; j++) {
                effect.addTextureCoordinates(i, 3, texture00);
                effect.addTextureCoordinates(i, 3, texture01);
                effect.addTextureCoordinates(i, 3, texture11);

                effect.addTextureCoordinates(i, 3, texture11);
                effect.addTextureCoordinates(i, 3, texture10);
                effect.addTextureCoordinates(i, 3, texture00);
            }
        }
    }
}
