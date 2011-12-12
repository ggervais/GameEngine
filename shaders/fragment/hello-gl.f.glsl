#version 110

uniform sampler2D texture;

varying vec2 texCoords;

void main()
{
    vec4 textureColor = texture2D(texture, texCoords);
    vec4 color = gl_Color;

    gl_FragColor = textureColor * color;
}