package com.ggervais.gameengine.geometry;           

import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.geometry.primitives.VertexBuffer;
import com.ggervais.gameengine.geometry.skinning.*;
import com.ggervais.gameengine.math.*;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;
import org.apache.log4j.Logger;

import java.util.*;

// This class represents a base geometry (a mesh).
public class SkinnedMeshGeometry extends Geometry {

    private VertexBuffer skinnedVertexBuffer;
    private boolean useSkinnedVersion;
    private List<AnimationSet> animationSets;
    private Map<AnimationSet, List<BoundingBox>> boundingBoxes;
    private Map<AnimationSet, List<List<Vector3D>>> vertexNormals;
    private static final Logger log = Logger.getLogger(SkinnedMeshGeometry.class);

    public SkinnedMeshGeometry() {
        super();
        this.useSkinnedVersion = false;
        this.animationSets = new ArrayList<AnimationSet>();
        this.boundingBoxes = new HashMap<AnimationSet, List<BoundingBox>>();
        this.vertexNormals = new HashMap<AnimationSet, List<List<Vector3D>>>();
        setStreamed(true);
        setRecomputeBoundingBoxWhenGeometryIsDirty(false);
    }

    @Override
    protected void generateTextureCoords(Effect effect) {
        // Do nothing, as we don't know the actual geometry.
    }

    public void addAnimationSet(AnimationSet animationSet) {
        this.animationSets.add(animationSet);
    }

    public void initializeSkinningIfNecessary() {
        if (this.skinnedVertexBuffer == null) {
            this.skinnedVertexBuffer = this.vertexBuffer.copy();
            this.useSkinnedVersion = true;
        }
    }

    public VertexBuffer getOriginalVertexBuffer() {
        return this.vertexBuffer;
    }

    public VertexBuffer getSkinnedVertexBuffer() {
        return this.skinnedVertexBuffer;
    }
    
    @Override
    public VertexBuffer getVertexBuffer() {
        if (this.useSkinnedVersion) {
            return this.skinnedVertexBuffer;
        } else {
            return this.vertexBuffer;
        }
    }
    
    public List<AnimationKey> getAnimationKeysForBoneAndAnimationSet(Bone bone, int animationSetIndex) {
        List<AnimationKey> list = new ArrayList<AnimationKey>();
        if (animationSetIndex >= 0 && animationSetIndex < this.animationSets.size()) {
            AnimationSet animationSet = this.animationSets.get(animationSetIndex);
            for (int i = 0; i < animationSet.getNbAnimations(); i++) {
                Animation animation = animationSet.getAnimation(i);
                if (animation.getBoneName().equals(bone.getName())) {
                    for (int j = 0; j < animation.getNbAnimationKeys(); j++) {
                        list.add(animation.getAnimationKey(j));
                    }
                    break;
                }
            }
        }
        return list;
    }
    
    public int nbAnimationSets() {
        return this.animationSets.size();
    }
    
    public AnimationSet getAnimationSet(int index) {
        AnimationSet set = null;
        
        if (index >= 0 && index < this.animationSets.size()) {
            set = this.animationSets.get(index);
        }
        
        return set;
    }
    
    public void computeBoundingBoxes() {

        // Compute bounding boxes for each animation set.
        for (AnimationSet animationSet : this.animationSets) {

            // Find maximum number of keys throughout the bone animations.
            // Normally, it should be the same for all animations.
            int maxNumberOfKeys = 0;
            for (int i = 0; i < animationSet.getNbAnimations(); i++) {
                maxNumberOfKeys = Math.max(animationSet.getAnimation(i).getNbAnimationKeys(), maxNumberOfKeys);
            }

            this.boundingBoxes.put(animationSet, new ArrayList<BoundingBox>());
            this.vertexNormals.put(animationSet, new ArrayList<List<Vector3D>>());
            
            // Step through each key and compute a bounding box for each.
            // This assumes that keys are shared throughout the animation set.
            for (int i = 0; i < maxNumberOfKeys; i++) {
                this.boneHierarchyRoot.updateMatrices(animationSet, maxNumberOfKeys, i);

                Point3D[] intermediatePositions = new Point3D[this.vertexBuffer.getRealSize()];

                for (SkinWeights weights : this.skinWeightsList) {

                    String boneName = weights.getBoneName();
                    Bone bone = this.boneHierarchyRoot.findByName(boneName);

                    if (bone != null) {
                        Matrix4x4 matrix = bone.getFinalMatrix();
                        Map<Integer, Float> indicesWeights = weights.getIndicesWeights();

                        for (int index : weights.getIndicesWeights().keySet()) {
                            float weight = indicesWeights.get(index);
                            if (intermediatePositions[index] == null) {
                                intermediatePositions[index] = Point3D.zero();
                            }
                            Point3D originalPosition = this.vertexBuffer.getVertex(index).getPosition();
                            Vector3D v = Point3D.sub(originalPosition, Point3D.zero());

                            if (originalPosition != null) {
                                intermediatePositions[index].add(matrix.mult(v).multiplied(weight));
                            }
                        }
                    }
                }

                // Fill in the blanks.
                for (int v = 0; v < this.vertexBuffer.getRealSize(); v++) {
                    Point3D intermediatePosition = intermediatePositions[v];
                    Vertex vertex = this.vertexBuffer.getVertex(v);
                    if (vertex != null) {
                        if (intermediatePosition == null) {
                            Vertex originalVertex = this.vertexBuffer.getVertex(v);
                            if (originalVertex != null) {
                                intermediatePositions[v] = originalVertex.getPosition();
                            }
                        }
                    }
                }

                BoundingBox boundingBoxForKey = Geometry.computeBoundingBox(Arrays.asList(intermediatePositions));
                this.boundingBoxes.get(animationSet).add(boundingBoxForKey);

                List<Vector3D> computedNormals = Geometry.computeVertexNormals(Arrays.asList(intermediatePositions), getIndexBuffer());
                this.vertexNormals.get(animationSet).add(computedNormals);
                
                log.info(this.vertexNormals.get(animationSet).size() + " normal lists in animation set " + animationSet.getName());
            }
        }
    }

    public Map<AnimationSet, List<BoundingBox>> getBoundingBoxes() {
        return boundingBoxes;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.modelBoundingBox = boundingBox;
    }
    
    public List<List<Vector3D>> getVertexNormals(AnimationSet animationSet) {
        return this.vertexNormals.get(animationSet);
    }

    @Override
    protected void computeBoundingBox(Matrix4x4 transform) {
        if (this.boundingBoxes.size() == 0) {
            super.computeBoundingBox(transform);
        }
    }

    @Override
    protected void computeVertexNormals() throws UnsupportedOperationException {
        if (this.vertexNormals.size() == 0) {
            super.computeVertexNormals();
        }
    }
}
