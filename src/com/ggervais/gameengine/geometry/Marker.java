package com.ggervais.gameengine.geometry;

import java.awt.Color;

import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;

public class Marker extends Model {

	public Marker() {
		super(2);
		create();
	}
	
	public void create() {
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0, 0, 0), Color.RED, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(1, 0, 0), Color.RED, 0, 0));
	
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0, 0, 0), Color.GREEN, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0, 1, 0), Color.GREEN, 0, 0));
		
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0, 0, 0), Color.BLUE, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0, 0, 1), Color.BLUE, 0, 0));
		
		this.indexBuffer.addIndex(2, 0);
		this.indexBuffer.addIndex(2, 1);

		this.indexBuffer.addIndex(2, 2);
		this.indexBuffer.addIndex(2, 3);

		this.indexBuffer.addIndex(2, 4);
		this.indexBuffer.addIndex(2, 5);
	}
	
}
