package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.geometry.primitives.VertexBuffer;
import com.ggervais.gameengine.geometry.skinning.Animation;
import com.ggervais.gameengine.geometry.skinning.AnimationSet;
import com.ggervais.gameengine.geometry.skinning.Bone;
import com.ggervais.gameengine.geometry.skinning.SkinWeights;
import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.math.*;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;
import com.ggervais.gameengine.scene.scenegraph.Transformation;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class represents a base geometry (a mesh).
public class MeshGeometry extends Geometry {

    private static final float DELAY = 50f;
    private VertexBuffer skinnedVertexBuffer;
    private boolean useSkinnedVersion;
    private List<AnimationSet> animationSets;
    private static final Logger log = Logger.getLogger(MeshGeometry.class);
    private long lastUpdate;
    private long lastChangeTime;
    private int currentAnimationSet;
    private float currentAnimationSetDuration;

    public MeshGeometry() {
        super();
        this.useSkinnedVersion = false;
        this.animationSets = new ArrayList<AnimationSet>();
        this.lastUpdate = 0;
        this.lastChangeTime = 0;
        this.currentAnimationSet = -1;
        this.currentAnimationSetDuration = 5000f;
    }

    @Override
    protected void generateTextureCoords(Effect effect) {
        // Do nothing, as we don't know the actual geometry.
    }

    public void addAnimationSet(AnimationSet animationSet) {
        this.animationSets.add(animationSet);
    }

    @Override
    public void updateGeometryState(long currentTime, InputController inputController, boolean isInitiator) {
        super.updateGeometryState(currentTime, inputController, isInitiator);

        if (this.boneHierarchyRoot == null) {
            return;
        }

        if (this.lastUpdate == 0) {
            this.lastUpdate = currentTime;
        }

        if (this.lastChangeTime == 0) {
            this.lastChangeTime = currentTime;
        }

        if (this.currentAnimationSet == -1) {
            this.currentAnimationSet = 0;
        }

        if (this.animationSets.size() > 0) {

            if (currentTime - this.lastUpdate >= DELAY) {
                this.boneHierarchyRoot.incrementCurrentAnimationKey();
                this.lastUpdate = currentTime;
            }

            if (currentTime - this.lastChangeTime >= this.currentAnimationSetDuration * 3 && this.animationSets.size() > 1) {
                if (this.currentAnimationSet < this.animationSets.size() - 1) {
                    this.currentAnimationSet++;
                } else {
                    this.currentAnimationSet = 0;
                }
                AnimationSet animationSet = this.animationSets.get(this.currentAnimationSet);
                if (animationSet.getNbAnimations() > 0) {
                    Animation firstAnimation = animationSet.getAnimation(0);
                    int nbAnimationKeys = firstAnimation.getNbAnimationKeys();
                    this.currentAnimationSetDuration = nbAnimationKeys * DELAY;
                }
                this.boneHierarchyRoot.setCurrentAnimationSet(animationSet);
                this.lastChangeTime = currentTime;
            }
        }
        float ratio = (currentTime - this.lastUpdate) / DELAY;

        this.boneHierarchyRoot.updateMatrices();
        this.skinnedVertexBuffer = this.vertexBuffer.copy();

        Map<Integer, Boolean> vertexVisited = new HashMap<Integer, Boolean>();
        for (int i = 0; i < this.skinnedVertexBuffer.getRealSize(); i++) {
            vertexVisited.put(i, false);
        }
        for (SkinWeights weights : this.skinWeightsList) {
            Bone bone = this.boneHierarchyRoot.findByName(weights.getBoneName());
            Matrix4x4 finalMatrixForCurrentKey = bone.getFinalMatrix();
            Matrix4x4 finalMatrixForNextKey = bone.getNextFinalMatrix();

            Transformation boneTransformation = new Transformation();

            RotationMatrix currentRotationMatrix = finalMatrixForCurrentKey.extractRotationMatrix();
            RotationMatrix nextRotationMatrix = finalMatrixForNextKey.extractRotationMatrix();
            Quaternion qa = Quaternion.createFromRotationMatrix(currentRotationMatrix);
            Quaternion qb = Quaternion.createFromRotationMatrix(nextRotationMatrix);
            Quaternion qm = Quaternion.slerp(qa, qb, ratio);
            RotationMatrix rotationMatrix = RotationMatrix.createFromQuaternion(qm);
            boneTransformation.setRotationMatrix(rotationMatrix);

            ScaleMatrix currentScaleMatrix = finalMatrixForCurrentKey.extractScaleMatrix();
            ScaleMatrix nextScaleMatrix = finalMatrixForNextKey.extractScaleMatrix();
            Point3D sa = currentScaleMatrix.getScale();
            Point3D sb = nextScaleMatrix.getScale();
            Point3D sm = Point3D.lerp(sa, sb, ratio);
            boneTransformation.setScale(sm.x(), sm.y(), sm.z());

            TranslationMatrix currentTranslationMatrix = finalMatrixForCurrentKey.extractTranslationMatrix();
            TranslationMatrix nextTranslationMatrix = finalMatrixForNextKey.extractTranslationMatrix();
            Point3D ta = currentTranslationMatrix.getTranslation();
            Point3D tb = nextTranslationMatrix.getTranslation();
            Point3D tm = Point3D.lerp(ta, tb, ratio);
            boneTransformation.setTranslation(tm.x(), tm.y(), tm.z());

            Matrix4x4 finalMatrix = boneTransformation.getMatrix();

            if (bone != null) {

                for (int index : weights.getIndicesWeights().keySet()) {

                    float weight = weights.getIndicesWeights().get(index);

                    Vertex vertex = this.skinnedVertexBuffer.getVertex(index);
                    if (!vertexVisited.get(index)) {
                        vertex.setPosition(Point3D.zero());
                        vertexVisited.put(index, true);
                    }

                    Vertex originalVertex = this.vertexBuffer.getVertex(index);
                    Vector3D originalPositionAsVector = Point3D.sub(originalVertex.getPosition(), Point3D.zero());

                    Vector3D multipliedPosition = finalMatrix.mult(originalPositionAsVector);
                    Vector3D weightedPosition = multipliedPosition.multiplied(weight);

                    Vector3D multipliedCurrentPosition = finalMatrixForCurrentKey.mult(originalPositionAsVector);
                    Vector3D weightedCurrentPosition = multipliedCurrentPosition.multiplied(weight);

                    Vector3D multipliedNextPosition = finalMatrixForNextKey.mult(originalPositionAsVector);
                    Vector3D weightedNextPosition = multipliedNextPosition.multiplied(weight);

                    weightedPosition = Vector3D.add(weightedCurrentPosition, Vector3D.sub(weightedNextPosition, weightedCurrentPosition).multiplied(ratio));

                    vertex.getPosition().add(weightedPosition);
                }
            } else {
                log.info("Bone not found!");
            }
        }
        this.useSkinnedVersion = true;
    }

    @Override
    public VertexBuffer getVertexBuffer() {
        if (this.useSkinnedVersion) {
            return this.skinnedVertexBuffer;
        } else {
            return this.vertexBuffer;
        }
    }
}
