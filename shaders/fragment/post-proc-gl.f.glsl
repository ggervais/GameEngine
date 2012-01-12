#version 110
 
uniform sampler2D fbo_texture;
varying vec2 f_texcoord;

const float MIN_LIMIT = 0.495;
const float MAX_LIMIT = 0.505;

void main(void) {
    vec2 texcoord = f_texcoord;
    vec4 color = vec4(1, 0, 0, 1);

    if (texcoord.x < MIN_LIMIT || texcoord.x > MAX_LIMIT) {
        if (texcoord.x > MAX_LIMIT) {
            texcoord.x += sin(texcoord.y * 4.0*2.0*3.14159 + 10.0) / 100.0;
            texcoord.y += sin(texcoord.x * 4.0*2.0*3.14159 + 10.0) / 100.0;
        }
        color = texture2D(fbo_texture, texcoord);
    }
    gl_FragColor = color;
}