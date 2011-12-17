#version 110

attribute vec4 position;
attribute vec4 color;
attribute vec3 normal;
attribute vec2 texCoords;
varying vec2 fragTexCoords;
varying vec3 fragNormal;

void main(void) {
    fragNormal = normal;
    fragTexCoords = texCoords;
    gl_FrontColor = color;
    gl_Position = gl_ModelViewProjectionMatrix * position;
}