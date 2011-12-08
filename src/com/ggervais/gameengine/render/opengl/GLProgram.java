package com.ggervais.gameengine.render.opengl;

import com.ggervais.gameengine.render.shader.Program;
import com.jogamp.common.nio.Buffers;
import org.apache.log4j.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class GLProgram extends Program {

    private GL2 gl;

    private static final Logger log = Logger.getLogger(GLProgram.class);

    public GLProgram(GL2 gl, GLShader vertexShader, GLShader fragmentShader) {
        super(vertexShader, fragmentShader);
        this.gl = gl;
    }

    private String getInfoLog() {
        int[] infoLogCountArray = new int[1];
        gl.glGetProgramiv(this.id, GL2.GL_INFO_LOG_LENGTH, infoLogCountArray, 0);

        int infoLogCount = infoLogCountArray[0];
        byte[] infoLogArray = new byte[infoLogCount];

        gl.glGetProgramInfoLog(this.id, infoLogCount, null, 0, infoLogArray, 0);

        return new String(infoLogArray);
    }

    private boolean getLinkSuccess() {
        int linkSuccess = 0;
        int[] linkSuccessArray = new int[1];
        gl.glGetProgramiv(this.id, GL2.GL_LINK_STATUS,  linkSuccessArray, 0);
        linkSuccess =  linkSuccessArray[0];
        return (linkSuccess != 0);
    }

    @Override
    public boolean linkShaders() {

        boolean success = true;

        this.id = gl.glCreateProgram();
        gl.glAttachShader(this.id, this.vertexShader.getId());
        gl.glAttachShader(this.id, this.fragmentShader.getId());
        gl.glLinkProgram(this.id);

        success = getLinkSuccess();

        if (!success) {
            log.error("Link failed. Please see console for messages.");

            String infoLog = getInfoLog();
            System.out.println(infoLog);

            gl.glDeleteProgram(this.id);
        }

        return success;
    }
}
