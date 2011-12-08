package com.ggervais.gameengine.render.shader;

public abstract class Shader {
    protected VertexType vertexType;
    protected String filename;
    protected int id;

    public abstract boolean compile();

    protected Shader(VertexType vertexType, String filename) {
        this.vertexType = vertexType;
        this.filename = filename;
    }

    public int getId() {
        return this.id;
    }

    public VertexType getVertexType() {
        return this.vertexType;
    }
}
