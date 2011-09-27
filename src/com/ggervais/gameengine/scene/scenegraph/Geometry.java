package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.geometry.primitives.*;
import com.ggervais.gameengine.math.*;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingSphere;
import com.ggervais.gameengine.render.SceneRenderer;
import com.ggervais.gameengine.scene.scenegraph.renderstates.GlobalState;
import com.ggervais.gameengine.scene.scenegraph.renderstates.GlobalStateType;
import org.apache.log4j.Logger;

import java.util.*;

public abstract class Geometry extends Spatial {
    private static final int DEFAULT_NB_VERTICES_PER_FACE = 3;
    private static final Logger log = Logger.getLogger(Geometry.class);

	protected VertexBuffer vertexBuffer;
	protected IndexBuffer indexBuffer;
	protected TextureBuffer textureBuffer; // Per face texture coordinates.
	protected int nbVerticesPerFace;
	private List<Face> faces;
    protected int nbFaces;
    protected List<Vector3D> normals;

    private BoundingBox modelBoundingBox;
    private boolean isGeometryDirty;

    private Map<GlobalStateType, GlobalState> globalStates;

	public Geometry() {
		this(DEFAULT_NB_VERTICES_PER_FACE); // Defaults to triangles.
	}

	public Geometry(int nbVerticesPerFace) {
		super();
        this.vertexBuffer = new VertexBuffer();
		this.indexBuffer = new IndexBuffer();
		this.textureBuffer = new TextureBuffer();
        this.normals = new ArrayList<Vector3D>();
		this.nbVerticesPerFace = nbVerticesPerFace;
		this.faces = new ArrayList<Face>();
        this.isGeometryDirty = true;
        this.globalStates = new HashMap<GlobalStateType, GlobalState>();
        this.modelBoundingBox = new BoundingBox(Point3D.zero(), Point3D.zero());
        this.nbFaces = 0;
	}

    public Vector3D getNormal(int index) {
        if (index >= 0 && index < this.normals.size()) {
            return this.normals.get(index);
        }
        return null;
    }

    public void setGeometryDirty(boolean dirty) {
        this.isGeometryDirty = dirty;
    }

    public void addFace(Face face) {
        this.faces.add(face);
        resetNbFaces();
    }

    public void setNbFaces(int nbFaces) {
        this.nbFaces = MathUtils.clamp(nbFaces, 0, this.faces.size());
    }

    public void setNbVertices(int size) {
        this.vertexBuffer.setNbVertices(size);
    }

    public int getNbVertices() {
        return this.vertexBuffer.size();
    }

    public Face getFace(int index) {
        if (index < 0 || index > this.faces.size() - 1) {
            return null;
        }

        return this.faces.get(index);
    }

    public int getNbFaces() {
        return this.nbFaces;
    }

