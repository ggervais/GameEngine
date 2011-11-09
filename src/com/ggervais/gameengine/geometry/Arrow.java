package com.ggervais.gameengine.geometry;

import java.awt.Color;

import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;

public class Arrow extends Model {

	public Arrow() {
		super(2);
		create();
	}
	
	public void create() {
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0, 0, 0), Color.GREEN, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(1, 0, 0), Color.GREEN, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.75f, 0.25f, 0), Color.GREEN, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.75f, -0.25f, 0), Color.GREEN, 0, 0));
		
		this.indexBuffer.addIndex(2, 0);
		this.indexBuffer.addIndex(2, 1);
		
		this.indexBuffer.addIndex(2, 1);
		this.indexBuffer.addIndex(2, 2);
		
		this.indexBuffer.addIndex(2, 1);
		this.indexBuffer.addIndex(2, 3);
	}

}
