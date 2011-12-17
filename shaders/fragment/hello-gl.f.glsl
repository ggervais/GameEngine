#version 110

uniform sampler2D texture;
uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

varying vec2 fragTexCoords;
varying vec3 fragNormal;
varying vec3 fragPosition;
varying vec4 fragSpecular;
varying float fragShininess;

//const vec3 lightDirection = vec3(0.408248, -0.816497, -0.408248);
const vec3 lightDirection = vec3(0.0, 0.0, -1);
const vec4 lightDiffuse = vec4(0.8, 0.8, 0.8, 0.0);
const vec4 lightAmbient = vec4(0.5, 0.5, 0.5, 1.0);
const vec4 lightSpecular = vec4(1.0, 1.0, 1.0, 1.0);

void main()
{

    vec3 modelViewLightDirection = (modelViewMatrix * vec4(lightDirection, 0)).xyz;
    vec3 normal = normalize(fragNormal);
    vec3 eye = normalize(fragPosition);
    vec3 reflection = reflect(modelViewLightDirection, normal);

    vec4 color = gl_Color;

    vec4 textureColor = texture2D(texture, fragTexCoords);

    vec4 fragDiffuse = textureColor * color;

    vec4 diffuseFactor = max(-dot(normal, modelViewLightDirection), 0.0) * lightDiffuse;
    vec4 ambientDiffuseFactor = diffuseFactor + lightAmbient;

    vec4 specularFactor = max(pow(-dot(reflection, eye), fragShininess), 0.0) * lightSpecular;

    gl_FragColor = specularFactor * fragSpecular + ambientDiffuseFactor * fragDiffuse;
    //gl_FragColor = fragDiffuse * color;
    //gl_FragColor = vec4(fragNormal.xyz, 1);
}