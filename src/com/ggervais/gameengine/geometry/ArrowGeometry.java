package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.scene.scenegraph.Geometry;

import java.awt.*;

public class ArrowGeometry extends Geometry {

	public ArrowGeometry() {
		super(2);
		create();
	}
	
	public void create() {
		Vertex vertex1 = new Vertex(new Point3D(0, 0, 0), Color.GREEN, 0, 0);
		Vertex vertex2 = new Vertex(new Point3D(1, 0, 0), Color.GREEN, 0, 0);
		Vertex vertex3 = new Vertex(new Point3D(0.75f, 0.25f, 0), Color.GREEN, 0, 0);
		Vertex vertex4 = new Vertex(new Point3D(0.75f, -0.25f, 0), Color.GREEN, 0, 0);

        Face face1 = new Face();
        face1.addVertex(vertex1);
        face1.addVertex(vertex2);

        Face face2 = new Face();
        face2.addVertex(vertex2);
        face2.addVertex(vertex3);

        Face face3 = new Face();
        face3.addVertex(vertex2);
        face3.addVertex(vertex4);
	}

}
