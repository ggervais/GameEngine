#version 110
 
uniform sampler2D fbo_texture;
varying vec2 f_texcoord;

void main(void) {
    vec2 texcoord = f_texcoord;
    //texcoord.x += sin(texcoord.y * 4.0*2.0*3.14159 + 10.0) / 100.0;
    gl_FragColor = texture2D(fbo_texture, texcoord);
}