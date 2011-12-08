package com.ggervais.gameengine.geometry.skinning;

import com.ggervais.gameengine.math.Matrix4x4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinWeights {
    private Map<Integer, Float> indicesWeights;
    private String boneName;
    private Matrix4x4 skinOffsetMatrix;

    public SkinWeights() {
        this.indicesWeights = new HashMap<Integer, Float>();
        this.skinOffsetMatrix = new Matrix4x4();
    }

    public Map<Integer, Float> getIndicesWeights() {
        return indicesWeights;
    }

    public void setIndicesWeights(Map<Integer, Float> indicesWeights) {
        this.indicesWeights = indicesWeights;
    }

    public String getBoneName() {
        return boneName;
    }

    public void setBoneName(String boneName) {
        this.boneName = boneName;
    }

    public Matrix4x4 getSkinOffsetMatrix() {
        return skinOffsetMatrix;
    }

    public void setSkinOffsetMatrix(Matrix4x4 skinOffsetMatrix) {
        this.skinOffsetMatrix = skinOffsetMatrix;
    }
}
