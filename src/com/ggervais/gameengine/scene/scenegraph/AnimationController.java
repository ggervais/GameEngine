package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.geometry.MeshGeometry;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.geometry.primitives.VertexBuffer;
import com.ggervais.gameengine.geometry.skinning.AnimationKey;
import com.ggervais.gameengine.geometry.skinning.Bone;
import com.ggervais.gameengine.geometry.skinning.SkinWeights;
import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.math.MathUtils;
import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.timing.Controller;

import java.util.List;
import java.util.Map;

public class AnimationController extends Controller {

    private static final float DEFAULT_CYCLE_DURATION = 3000;
    private float cycleDuration;
    private float lastCycleTime;
    private int currentAnimationSet;

    public AnimationController() {
        this.cycleDuration = DEFAULT_CYCLE_DURATION;
        this.lastCycleTime = 0;
        this.currentAnimationSet = 0;
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

        MeshGeometry geometry = (MeshGeometry) this.controlledSpatialObject;
        Bone rootBone = geometry.getBoneHierarchyRoot();
        
        float dt = currentTime - (this.lastCycleTime + this.pauseOffset);
        if (dt > this.cycleDuration) {
            // Cycle animation set.
            this.lastCycleTime = currentTime;
            dt = 0;
        }

        float ratio = MathUtils.clamp(dt / this.cycleDuration, 0, 1);

        Point3D[] intermediatePositions = new Point3D[geometry.getOriginalVertexBuffer().getRealSize()];

        rootBone.updateMatrices();
        List<SkinWeights> weightsList = geometry.getSkinWeightsList();
        for (SkinWeights weights : weightsList) {
            String boneName = weights.getBoneName();
            Bone affectedBone = rootBone.findByName(boneName);
            List<AnimationKey> animationKeys = geometry.getAnimationKeysForBoneAndAnimationSet(affectedBone, this.currentAnimationSet);
            Matrix4x4 boneCombinedMatrix = affectedBone.getRawCombinedMatrix();
            Matrix4x4 boneSkinOffsetMatrix = affectedBone.getSkinOffsetMatrix();

            if (animationKeys.size() == 0) {
                break;
            }

            int animationKeyIndex = (int) Math.floor(ratio * animationKeys.size());
            int nextAnimationKeyIndex = (animationKeyIndex < animationKeys.size() - 1 ? animationKeyIndex + 1 : 0);

            float timeElapsedInAnimationSet = ratio * this.cycleDuration;
            float timeForEachKey = this.cycleDuration / animationKeys.size();
            float timeAtStartOfKey = timeForEachKey * animationKeyIndex;
            float t = (timeElapsedInAnimationSet - timeAtStartOfKey) / timeForEachKey;

            AnimationKey currentAnimationKey = animationKeys.get(animationKeyIndex);
            AnimationKey nextAnimationKey = animationKeys.get(nextAnimationKeyIndex);

            Matrix4x4 transformA = currentAnimationKey.getTransformMatrix();
            Matrix4x4 transformB = nextAnimationKey.getTransformMatrix();
            
            Map<Integer, Float> indicesWeights = weights.getIndicesWeights();

            for (int index : weights.getIndicesWeights().keySet()) {
                float weight = indicesWeights.get(index);
                if (intermediatePositions[index] == null) {
                    intermediatePositions[index] = Point3D.zero();
                }
                Point3D position = intermediatePositions[index];
                Point3D originalPosition = geometry.getOriginalVertexBuffer().getVertex(index).getPosition();
                Vector3D v = Point3D.sub(originalPosition, Point3D.zero());

                if (originalPosition != null) {
                    Vector3D va = transformA.mult(v).multiplied(weight);
                    Vector3D vb = transformB.mult(v).multiplied(weight);
                    
                    Vector3D interpolated = Vector3D.add(va, Vector3D.sub(vb, va).multiplied(t));
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
                }
            } else {
                Vertex originalVertex = geometry.getOriginalVertexBuffer().getVertex(i);
                if (originalVertex != null) {
                    vertex.setPosition(originalVertex.getPosition());
                }
            }
        }
    }
}
