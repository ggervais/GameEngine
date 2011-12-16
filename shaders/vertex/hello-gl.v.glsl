#version 110

attribute vec4 position;
attribute vec4 color;
attribute vec2 texCoords;
varying vec2 fragTexCoords;

void main(void) {
    fragTexCoords = texCoords;
    gl_FrontColor = color;
    gl_Position = gl_ModelViewProjectionMatrix * position;
}