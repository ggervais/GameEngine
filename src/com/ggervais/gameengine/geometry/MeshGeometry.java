package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.geometry.primitives.VertexBuffer;
import com.ggervais.gameengine.geometry.skinning.AnimationSet;
import com.ggervais.gameengine.geometry.skinning.Bone;
import com.ggervais.gameengine.geometry.skinning.SkinWeights;
import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;
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

    public MeshGeometry() {
        super();
        this.useSkinnedVersion = false;
        this.animationSets = new ArrayList<AnimationSet>();
        this.lastUpdate = 0;
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

        if (this.lastUpdate == 0) {
            this.lastUpdate = currentTime;
        }

        if (this.animationSets.size() > 0) {
            if (currentTime - this.lastUpdate >= DELAY) {
                this.boneHierarchyRoot.incrementCurrentAnimationKey();
                this.lastUpdate = currentTime;
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

                    Vector3D multipliedCurrentPosition = finalMatrixForCurrentKey.mult(originalPositionAsVector);
                    Vector3D weightedCurrentPosition = multipliedCurrentPosition.multiplied(weight);

                    Vector3D multipliedNextPosition = finalMatrixForNextKey.mult(originalPositionAsVector);
                    Vector3D weightedNextPosition = multipliedNextPosition.multiplied(weight);

                    Vector3D weightedPosition = Vector3D.add(weightedCurrentPosition, Vector3D.sub(weightedNextPosition, weightedCurrentPosition).multiplied(ratio));

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
