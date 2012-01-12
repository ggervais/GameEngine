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

            // Apply special effect.
            // Here we are doing some sort of heat vision, based on the code found here:
            // http://www.geeks3d.com/20101123/shader-library-predators-thermal-vision-post-processing-filter-glsl/
            // Page viewed on January 12th, 2012.

            vec4 sampled_color = texture2D(fbo_texture, texcoord);
            vec4 colors[3];
            colors[0] = vec4(0, 0, 1, 1); // Blue
            colors[1] = vec4(1, 1, 0, 1); // Yellow
            colors[2] = vec4(1, 0 ,0, 1); // Red
            float luminance = (sampled_color.r + sampled_color.g + sampled_color.b) / 3.0;
            int index = (luminance < 0.5 ? 0 : 1);
            color = mix(colors[index], colors[index + 1], (luminance - float(index) * 0.5) / 0.5);
            //texcoord.x += sin(texcoord.y * 4.0*2.0*3.14159 + 10.0) / 100.0;
            //texcoord.y += sin(texcoord.x * 4.0*2.0*3.14159 + 10.0) / 100.0;
        } else {
            color = texture2D(fbo_texture, texcoord);
        }
    }
    gl_FragColor = color;
}