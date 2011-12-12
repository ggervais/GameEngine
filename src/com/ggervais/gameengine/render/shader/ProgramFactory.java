package com.ggervais.gameengine.render.shader;

public interface ProgramFactory {
    public Program buildProgram(Shader vertexShader, Shader fragmentShader) throws IllegalStateException, IllegalArgumentException;
}
