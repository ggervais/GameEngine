#version 110

attribute vec4 position;
attribute vec4 color;
varying vec2 texCoords;

void main(void) {
    texCoords = gl_MultiTexCoord0.st;
    gl_FrontColor = color;
    gl_Position = gl_ModelViewProjectionMatrix * position;
}