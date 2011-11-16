package com.ggervais.gameengine.geometry.skinning;

import com.ggervais.gameengine.math.Matrix4x4;

public class AnimationKey {
    private long timestamp;
    Matrix4x4 transformMatrix;

    public AnimationKey() {

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Matrix4x4 getTransformMatrix() {
        return transformMatrix;
    }

    public void setTransformMatrix(Matrix4x4 transformMatrix) {
        this.transformMatrix = transformMatrix;
    }
}
