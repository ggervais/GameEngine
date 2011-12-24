package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.geometry.primitives.*;
import com.ggervais.gameengine.geometry.skinning.Bone;
import com.ggervais.gameengine.geometry.skinning.SkinWeights;
import com.ggervais.gameengine.input.InputController;
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
    protected Bone boneHierarchyRoot;

    private boolean recomputeBoundingBoxWhenGeometryIsDirty;
    private boolean isStreamed;
    private boolean normalsCalculated;

    protected List<SkinWeights> skinWeightsList;

    protected BoundingBox modelBoundingBox;
    private boolean isGeometryDirty;
    private boolean needsGeometryDataUpdate;

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
        this.skinWeightsList = new ArrayList<SkinWeights>();
        this.isStreamed = false;
        this.recomputeBoundingBoxWhenGeometryIsDirty = true;
        this.normalsCalculated = false;
	}

    public boolean isNeedsGeometryDataUpdate() {
        return needsGeometryDataUpdate;
    }

    public void setNeedsGeometryDataUpdate(boolean needsGeometryDataUpdate) {
        this.needsGeometryDataUpdate = needsGeometryDataUpdate;
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

    public boolean isGeometryDirty() {
        return this.isGeometryDirty;
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

    protected void computeBoundingBox(Matrix4x4 transform) {

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;

        float maxX = (-Float.MAX_VALUE);
        float maxY = (-Float.MAX_VALUE);
        float maxZ = (-Float.MAX_VALUE);

        VertexBuffer vertexBufferToUse = getVertexBuffer();
        for (int i = 0; i < vertexBufferToUse.size(); i++) {
            Vertex vertex = vertexBufferToUse.getVertex(i);
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
    
    public static BoundingBox computeBoundingBox(List<Point3D> points) {

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;

        float maxX = (-Float.MAX_VALUE);
        float maxY = (-Float.MAX_VALUE);
        float maxZ = (-Float.MAX_VALUE);
        
        for (Point3D point : points) {
            
            minX = Math.min(point.x(), minX);
            minY = Math.min(point.y(), minY);
            minZ = Math.min(point.z(), minZ);
            maxX = Math.max(point.x(), maxX);
            maxY = Math.max(point.y(), maxY);
            maxZ = Math.max(point.z(), maxZ);
        }
        
        Point3D minCorner = new Point3D(minX, minY, minZ);
        Point3D maxCorner = new Point3D(maxX, maxY, maxZ);

        return new BoundingBox(minCorner, maxCorner);
        
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

    public static List<Vector3D> computeVertexNormals(List<Point3D> points, IndexBuffer geometryIndexBuffer) {
        List<Vector3D> vertexNormals = new ArrayList<Vector3D>();

        int nbFaces = geometryIndexBuffer.getSubIndexBuffer(DEFAULT_NB_VERTICES_PER_FACE).size() / DEFAULT_NB_VERTICES_PER_FACE;

        // for each vertex
        for (int i = 0; i < points.size(); i++) {
            Vector3D sumOfFaceNormals = Vector3D.zero();
            // for each face
            List<Integer> subIndexBuffer = geometryIndexBuffer.getSubIndexBuffer(DEFAULT_NB_VERTICES_PER_FACE);
            for (int j = 0; j < subIndexBuffer.size(); j += DEFAULT_NB_VERTICES_PER_FACE) {
                // j points to the current vertex index
                int indexOfFirstVertexInFace = subIndexBuffer.get(j);
                int indexOfSecondVertexInFace = subIndexBuffer.get(j + 1);
                int indexOfThirdVertexInFace = subIndexBuffer.get(j + 2);

                // if indexed vertex == current vertex
                if (i == indexOfFirstVertexInFace || i == indexOfSecondVertexInFace || i == indexOfThirdVertexInFace) {

                    // get all vertices in face
                    Point3D firstVertexInFace = points.get(indexOfFirstVertexInFace);
                    Point3D secondVertexInFace = points.get(indexOfSecondVertexInFace);
                    Point3D thirdVertexInFace = points.get(indexOfThirdVertexInFace);

                    // compute face normal and add it to the sum for current vertex
                    Vector3D v1 = secondVertexInFace.sub(firstVertexInFace);
                    Vector3D v2 = thirdVertexInFace.sub(firstVertexInFace);
                    Vector3D faceNormal = v1.crossProduct(v2).normalized();

                    sumOfFaceNormals.add(faceNormal);
                }
            }

            if (geometryIndexBuffer.hasSubIndexBuffer(4)) {

                nbFaces += geometryIndexBuffer.getSubIndexBuffer(4).size() / 4;

                subIndexBuffer = geometryIndexBuffer.getSubIndexBuffer(4);
                for (int j = 0; j < subIndexBuffer.size(); j += 4) {
                    // j points to the current vertex index
                    int indexOfFirstVertexInFace = subIndexBuffer.get(j);
                    int indexOfSecondVertexInFace = subIndexBuffer.get(j + 1);
                    int indexOfThirdVertexInFace = subIndexBuffer.get(j + 2);
                    int indexOfFourthVertexInFace = subIndexBuffer.get(j + 3);

                    // if indexed vertex == current vertex
                    if (i == indexOfFirstVertexInFace || i == indexOfSecondVertexInFace || i == indexOfThirdVertexInFace || i == indexOfFourthVertexInFace) {

                        // get all vertices in face
                        Point3D firstVertexInFace = points.get(indexOfFirstVertexInFace);
                        Point3D secondVertexInFace = points.get(indexOfSecondVertexInFace);
                        Point3D thirdVertexInFace = points.get(indexOfThirdVertexInFace);

                        // compute face normal and add it to the sum for current vertex
                        Vector3D v1 = secondVertexInFace.sub(firstVertexInFace);
                        Vector3D v2 = thirdVertexInFace.sub(firstVertexInFace);
                        Vector3D faceNormal = v1.crossProduct(v2).normalized();

                        sumOfFaceNormals.add(faceNormal);
                    }
                }
            }

            // vertex normal is the average of face normals (sum over number of faces)
            Vector3D unweightedVertexNormal = Vector3D.zero();
            if (nbFaces > 0) {
                unweightedVertexNormal = sumOfFaceNormals.multiplied(1.0f / nbFaces).normalized();
            }
            vertexNormals.add(unweightedVertexNormal);
        }

        return vertexNormals;
    }
    
    protected void computeVertexNormals() throws UnsupportedOperationException {
        this.normals.clear();

        if (!this.indexBuffer.hasSubIndexBuffer(DEFAULT_NB_VERTICES_PER_FACE)) {
            log.warn("Only " + DEFAULT_NB_VERTICES_PER_FACE + " vertices per face supported right now.");
            return;
        }

        int nbFaces = this.indexBuffer.getSubIndexBuffer(DEFAULT_NB_VERTICES_PER_FACE).size() / DEFAULT_NB_VERTICES_PER_FACE;

        // for each vertex
        for (int i = 0; i < getVertexBuffer().size(); i++) {
            Vector3D sumOfFaceNormals = Vector3D.zero();
            // for each face
            List<Integer> subIndexBuffer = this.indexBuffer.getSubIndexBuffer(DEFAULT_NB_VERTICES_PER_FACE);
            for (int j = 0; j < subIndexBuffer.size(); j += DEFAULT_NB_VERTICES_PER_FACE) {
                // j points to the current vertex index
                int indexOfFirstVertexInFace = subIndexBuffer.get(j);
                int indexOfSecondVertexInFace = subIndexBuffer.get(j + 1);
                int indexOfThirdVertexInFace = subIndexBuffer.get(j + 2);

                // if indexed vertex == current vertex
                if (i == indexOfFirstVertexInFace || i == indexOfSecondVertexInFace || i == indexOfThirdVertexInFace) {

                    // get all vertices in face
                    Vertex firstVertexInFace = getVertexBuffer().getVertex(indexOfFirstVertexInFace);
                    Vertex secondVertexInFace = getVertexBuffer().getVertex(indexOfSecondVertexInFace);
                    Vertex thirdVertexInFace = getVertexBuffer().getVertex(indexOfThirdVertexInFace);

                    // compute face normal and add it to the sum for current vertex
                    Vector3D v1 = secondVertexInFace.getPosition().sub(firstVertexInFace.getPosition());
			        Vector3D v2 = thirdVertexInFace.getPosition().sub(firstVertexInFace.getPosition());
			        Vector3D faceNormal = v1.crossProduct(v2).normalized();

                    sumOfFaceNormals.add(faceNormal);
                }
            }

            if (this.indexBuffer.hasSubIndexBuffer(4)) {

                nbFaces += this.indexBuffer.getSubIndexBuffer(4).size() / 4;

                subIndexBuffer = this.indexBuffer.getSubIndexBuffer(4);
                for (int j = 0; j < subIndexBuffer.size(); j += 4) {
                    // j points to the current vertex index
                    int indexOfFirstVertexInFace = subIndexBuffer.get(j);
                    int indexOfSecondVertexInFace = subIndexBuffer.get(j + 1);
                    int indexOfThirdVertexInFace = subIndexBuffer.get(j + 2);
                    int indexOfFourthVertexInFace = subIndexBuffer.get(j + 3);
                    
                    // if indexed vertex == current vertex
                    if (i == indexOfFirstVertexInFace || i == indexOfSecondVertexInFace || i == indexOfThirdVertexInFace || i == indexOfFourthVertexInFace) {

                        // get all vertices in face
                        Vertex firstVertexInFace = getVertexBuffer().getVertex(indexOfFirstVertexInFace);
                        Vertex secondVertexInFace = getVertexBuffer().getVertex(indexOfSecondVertexInFace);
                        Vertex thirdVertexInFace = getVertexBuffer().getVertex(indexOfThirdVertexInFace);

                        // compute face normal and add it to the sum for current vertex
                        Vector3D v1 = secondVertexInFace.getPosition().sub(firstVertexInFace.getPosition());
                        Vector3D v2 = thirdVertexInFace.getPosition().sub(firstVertexInFace.getPosition());
                        Vector3D faceNormal = v1.crossProduct(v2).normalized();

                        sumOfFaceNormals.add(faceNormal);
                    }
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

        if (this.isGeometryDirty) {
            computeBoundingBox(new Matrix4x4());
            
            // This might not be the best place to recompute vertex normals, but it will do for now.
            if (!this.normalsCalculated) {
                computeVertexNormals();
                this.normalsCalculated = true;
            }
            this.isGeometryDirty = false;
            this.needsGeometryDataUpdate = true;
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
        for (Light light : this.lights) {
            if (light.isOn()) {
                renderer.enableLight(light);
            }
        }
        renderer.drawGeometry(this);
    }

    public Map<GlobalStateType, GlobalState> getStates() {
        return this.globalStates;
    }

    public Bone getBoneHierarchyRoot() {
        return boneHierarchyRoot;
    }

    public void setBoneHierarchyRoot(Bone boneHierarchyRoot) {
        this.boneHierarchyRoot = boneHierarchyRoot;
    }

    public List<SkinWeights> getSkinWeightsList() {
        return skinWeightsList;
    }

    public void setSkinWeightsList(List<SkinWeights> skinWeights) {
        this.skinWeightsList = skinWeights;
    }
    
    
    public float[] getVertexAttributesFloatArray() {

        int nbVertices = getVertexBuffer().getRealSize();
        int size = nbVertices * 7;
        float[] buffer = new float[size];
        
        int bufferIndex = 0;
        for (int i = 0; i < nbVertices; i++) {
            Point3D position = Point3D.zero();
            Vertex vertex = getVertexBuffer().getVertex(i);
            if (vertex != null) {
                position = vertex.getPosition();
            }
            
            Vector3D normal = Vector3D.zero();
            if (i < this.normals.size()) {
                normal = this.normals.get(i);
            }
            
            buffer[bufferIndex + 0] = position.x();
            buffer[bufferIndex + 1] = position.y();
            buffer[bufferIndex + 2] = position.z();
            buffer[bufferIndex + 3] = position.w();
            
            buffer[bufferIndex + 4] = normal.x();
            buffer[bufferIndex + 5] = normal.y();
            buffer[bufferIndex + 6] = normal.z();

            bufferIndex += 7;
        }
        
        return buffer;
    }

    public int getNbIndices(int nbVerticesPerFace) {
        int nbVertices = getVertexBuffer().size();
        return this.indexBuffer.getNbIndices(nbVerticesPerFace, nbVertices);
    }
    
    public int[] getIndexBufferIntegerArray(int nbVerticesPerFace) {

        int nbVertices = getVertexBuffer().size();

        List<Integer> intermediateIndices = new ArrayList<Integer>();
        for (int index : this.indexBuffer.getSubIndexBuffer(nbVerticesPerFace)) {
            if (index < nbVertices) {
                intermediateIndices.add(index);
            }
        }

        int[] buffer = new int[intermediateIndices.size()];
        int bufferIndex = 0;
        for (int index : intermediateIndices) {
            buffer[bufferIndex] = index;
            bufferIndex++;
        }
        
        return buffer;
    }

    public float[] getEffectFloatArray() {
        if (super.getEffect() != null) {
            return super.getEffect().getEffectBufferAsFloatArray(getVertexBuffer().size());
        } else {
            return new float[0];
        }
                
    }

    public boolean isStreamed() {
        return this.isStreamed;
    }

    protected void setStreamed(boolean isStreamed) {
        this.isStreamed = isStreamed;
    }

    protected boolean getRecomputeBoundingBoxWhenGeometryIsDirty() {
        return this.recomputeBoundingBoxWhenGeometryIsDirty;
    }

    protected void setRecomputeBoundingBoxWhenGeometryIsDirty(boolean value) {
        this.recomputeBoundingBoxWhenGeometryIsDirty = value;
    }
    
    public void setNormals(List<Vector3D> normals) {
        this.normals = normals;
    }
}