    public void resetNbFaces() {
        this.nbFaces = this.faces.size();
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

    private void computeBoundingSphere(Matrix4x4 transform) {
        float sumX = 0;
        float sumY = 0;
        float sumZ = 0;

        int nbVertices = 0;

        for (int i = 0; i < this.vertexBuffer.size(); i++) {
            Vertex vertex = this.vertexBuffer.getVertex(i);
            Point3D position = transform.mult(vertex.getPosition());
            sumX += position.x();
            sumY += position.y();
            sumZ += position.z();

            nbVertices++;
        }

        float averageX = sumX / nbVertices;
        float averageY = sumY / nbVertices;
        float averageZ = sumZ / nbVertices;

        Point3D center = new Point3D(averageX, averageY, averageZ);

        float radiusSquared = (-Float.MAX_VALUE);
        for (int i = 0; i < this.vertexBuffer.size(); i++) {
            Vertex vertex = this.vertexBuffer.getVertex(i);
            Point3D position = transform.mult(vertex.getPosition());

            Vector3D diff = position.sub(center);
            radiusSquared = Math.max(diff.lengthSquared(), radiusSquared);
        }

        // Only compute sqrt at the end.
        float radius = (float) Math.sqrt(radiusSquared);

        BoundingSphere sphere = new BoundingSphere(center, radius);

        this.boundingSphere = sphere;
    }

    private void computeBoundingBox(Matrix4x4 transform) {


        // TODO fix precision

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;

        float maxX = (-Float.MAX_VALUE);
        float maxY = (-Float.MAX_VALUE);
        float maxZ = (-Float.MAX_VALUE);

        for (int i = 0; i < this.vertexBuffer.size(); i++) {
            Vertex vertex = this.vertexBuffer.getVertex(i);
            Point3D position = transform.mult(vertex.getPosition());

            minX = Math.min(position.x(), minX);
            minY = Math.min(position.y(), minY);
            minZ = Math.min(position.z(), minZ);
            maxX = Math.max(position.x(), maxX);
            maxY = Math.max(position.y(), maxY);
            maxZ = Math.max(position.z(), maxZ);
        }

        Point3D minCorner = new Point3D(minX, minY, minZ);
        Point3D maxCorner = new Point3D(maxX, maxY, maxZ);
        BoundingBox box = new BoundingBox(minCorner, maxCorner);

        this.modelBoundingBox = box;
    }

    protected abstract void generateTextureCoords(Effect effect);

    @Override
    public void setEffect(Effect effect) {

        int size = this.vertexBuffer.getRealSize();

        effect.resetColorList();
        effect.initializeColors(size);
        for (int i = 0; i < size; i++) {
            effect.setColor(i, effect.getColor());
        }

        generateTextureCoords(effect);

        super.setEffect(effect);
    }

    private void computeVertexNormals() throws UnsupportedOperationException {
        this.normals.clear();

        if (this.nbVerticesPerFace != DEFAULT_NB_VERTICES_PER_FACE) {
            throw new UnsupportedOperationException("Only " + DEFAULT_NB_VERTICES_PER_FACE + " vertices per face supported right now.");
        }

        int nbFaces = this.indexBuffer.size() / this.nbVerticesPerFace;

        // for each vertex
        for (int i = 0; i < this.vertexBuffer.size(); i++) {
            Vector3D sumOfFaceNormals = Vector3D.zero();
            // for each face
            for (int j = 0; j < this.indexBuffer.size(); j += this.nbVerticesPerFace) {
                // j points to the current vertex index
                int indexOfFirstVertexInFace = this.indexBuffer.getIndex(j);
                int indexOfSecondVertexInFace = this.indexBuffer.getIndex(j + 1);
                int indexOfThirdVertexInFace = this.indexBuffer.getIndex(j + 2);

                // if indexed vertex == current vertex
                if (i == indexOfFirstVertexInFace || i == indexOfSecondVertexInFace || i == indexOfThirdVertexInFace) {

                    // get all vertices in face
                    Vertex firstVertexInFace = this.vertexBuffer.getVertex(indexOfFirstVertexInFace);
                    Vertex secondVertexInFace = this.vertexBuffer.getVertex(indexOfSecondVertexInFace);
                    Vertex thirdVertexInFace = this.vertexBuffer.getVertex(indexOfThirdVertexInFace);

                    // compute face normal and add it to the sum for current vertex
                    Vector3D v1 = secondVertexInFace.getPosition().sub(firstVertexInFace.getPosition());
			        Vector3D v2 = thirdVertexInFace.getPosition().sub(firstVertexInFace.getPosition());
			        Vector3D faceNormal = v1.crossProduct(v2).normalized();

                    sumOfFaceNormals.add(faceNormal);
                }
            }

            // vertex normal is the average of face normals (sum over number of faces)
            Vector3D unweightedVertexNormal = Vector3D.zero();
            if (nbFaces > 0) {
                unweightedVertexNormal = sumOfFaceNormals.multiplied(1.0f / nbFaces).normalized();
            }
            this.normals.add(unweightedVertexNormal);
        }
    }

    @Override
    public void updateWorldBound() {

        //this.isGeometryDirty = true;
        if (this.isGeometryDirty) {
            computeBoundingBox(new Matrix4x4());

            // This might not be the best place to recompute vertex normals, but it will do for now.
            computeVertexNormals();

            this.isGeometryDirty = false;
        }

        BoundingBox copyBox = this.modelBoundingBox.copy();

        copyBox.transform(this.worldTransform);
        this.boundingBox = copyBox;
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
