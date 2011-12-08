package com.ggervais.gameengine.render.opengl;

import com.ggervais.gameengine.render.shader.Shader;
import com.ggervais.gameengine.render.shader.VertexType;
import org.apache.log4j.Logger;

import javax.media.opengl.GL2;

public class GLShader extends Shader {
    private GL2 gl;

    private static final Logger log = Logger.getLogger(GLShader.class);

    public GLShader(GL2 gl, VertexType vertexType, String filename) {
        super(vertexType, filename);
        this.gl = gl;
        this.vertexType = vertexType;
    }

    public void setGLContext(GL2 gl) {
        this.gl = gl;
    }

    private int getGLVertexType() {
        int glVertexType = GL2.GL_VERTEX_SHADER;
        switch (this.vertexType) {
            case VERTEX_SHADER:
                glVertexType = GL2.GL_VERTEX_SHADER;
                break;
            case FRAGMENT_SHADER:
                glVertexType = GL2.GL_FRAGMENT_SHADER;
                break;
        }
        return glVertexType;
    }

    private String getInfoLog() {
        int[] infoLogCountArray = new int[1];
        gl.glGetShaderiv(this.id, GL2.GL_INFO_LOG_LENGTH, infoLogCountArray, 0);

        int infoLogCount = infoLogCountArray[0];
        byte[] infoLogArray = new byte[infoLogCount];

        gl.glGetShaderInfoLog(this.id, infoLogCount, null, 0, infoLogArray, 0);

        return new String(infoLogArray);
    }

    private boolean getCompilationSuccess() {
        int compileSuccess = 0;
        int[] compileSuccessArray = new int[1];
        gl.glGetShaderiv(this.id, GL2.GL_COMPILE_STATUS, compileSuccessArray, 0);
        compileSuccess = compileSuccessArray[0];
        return (compileSuccess != 0);
    }

    public boolean compile() {
        boolean success = true;

        String[] filenames = new String[1];
        filenames[0] = this.filename;

        int[] lengths = new int[1];
        lengths[0] = filenames[0].length();

        int glVertexType = getGLVertexType();

        this.id = gl.glCreateShader(glVertexType);
        gl.glShaderSource(this.id, filenames.length, filenames, lengths, 0);

        gl.glCompileShader(this.id);

        success = getCompilationSuccess();

        if (!success) {
            log.error("Compilation failed. Please see console for messages.");

            String infoLog = getInfoLog();
            System.out.println(infoLog);

            gl.glDeleteShader(this.id);
        }

        return success;
    }
}
