package com.ggervais.gameengine.render.shader;

public abstract class Shader {
    protected ShaderType shaderType;
    protected String filename;
    protected int id;

    public abstract boolean compile();

    protected Shader(ShaderType shaderType, String filename) {
        this.shaderType = shaderType;
        this.filename = filename;
    }

    public int getId() {
        return this.id;
    }

    public ShaderType getShaderType() {
        return this.shaderType;
    }
}
