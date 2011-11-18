package com.ggervais.gameengine.geometry.skinning;

import com.ggervais.gameengine.math.Matrix4x4;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Bone {

    private static final Logger log = Logger.getLogger(Bone.class);

    private String name;
    private Matrix4x4 transformMatrix;
    private Matrix4x4 combinedMatrix;
    private Matrix4x4 nextCombinedMatrix;
    private Matrix4x4 skinOffsetMatrix;
    private List<Integer> vertexIndices;
    private List<Float> weights;
    private List<Bone> children;
    private Bone parent;
    private int currentAnimationKey;
    private int nextAnimationKey;
    private Animation currentAnimation;

    public Bone() {
        this.children = new ArrayList<Bone>();
        this.vertexIndices = new ArrayList<Integer>();
        this.weights = new ArrayList<Float>();
        this.transformMatrix = Matrix4x4.createIdentity();
        this.combinedMatrix = Matrix4x4.createIdentity();
        this.nextCombinedMatrix = Matrix4x4.createIdentity();
        this.skinOffsetMatrix = Matrix4x4.createIdentity();
        this.currentAnimationKey = 0;
        this.nextAnimationKey = 1;
    }

    public Bone(String name) {
        this.name = name;
        this.children = new ArrayList<Bone>();
        this.vertexIndices = new ArrayList<Integer>();
        this.weights = new ArrayList<Float>();
        this.transformMatrix = Matrix4x4.createIdentity();
        this.combinedMatrix = Matrix4x4.createIdentity();
        this.nextCombinedMatrix = Matrix4x4.createIdentity();
        this.skinOffsetMatrix = Matrix4x4.createIdentity();
        this.currentAnimationKey = 0;
        this.nextAnimationKey = 1;
    }

    public Bone(String name, Matrix4x4 transformMatrix, Matrix4x4 skinOffsetMatrix) {
        this.name = name;
        setTransformMatrix(transformMatrix);
        setSkinOffsetMatrix(skinOffsetMatrix);
        this.children = new ArrayList<Bone>();
        this.vertexIndices = new ArrayList<Integer>();
        this.weights = new ArrayList<Float>();
        this.combinedMatrix = Matrix4x4.createIdentity();
        this.nextCombinedMatrix = Matrix4x4.createIdentity();
        this.currentAnimationKey = 0;
        this.nextAnimationKey = 1;
    }

    public void setCurrentAnimationSet(AnimationSet animationSet) {
        this.currentAnimationKey = 0;
        if (animationSet != null) {
            for (int i = 0; i < animationSet.getNbAnimations(); i++) {
                Animation animation = animationSet.getAnimation(i);
                if (animation.getBoneName() != null && animation.getBoneName().length() > 0 && animation.getBoneName().equals(this.name)) {
                    this.currentAnimation = animation;
                    break;
                }
            }
            this.nextAnimationKey = getNextAnimationKey();
            log.info(currentAnimationKey + " " + nextAnimationKey);
        }
        for (Bone child : this.children) {
            child.setCurrentAnimationSet(animationSet);
        }
    }

    private int getNextAnimationKey() {
        int key = this.currentAnimationKey;
        if (this.currentAnimation != null) {
            if (this.currentAnimationKey < this.currentAnimation.getNbAnimationKeys() - 1) {
                key++;
            } else {
                key = 0;
            }
        }
        return key;
    }

    private int getPreviousAnimationKey() {
        int key = 0;
        if (this.currentAnimation != null) {
            if (this.currentAnimationKey > 0) {
                key--;
            } else {
                key = this.currentAnimation.getNbAnimationKeys() - 1;
            }
        }
        return key;
    }

    public void incrementCurrentAnimationKey() {
        if (this.currentAnimation != null) {
            this.currentAnimationKey = getNextAnimationKey();
            this.nextAnimationKey = getNextAnimationKey();
        }

        for (Bone child : this.children) {
            child.incrementCurrentAnimationKey();
        }
    }

    /*public void decrementCurrentAnimationKey() {
        if (this.currentAnimation != null) {
            if (this.currentAnimationKey > 0) {
                this.currentAnimationKey--;
            } else {
                this.currentAnimationKey = this.currentAnimation.getNbAnimationKeys() - 1;
            }
        }

        for (Bone child : this.children) {
            child.decrementCurrentAnimationKey();
        }
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Matrix4x4 getTransformMatrix() {
        return transformMatrix;
    }

    public void setTransformMatrix(Matrix4x4 transformMatrix) {

        if (transformMatrix != null) {
            this.transformMatrix = transformMatrix;
        } else {
            this.transformMatrix = Matrix4x4.createIdentity();
        }
    }

    public Matrix4x4 getSkinOffsetMatrix() {
        return skinOffsetMatrix;
    }

    public void setSkinOffsetMatrix(Matrix4x4 skinOffsetMatrix) {

        if (skinOffsetMatrix != null) {
            this.skinOffsetMatrix = skinOffsetMatrix;
        } else {
            this.skinOffsetMatrix = Matrix4x4.createIdentity();
        }
    }

    public List<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public void setVertexIndices(List<Integer> vertexIndices) {
        this.vertexIndices = vertexIndices;
    }

    public List<Float> getWeights() {
        return weights;
    }

    public void setWeights(List<Float> weights) {
        this.weights = weights;
    }

    public void addChild(Bone bone) {
        this.children.add(bone);
        bone.setParent(this);
    }

    public List<Bone> getChildren() {
        return children;
    }

    public void setChildren(List<Bone> children) {
        this.children = children;
    }

    public void addVertexIndex(int index) {
        this.vertexIndices.add(index);
    }

    public void addWeight(float weight) {
        this.weights.add(weight);
    }

    public Bone getParent() {
        return parent;
    }

    public void setParent(Bone parent) {
        this.parent = parent;
    }

    public String toString() {
        StringBuilder logString = new StringBuilder();
        logString.append(String.format("Bone '%s', with %d %s. ", (this.name != null ? this.name : "<null>"), this.children.size(), (this.children.size() > 1 ? "children" : "child")));
        logString.append(String.format("Nb. indices: %d. Nb. weights: %d. Skin offset matrix: \n%s", this.vertexIndices.size(), this.weights.size(), this.skinOffsetMatrix));


        return logString.toString();
    }

    public void logTree() {
        logTree(0);
    }

    private void logTree(int level) {
        StringBuilder pounds = new StringBuilder();
        for (int i = 0; i < level + 1 ;i++) {
            pounds.append("#");
        }
        if (pounds.length() > 0) {
            pounds.append(" ");
        }

        log.info(pounds + this.toString());

        for (Bone child : this.children) {
            child.logTree(level + 1);
        }

    }

    public Bone findByName(String name) {
        Bone foundBone = null;
        if (name != null && name.length() > 0 && name.equals(this.name)) {
            foundBone = this;
        } else {
            for (Bone child: this.children) {
                foundBone = child.findByName(name);
                if (foundBone != null) {
                    break;
                }
            }
        }
        return foundBone;
    }

    public Matrix4x4 getCombinedMatrix() {
        return this.combinedMatrix;
    }

    public void updateMatrices() {

        Matrix4x4 currentTransformMatrixToUse = this.transformMatrix;
        Matrix4x4 nextTransformMatrixToUse = this.transformMatrix;

        if (this.currentAnimation != null) {
            AnimationKey animationKey = this.currentAnimation.getAnimationKey(this.currentAnimationKey);
            if (animationKey != null) {
                currentTransformMatrixToUse = animationKey.getTransformMatrix();
            }

            animationKey = this.currentAnimation.getAnimationKey(this.nextAnimationKey);
            if (animationKey != null) {
                nextTransformMatrixToUse = animationKey.getTransformMatrix();
            }
        }

        Matrix4x4 currentParentMatrix = Matrix4x4.createIdentity();
        if (this.parent != null) {
            currentParentMatrix = this.parent.getCombinedMatrix();
        }
        this.combinedMatrix = Matrix4x4.mult(currentParentMatrix, currentTransformMatrixToUse);


        Matrix4x4 nextParentMatrix = Matrix4x4.createIdentity();
        if (this.parent != null) {
            nextParentMatrix = this.parent.getNextCombinedMatrix();
        }
        this.nextCombinedMatrix = Matrix4x4.mult(nextParentMatrix, nextTransformMatrixToUse);

        for (Bone child : this.children) {
            child.updateMatrices();
        }
    }

    public Matrix4x4 getNextCombinedMatrix() {
        return this.nextCombinedMatrix;
    }

    public Matrix4x4 getFinalMatrix() {
        return Matrix4x4.mult(this.combinedMatrix, this.skinOffsetMatrix);
    }

    public Matrix4x4 getNextFinalMatrix() {
        return Matrix4x4.mult(this.nextCombinedMatrix, this.skinOffsetMatrix);
    }
}
