package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;

import java.awt.*;

public class SphereGeometry extends Geometry {

	private static int DEFAULT_RINGS_WIDTH = 20;
	private static int DEFAULT_RINGS_HEIGHT = 20;

	private int nbRingsWidth;
	private int nbRingsHeight;

	public SphereGeometry(int nbRingsWidth, int nbRingsHeight) {
		super(3);
		this.nbRingsWidth = nbRingsWidth;
		this.nbRingsHeight = nbRingsHeight;
		create();
	}

	public SphereGeometry() {
		this(DEFAULT_RINGS_WIDTH, DEFAULT_RINGS_HEIGHT);
	}

    @Override
    protected void generateTextureCoords(Effect effect) {
        // TODO implement texture coordinates for a sphere.
    }

    public void create() {
		int resolutionWidth = this.nbRingsWidth + 1;
		int resolutionHeight = this.nbRingsHeight + 1;
		
		float ds = (float) (Math.PI / this.nbRingsWidth);
		float dt = (float) (2 * Math.PI / this.nbRingsHeight);
		
		for (float i = 0; i < resolutionWidth; i++) {
			for (int j = 0; j < resolutionHeight; j++) {
				float s = i * ds;
				float t = j * dt;
				float x = (float) Math.cos(s);
				float y = (float) (Math.sin(s) * Math.cos(t));
				float z = (float) (Math.sin(s) * Math.sin(t));
				
				Vertex vertex = new Vertex(new Point3D(x, y, z), Color.BLUE, 0, 0);
				this.vertexBuffer.addVertex(vertex);
			}
		}
		
		int index = 0;
		for (float i = 0; i < this.nbRingsWidth; i++) {
			for (int j = 0; j < this.nbRingsHeight; j++) {
				int first = index;
				int second = index + resolutionHeight;
				int third = index + resolutionHeight + 1;
				int fourth = index + 1;
				
				Face face1 = new Face();
				Face face2 = new Face();
				
				Vertex firstVertex = this.vertexBuffer.getVertex(first);
				Vertex secondVertex = this.vertexBuffer.getVertex(second);
				Vertex thirdVertex = this.vertexBuffer.getVertex(third);
				Vertex fourthVertex = this.vertexBuffer.getVertex(fourth);
				
				face1.addVertex(firstVertex);
				face1.addVertex(secondVertex);
				face1.addVertex(thirdVertex);
				
				face2.addVertex(firstVertex);
				face2.addVertex(thirdVertex);
				face2.addVertex(fourthVertex);

                /*this.vertexBuffer.addVertex(firstVertex);
                this.vertexBuffer.addVertex(secondVertex);
                this.vertexBuffer.addVertex(thirdVertex);
                this.vertexBuffer.addVertex(fourthVertex);*/
				
				this.indexBuffer.addIndex(3, first);
				this.indexBuffer.addIndex(3, second);
				this.indexBuffer.addIndex(3, third);
				
				this.indexBuffer.addIndex(3, first);
				this.indexBuffer.addIndex(3, third);
				this.indexBuffer.addIndex(3, fourth);
				
				addFace(face1);
				addFace(face2);
				
				index++;
			}
			index++;
		}
	}
}
