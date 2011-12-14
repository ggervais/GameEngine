package com.ggervais.gameengine.geometry.loader;

import java.util.HashMap;
import java.util.Map;

public class VertexDuplicationData {
    private int originalVertexIndex;
    private Map<Integer, Integer> vertexIndexReplacementsFromTextureIndex;

    public VertexDuplicationData() {
        this.vertexIndexReplacementsFromTextureIndex = new HashMap<Integer, Integer>();
    }

    public int getOriginalVertexIndex() {
        return originalVertexIndex;
    }

    public void setOriginalVertexIndex(int originalVertexIndex) {
        this.originalVertexIndex = originalVertexIndex;
    }

    public Map<Integer, Integer> getVertexIndexReplacementsFromTextureIndex() {
        return vertexIndexReplacementsFromTextureIndex;
    }

    public void putReplacement(int textureIndex, int vertexIndex) {
        this.vertexIndexReplacementsFromTextureIndex.put(textureIndex, vertexIndex);
    }
    
    public int getReplacement(int textureIndex) {
        return this.vertexIndexReplacementsFromTextureIndex.get(textureIndex);
    }
}
