#version 110

uniform sampler2D texture;

varying vec2 fragTexCoords;

void main()
{
    vec4 color = gl_Color;

    vec4 textureColor = texture2D(texture, fragTexCoords);

    gl_FragColor = textureColor * color;
}