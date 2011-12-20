package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.geometry.MeshGeometry;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.geometry.primitives.VertexBuffer;
import com.ggervais.gameengine.geometry.skinning.AnimationKey;
import com.ggervais.gameengine.geometry.skinning.AnimationSet;
import com.ggervais.gameengine.geometry.skinning.Bone;
import com.ggervais.gameengine.geometry.skinning.SkinWeights;
import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.math.MathUtils;
import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import com.ggervais.gameengine.timing.Controller;
import net.java.games.input.Component;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnimationController extends Controller {

    private static final Logger log = Logger.getLogger(AnimationController.class);
    private static final float DEFAULT_CYCLE_DURATION = 3000;
    private float cycleDuration;
    private long lastCycleTime;
    private int currentAnimationSet;
    private boolean interpolate;
    private boolean isPreviousNDown;

    public AnimationController(float cycleDuration, boolean interpolate) {
        this.cycleDuration = cycleDuration;
        this.lastCycleTime = 0;
        this.currentAnimationSet = 0;
        this.interpolate = interpolate;
        this.isPreviousNDown = false;
    }

    public AnimationController(float cycleDuration) {
        this(cycleDuration, true);
    }
    
    public AnimationController() {
        this(DEFAULT_CYCLE_DURATION, true);
    }

    @Override
    public void setControlledObject(Spatial controlledObject) {
        if (controlledObject != null) {
            if (controlledObject instanceof MeshGeometry) {
                super.setControlledObject(controlledObject);
            } else {
                throw new IllegalArgumentException("Controlled object for an AnimationController should be of type MeshGeometry.");
            }
        }
    }

    @Override
    public void doUpdate(long currentTime, InputController inputController) {

        long adjustedTime = currentTime - this.pauseOffset;

        MeshGeometry geometry = (MeshGeometry) this.controlledSpatialObject;
        geometry.initializeSkinningIfNecessary();
        Bone rootBone = geometry.getBoneHierarchyRoot();

        if (this.lastCycleTime == 0) {
            this.lastCycleTime = adjustedTime;
        }

        boolean isNDown = inputController.isKeyDown(Component.Identifier.Key.N);

        long dt = adjustedTime - this.lastCycleTime;
        if (dt > this.cycleDuration || (this.isPreviousNDown && !isNDown)) {

            // Cycle animation set.
            int nbAnimationSet = geometry.nbAnimationSets();
            this.currentAnimationSet = (this.currentAnimationSet < nbAnimationSet - 1 ? this.currentAnimationSet + 1 : 0);

            this.lastCycleTime = adjustedTime;
            dt = 0;
        }
        this.isPreviousNDown = isNDown;


        float ratio = MathUtils.clamp(dt / this.cycleDuration, 0, 1);

        Point3D[] intermediatePositions = new Point3D[geometry.getOriginalVertexBuffer().getRealSize()];

        float timeElapsedInAnimationSet = ratio * this.cycleDuration;

        rootBone.updateMatrices(geometry.getAnimationSet(this.currentAnimationSet), this.cycleDuration, timeElapsedInAnimationSet);
        List<SkinWeights> weightsList = geometry.getSkinWeightsList();
        for (SkinWeights weights : weightsList) {
            String boneName = weights.getBoneName();
            Bone affectedBone = rootBone.findByName(boneName);
            List<AnimationKey> animationKeys = geometry.getAnimationKeysForBoneAndAnimationSet(affectedBone, this.currentAnimationSet);

            if (animationKeys.size() == 0) {
                break;
            }

            int animationKeyIndex = (int) Math.floor(ratio * animationKeys.size());

            float timeForEachKey = this.cycleDuration / animationKeys.size();
            float timeAtStartOfKey = timeForEachKey * animationKeyIndex;
            float t = (timeElapsedInAnimationSet - timeAtStartOfKey) / timeForEachKey;
            
            Matrix4x4 transformA = affectedBone.getFinalMatrix();
            Matrix4x4 transformB = affectedBone.getNextFinalMatrix();
            
            Map<Integer, Float> indicesWeights = weights.getIndicesWeights();

            for (int index : weights.getIndicesWeights().keySet()) {
                float weight = indicesWeights.get(index);
                if (intermediatePositions[index] == null) {
                    intermediatePositions[index] = Point3D.zero();
                }
                Point3D originalPosition = geometry.getOriginalVertexBuffer().getVertex(index).getPosition();
                Vector3D v = Point3D.sub(originalPosition, Point3D.zero());

                if (originalPosition != null) {
                    Vector3D va = transformA.mult(v).multiplied(weight);
                    Vector3D vb = transformB.mult(v).multiplied(weight);
                    
                    Vector3D interpolated = Vector3D.lerp(va, vb, t);
                    intermediatePositions[index].add(interpolated);
                }
            }
        }

        VertexBuffer skinnedVertexBuffer = geometry.getSkinnedVertexBuffer();
        for (int i = 0; i < geometry.getOriginalVertexBuffer().getRealSize(); i++) {
            Point3D intermediatePosition = intermediatePositions[i];
            Vertex vertex = skinnedVertexBuffer.getVertex(i);
            if (vertex != null) {
                if (intermediatePosition != null) {
                    vertex.setPosition(intermediatePosition);
                } else {
                    Vertex originalVertex = geometry.getOriginalVertexBuffer().getVertex(i);
                    if (originalVertex != null) {
                        vertex.setPosition(originalVertex.getPosition());
                    }
                }
            }
        }

        AnimationSet animationSet = geometry.getAnimationSet(this.currentAnimationSet);
        List<BoundingBox> boundingBoxes = geometry.getBoundingBoxes().get(animationSet);
        if (boundingBoxes != null) {
            float exactIndex = ratio * (boundingBoxes.size() - 1);
            int boundingBoxIndex = (int) Math.floor(exactIndex);
            
            float t = exactIndex - (boundingBoxIndex + 0f);

            int nextBoundingBoxIndex = (boundingBoxIndex < boundingBoxes.size() - 1 ? boundingBoxIndex + 1 : 0);
            BoundingBox boundingBox = boundingBoxes.get(boundingBoxIndex);
            BoundingBox nextBoundingBox = boundingBoxes.get(nextBoundingBoxIndex);
            
            Point3D interpolatedMinCorner = Point3D.lerp(boundingBox.getMinCorner(), nextBoundingBox.getMinCorner(), t);
            Point3D interpolatedMaxCorner = Point3D.lerp(boundingBox.getMaxCorner(), nextBoundingBox.getMaxCorner(), t);

            geometry.setBoundingBox(new BoundingBox(interpolatedMinCorner, interpolatedMaxCorner));
        }
        
        List<List<Vector3D>> vertexNormals = geometry.getVertexNormals(animationSet);
        if (vertexNormals != null) {
            float exactIndex = ratio * (vertexNormals.size() - 1);

            int vertexNormalsIndex = (int) Math.floor(exactIndex);
            int nextVertexNormalsIndex = (vertexNormalsIndex < vertexNormals.size() - 1 ? vertexNormalsIndex + 1 : 0);
            
            float t = exactIndex - (vertexNormalsIndex + 0f);

            List<Vector3D> normals = vertexNormals.get(vertexNormalsIndex);
            List<Vector3D> nextNormals = vertexNormals.get(nextVertexNormalsIndex);
            
            List<Vector3D> realNormals = new ArrayList<Vector3D>();
            for (int i = 0; i < normals.size(); i++) {
                Vector3D normal = normals.get(i);
                Vector3D nextNormal = nextNormals.get(i);
                Vector3D interpolatedNormal = Vector3D.lerp(normal, nextNormal, t);
                realNormals.add(interpolatedNormal);
            }

            geometry.setNormals(realNormals);
        }
        geometry.setGeometryDirty(true);
    }
}
