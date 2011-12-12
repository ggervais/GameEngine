package com.ggervais.gameengine.render.opengl;

import com.ggervais.gameengine.render.shader.Shader;
import com.ggervais.gameengine.render.shader.ShaderType;
import com.ggervais.gameengine.utils.FileUtils;
import org.apache.log4j.Logger;

import javax.media.opengl.GL2;

public class GLShader extends Shader {
    private GL2 gl;

    private static final Logger log = Logger.getLogger(GLShader.class);

    public GLShader(GL2 gl, ShaderType shaderType, String filename) {
        super(shaderType, filename);
        this.gl = gl;
        this.shaderType = shaderType;
    }

    public void setGLContext(GL2 gl) {
        this.gl = gl;
    }

    private int getGLVertexType() {
        int glVertexType = GL2.GL_VERTEX_SHADER;
        switch (this.shaderType) {
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

        String shaderSource = "";
        try {
            shaderSource = FileUtils.getFileContents(filename);
        } catch (Exception e) {
            log.error("An exception occurred while reading shader source: " + e.getMessage());
            return false;
        }
        String[] sources = new String[1];
        sources[0] = shaderSource;

        int[] lengths = new int[1];
        lengths[0] = sources[0].length();

        int glVertexType = getGLVertexType();

        this.id = gl.glCreateShader(glVertexType);
        gl.glShaderSource(this.id, sources.length, sources, lengths, 0);

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
