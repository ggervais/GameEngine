#version 110
 
uniform sampler2D fbo_texture;
varying vec2 f_texcoord;

void main(void) {
    vec2 texcoord = f_texcoord;
    gl_FragColor = texture2D(fbo_texture, texcoord);
}