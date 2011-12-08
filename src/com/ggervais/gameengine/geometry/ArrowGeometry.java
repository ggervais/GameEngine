package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;

import java.awt.*;

public class ArrowGeometry extends Geometry {

	public ArrowGeometry() {
		super(2);
		create();
	}

    @Override
    protected void generateTextureCoords(Effect effect) {
        // Do nothing.
    }

    public void create() {
		Vertex vertex1 = new Vertex(new Point3D(0, 0, 0), Color.GREEN, 0, 0);
		Vertex vertex2 = new Vertex(new Point3D(1, 0, 0), Color.GREEN, 0, 0);
		Vertex vertex3 = new Vertex(new Point3D(0.75f, 0.25f, 0), Color.GREEN, 0, 0);
		Vertex vertex4 = new Vertex(new Point3D(0.75f, -0.25f, 0), Color.GREEN, 0, 0);

        this.vertexBuffer.addVertex(vertex1);
        this.vertexBuffer.addVertex(vertex2);
        this.vertexBuffer.addVertex(vertex3);
        this.vertexBuffer.addVertex(vertex4);

        this.indexBuffer.addIndex(2, 0);
        this.indexBuffer.addIndex(2, 1);

        this.indexBuffer.addIndex(2, 1);
        this.indexBuffer.addIndex(2, 2);

        this.indexBuffer.addIndex(2, 1);
        this.indexBuffer.addIndex(2, 4);
	}

}
