package com.ggervais.gameengine.render.opengl;

import com.ggervais.gameengine.render.shader.Shader;
import com.ggervais.gameengine.render.shader.ShaderFactory;
import com.ggervais.gameengine.render.shader.ShaderType;

import javax.media.opengl.GL2;

public class GLShaderFactory implements ShaderFactory {
    
    private GL2 gl;
    
    public GLShaderFactory(GL2 gl) {
        this.gl = gl;
    }

    public GLShaderFactory() {
        this(null);
    }
    
    public void setGLContext(GL2 gl) {
        this.gl = gl;
    }
    
    public GL2 getGLContext() {
        return this.gl;
    }

    public Shader buildVertexShader(String filename) throws IllegalStateException {

        if (this.gl == null) {
            throw new IllegalStateException("OpenGL context must be initialized before building shaders!");
        }

        return new GLShader(this.gl, ShaderType.VERTEX_SHADER, filename);
    }

    public Shader buildFragmentShader(String filename) {

        if (this.gl == null) {
            throw new IllegalStateException("OpenGL context must be initialized before building shaders!");
        }

        return new GLShader(this.gl, ShaderType.FRAGMENT_SHADER, filename);
    }
}
