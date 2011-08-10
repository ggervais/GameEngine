package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.scene.scenegraph.Geometry;

import java.awt.*;

public class CubeGeometry extends Geometry {

	public CubeGeometry() {
		super(4);
		create();
	}
	
	public void create() {
		
		// First face
		Vertex vertex1 = new Vertex(new Point3D(0.5f, 0.5f, 0.5f), Color.RED, 0, 0);
		Vertex vertex2 = new Vertex(new Point3D(-0.5f, 0.5f, 0.5f), Color.RED, 0, 1);
		Vertex vertex3 = new Vertex(new Point3D(-0.5f, -0.5f, 0.5f), Color.RED, 1, 0);
		Vertex vertex4 = new Vertex(new Point3D(0.5f, -0.5f, 0.5f), Color.RED, 1, 1);
		
		Vertex vertex5 = new Vertex(new Point3D(0.5f, 0.5f, -0.5f), Color.RED, 0, 0);
		Vertex vertex6 = new Vertex(new Point3D(-0.5f, 0.5f, -0.5f), Color.RED, 0, 1);
		Vertex vertex7 = new Vertex(new Point3D(-0.5f, -0.5f, -0.5f), Color.RED, 1, 0);
		Vertex vertex8 = new Vertex(new Point3D(0.5f, -0.5f, -0.5f), Color.RED, 1, 1);
		
		/*this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, 0.5f, 0.5f), Color.RED, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, 0.5f, 0.5f), Color.RED, 0, 1));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, -0.5f, 0.5f), Color.RED, 1, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, -0.5f, 0.5f), Color.RED, 1, 1));
		
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, 0.5f, -0.5f), Color.RED, 0, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, 0.5f, -0.5f), Color.RED, 0, 1));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(-0.5f, -0.5f, -0.5f), Color.RED, 1, 0));
		this.vertexBuffer.addVertex(new Vertex(new Point3D(0.5f, -0.5f, -0.5f), Color.RED, 1, 1));
		
		this.indexBuffer.addIndex(0);
		this.indexBuffer.addIndex(1);
		this.indexBuffer.addIndex(2);
		this.indexBuffer.addIndex(3);
		
		this.indexBuffer.addIndex(0);
		this.indexBuffer.addIndex(4);
		this.indexBuffer.addIndex(5);
		this.indexBuffer.addIndex(1);
		
		this.indexBuffer.addIndex(2);
		this.indexBuffer.addIndex(6);
		this.indexBuffer.addIndex(7);
		this.indexBuffer.addIndex(3);
		
		
		this.indexBuffer.addIndex(7);
		this.indexBuffer.addIndex(6);
		this.indexBuffer.addIndex(5);
		this.indexBuffer.addIndex(4);
		
		this.indexBuffer.addIndex(3);
		this.indexBuffer.addIndex(7);
		this.indexBuffer.addIndex(4);
		this.indexBuffer.addIndex(0);
		
		this.indexBuffer.addIndex(1);
		this.indexBuffer.addIndex(5);
		this.indexBuffer.addIndex(6);
		this.indexBuffer.addIndex(2);
		
		this.textureBuffer.addCoords(1, 0);
		this.textureBuffer.addCoords(0, 0);
		this.textureBuffer.addCoords(0, 1);
		this.textureBuffer.addCoords(1, 1);
		
		this.textureBuffer.addCoords(1, 1);
		this.textureBuffer.addCoords(1, 0);
		this.textureBuffer.addCoords(0, 0);
		this.textureBuffer.addCoords(0, 1);
		
		this.textureBuffer.addCoords(1, 1);
		this.textureBuffer.addCoords(1, 0);
		this.textureBuffer.addCoords(0, 0);
		this.textureBuffer.addCoords(0, 1);
		
		this.textureBuffer.addCoords(0, 1);
		this.textureBuffer.addCoords(1, 1);
		this.textureBuffer.addCoords(1, 0);
		this.textureBuffer.addCoords(0, 0);
		
		this.textureBuffer.addCoords(0, 1);
		this.textureBuffer.addCoords(1, 1);
		this.textureBuffer.addCoords(1, 0);
		this.textureBuffer.addCoords(0, 0);
		
		this.textureBuffer.addCoords(1, 0);
		this.textureBuffer.addCoords(0, 0);
		this.textureBuffer.addCoords(0, 1);
		this.textureBuffer.addCoords(1, 1);
		*/
		
		TextureCoords texture00 = new TextureCoords(0, 0);
		TextureCoords texture10 = new TextureCoords(1, 0);
		TextureCoords texture01 = new TextureCoords(0, 1);
		TextureCoords texture11 = new TextureCoords(1, 1);
		
		// 1st
		Face face1 = new Face();
		face1.addVertex(vertex1);
		face1.addVertex(vertex2);
		face1.addVertex(vertex3);
		face1.addVertex(vertex4);
		face1.addTextureCoords(texture10);
		face1.addTextureCoords(texture00);
		face1.addTextureCoords(texture01);
		face1.addTextureCoords(texture11);
		
		// 2nd
		Face face2 = new Face();
		face2.addVertex(vertex1);
		face2.addVertex(vertex5);
		face2.addVertex(vertex6);
		face2.addVertex(vertex2);
		face2.addTextureCoords(texture11);
		face2.addTextureCoords(texture10);
		face2.addTextureCoords(texture00);
		face2.addTextureCoords(texture01);
		
		// 3rd
		Face face3 = new Face();
		face3.addVertex(vertex3);
		face3.addVertex(vertex7);
		face3.addVertex(vertex8);
		face3.addVertex(vertex4);
		face3.addTextureCoords(texture11);
		face3.addTextureCoords(texture10);
		face3.addTextureCoords(texture00);
		face3.addTextureCoords(texture01);
		
		// 4th
		Face face4 = new Face();
		face4.addVertex(vertex8);
		face4.addVertex(vertex7);
		face4.addVertex(vertex6);
		face4.addVertex(vertex5);
		face4.addTextureCoords(texture01);
		face4.addTextureCoords(texture11);
		face4.addTextureCoords(texture10);
		face4.addTextureCoords(texture00);
		
		// 5th
		Face face5 = new Face();
		face5.addVertex(vertex4);
		face5.addVertex(vertex8);
		face5.addVertex(vertex5);
		face5.addVertex(vertex1);
		face5.addTextureCoords(texture01);
		face5.addTextureCoords(texture11);
		face5.addTextureCoords(texture10);
		face5.addTextureCoords(texture00);
		
		// 6th
		Face face6 = new Face();
		face6.addVertex(vertex2);
		face6.addVertex(vertex6);
		face6.addVertex(vertex7);
		face6.addVertex(vertex3);
		face6.addTextureCoords(texture10);
		face6.addTextureCoords(texture00);
		face6.addTextureCoords(texture01);
		face6.addTextureCoords(texture11);
		
		addFace(face1);
		addFace(face2);
		addFace(face3);
		addFace(face4);
		addFace(face5);
		addFace(face6);
	}

}
