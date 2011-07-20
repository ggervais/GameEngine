package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.geometry.primitives.*;
import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingSphere;
import com.ggervais.gameengine.render.SceneRenderer;
import com.ggervais.gameengine.scene.scenegraph.renderstates.GlobalState;
import com.ggervais.gameengine.scene.scenegraph.renderstates.GlobalStateType;

import java.util.*;

public class Geometry extends Spatial {
    private static final int DEFAULT_NB_VERTICES_PER_FACE = 3;

    private BoundingBox boundingBox;
    private BoundingSphere boundingSphere;

	protected VertexBuffer vertexBuffer;
	protected IndexBuffer indexBuffer;
	protected TextureBuffer textureBuffer; // Per face texture coordinates.
	protected int nbVerticesPerFace;
	protected List<Face> faces;

    boolean isBoundingBoxDirty;
    boolean isBoundingSphereDirty;

    private Map<GlobalStateType, GlobalState> globalStates;

	public Geometry() {
		this(DEFAULT_NB_VERTICES_PER_FACE); // Defaults to triangles.
	}

	public Geometry(int nbVerticesPerFace) {
		super();
        this.vertexBuffer = new VertexBuffer();
		this.indexBuffer = new IndexBuffer();
		this.textureBuffer = new TextureBuffer();
		this.nbVerticesPerFace = nbVerticesPerFace;
		this.faces = new ArrayList<Face>();
        this.isBoundingBoxDirty = true;
        this.isBoundingSphereDirty = true;
        this.globalStates = new HashMap<GlobalStateType, GlobalState>();
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
        if (this.isBoundingSphereDirty) {
            computeBoundingSphere(transform);
            this.isBoundingBoxDirty = false;
        }
        return this.boundingSphere;
    }

    private void computeBoundingSphere(Matrix4x4 transform) {
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

        this.boundingSphere = sphere;
    }

    public BoundingBox getBoundingBox(Matrix4x4 transform) {
        if (this.isBoundingBoxDirty) {
            computeBoundingBox(new Matrix4x4());
            this.isBoundingBoxDirty = false;
        }
        return this.boundingBox;
    }

    public BoundingBox getBoundingBox() {
        return getBoundingBox(new Matrix4x4());
    }

    private void computeBoundingBox(Matrix4x4 transform) {
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

        this.boundingBox = box;
    }

    @Override
    public void updateWorldBound() {
        // Transform bounding box/sphere.
    }

    @Override
    protected void updateState(Map<GlobalStateType, Stack<GlobalState>> stateMap) {
        Set<GlobalStateType> keys = stateMap.keySet();
        for (GlobalStateType stateType : keys) {
            this.globalStates.put(stateType, stateMap.get(stateType).peek());
        }
    }



    @Override
    public void draw(SceneRenderer renderer) {
        renderer.drawGeometry(this);
    }

    public Map<GlobalStateType, GlobalState> getStates() {
        return this.globalStates;
    }
}