package com.ggervais.gameengine.render.shader;

public interface ShaderFactory {
    public Shader buildVertexShader(String filename) throws IllegalStateException;
    public Shader buildFragmentShader(String filename) throws IllegalStateException;
}
