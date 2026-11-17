#version 330 core

layout (location = 0) in vec3 aPosition;

uniform mat4 uModel;

out vec3 vColor;

void main() {
    vec2 screenPosition = aPosition.xy + 0.5;

    vColor = vec3(screenPosition, 0.0);

    gl_Position = uModel * vec4(aPosition, 1.0);
}