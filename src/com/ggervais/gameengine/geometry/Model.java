package com.ggervais.gameengine.geometry;

import java.util.ArrayList;
import java.util.List;

import com.ggervais.gameengine.geometry.primitives.*;
import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingSphere;

public class Model {
	
	private static final int DEFAULT_NB_VERTICES_PER_FACE = 3;
	
	protected VertexBuffer vertexBuffer;
	protected IndexBuffer indexBuffer;
	protected TextureBuffer textureBuffer; // Per face texture coordinates.
	protected int nbVerticesPerFace;
	protected List<Face> faces;
	
	public Model() {
		this(DEFAULT_NB_VERTICES_PER_FACE); // Defaults to triangles.
	}
	
	public Model(int nbVerticesPerFace) {
		this.vertexBuffer = new VertexBuffer();
		this.indexBuffer = new IndexBuffer();
		this.textureBuffer = new TextureBuffer();
		this.nbVerticesPerFace = nbVerticesPerFace;
		this.faces = new ArrayList<Face>();
	}
	
	public List<Face> getFaces() {
		return this.faces;
	}
	
	public VertexBuffer getVertexBuffer() {
		return this.vertexBuffer;
	}
	
	public IndexBuffer getIndexBuffer() {
		return this.indexBuffer;
	}
	
	public TextureBuffer getTextureBuffer() {
		return this.textureBuffer;
	}
	
	public int getNbVerticesPerFace() {
		return this.nbVerticesPerFace;
	}
	
	public float getScale() {
		return 1.0f;
	}

    public BoundingSphere getBoundingSphere() {
        return getBoundingSphere(new Matrix4x4());
    }

    public BoundingSphere getBoundingSphere(Matrix4x4 transform) {
        float sumX = 0;
        float sumY = 0;
        float sumZ = 0;

        int nbVertices = 0;
        for (Face face: this.faces) {
            for (int i = 0; i < face.nbVertices(); i++) {
                Vertex vertex = face.getVertex(i);
                Point3D position = transform.mult(vertex.getPosition());
                sumX += position.x();
                sumY += position.y();
                sumZ += position.z();

                nbVertices++;
            }
        }

        float averageX = sumX / nbVertices;
        float averageY = sumY / nbVertices;
        float averageZ = sumZ / nbVertices;

        Point3D center = new Point3D(averageX, averageY, averageZ);

        float radiusSquared = Float.MIN_VALUE;
        for (Face face: this.faces) {
            for (int i = 0; i < face.nbVertices(); i++) {
                Vertex vertex = face.getVertex(i);
                Point3D position = transform.mult(vertex.getPosition());

                Vector3D diff = position.sub(center);
                radiusSquared = Math.max(diff.lengthSquared(), radiusSquared);
            }
        }

        // Only compute sqrt at the end.
        float radius = (float) Math.sqrt(radiusSquared);

        BoundingSphere sphere = new BoundingSphere(center, radius);

        return sphere;
    }

    public BoundingBox getBoundingBox() {
        return getBoundingBox(new Matrix4x4());
    }

    // TODO: use dirty bit.
    public BoundingBox getBoundingBox(Matrix4x4 transform) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;

        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;
        float maxZ = Float.MIN_VALUE;

        for (Face face: this.faces) {
            for (int i = 0; i < face.nbVertices(); i++) {
                Vertex vertex = face.getVertex(i);
                Point3D position = transform.mult(vertex.getPosition());
                minX = Math.min(position.x(), minX);
                minY = Math.min(position.y(), minY);
                minZ = Math.min(position.z(), minZ);
                maxX = Math.max(position.x(), maxX);
                maxY = Math.max(position.y(), maxY);
                maxZ = Math.max(position.z(), maxZ);
            }
        }

        Point3D minCorner = new Point3D(minX, minY, minZ);
        Point3D maxCorner = new Point3D(maxX, maxY, maxZ);
        BoundingBox box = new BoundingBox(minCorner, maxCorner);

        return box;
    }
}
