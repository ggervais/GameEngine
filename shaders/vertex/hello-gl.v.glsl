#version 110

varying vec2 texCoords;

void main(void) {
    texCoords = gl_MultiTexCoord0.st;
    gl_FrontColor = gl_Color;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}