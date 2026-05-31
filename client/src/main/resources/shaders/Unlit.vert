#version 330 core

layout(location = 0) in vec3 aPosition;
out vec2 vPos;

void main() {
    vPos = aPosition.xy;
    gl_Position = vec4(aPosition, 1.0);
}