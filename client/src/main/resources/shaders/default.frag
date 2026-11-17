#version 330 core

out vec4 fragmentColor;

in vec3 vColor;

void main() {
    fragmentColor = vec4(vColor, 1.0);
}