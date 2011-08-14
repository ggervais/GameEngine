package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;

import java.awt.*;

public class CubeGeometry extends Geometry {

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
            this.indexBuffer.addIndex(vertexCounter);
            this.indexBuffer.addIndex(vertexCounter + 1);
            this.indexBuffer.addIndex(vertexCounter + 2);

            this.indexBuffer.addIndex(vertexCounter + 2);
            this.indexBuffer.addIndex(vertexCounter + 3);
            this.indexBuffer.addIndex(vertexCounter);

            vertexCounter += 4;
        }
	}

    @Override
    protected void generateTextureCoords(Effect effect) {

        TextureCoords texture00 = new TextureCoords(0, 0);
		TextureCoords texture10 = new TextureCoords(1, 0);
		TextureCoords texture01 = new TextureCoords(0, 1);
		TextureCoords texture11 = new TextureCoords(1, 1);

        for (int i = 0; i < effect.nbTextures(); i++) {
            effect.clearTextureCoordinates(i);
            for (int j = 0; j <= 6; j++) {
                effect.addTextureCoordinates(i, texture00);
                effect.addTextureCoordinates(i, texture01);
                effect.addTextureCoordinates(i, texture11);
                effect.addTextureCoordinates(i, texture10);
            }
        }
    }
}
