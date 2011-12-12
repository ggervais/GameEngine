package com.ggervais.gameengine.render.shader;

public abstract class Program {
    protected Shader vertexShader;
    protected Shader fragmentShader;
    protected int id;

    public Program(Shader vertexShader, Shader fragmentShader) {
        setVertexShader(vertexShader);
        setFragmentShader(fragmentShader);
    }

    public Shader getVertexShader() {
        return vertexShader;
    }

    public void setVertexShader(Shader vertexShader) {
        if (vertexShader == null) {
            throw new IllegalArgumentException("Shader object is null!");
        }

        if (vertexShader.getShaderType() != ShaderType.VERTEX_SHADER) {
            throw new IllegalArgumentException("Shader must be a vertex shader. Type given: " + vertexShader.getShaderType());
        }

        this.vertexShader = vertexShader;
    }

    public Shader getFragmentShader() {
        return fragmentShader;
    }

    public void setFragmentShader(Shader fragmentShader) {

        if (fragmentShader == null) {
            throw new IllegalArgumentException("Shader object is null!");
        }

        if (fragmentShader.getShaderType() != ShaderType.FRAGMENT_SHADER) {
            throw new IllegalArgumentException("Shader must be a fragment shader. Type given: " + fragmentShader.getShaderType());
        }

        this.fragmentShader = fragmentShader;
    }

    public int getId() {
        return this.id;
    }

    public abstract boolean linkShaders();
}
