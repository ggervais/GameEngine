package com.ggervais.gameengine.render.opengl;

import com.ggervais.gameengine.render.shader.Program;
import com.ggervais.gameengine.render.shader.ProgramFactory;
import com.ggervais.gameengine.render.shader.Shader;

import javax.media.opengl.GL2;

public class GLProgramFactory implements ProgramFactory {

    private GL2 gl;

    public GLProgramFactory(GL2 gl) {
        this.gl = gl;
    }

    public GLProgramFactory() {
        this(null);
    }

    public void setGLContext(GL2 gl) {
        this.gl = gl;
    }

    public GL2 getGLContext() {
        return this.gl;
    }

    public Program buildProgram(Shader vertexShader, Shader fragmentShader) throws IllegalStateException, IllegalArgumentException {
        if (this.gl == null) {
            throw new IllegalStateException("OpenGL context must be initialized before building programs!");
        }
        if (!(vertexShader instanceof GLShader) || !(fragmentShader instanceof GLShader)) {
            throw new IllegalArgumentException("Both shaders must be of GLShader type!");
        }
        return new GLProgram(this.gl, (GLShader) vertexShader, (GLShader) fragmentShader);
    }
}
