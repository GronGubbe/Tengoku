#version 330 core

uniform vec4 uBaseColorFactor;

out vec4 fragmentColor;

void main() {
    fragmentColor = uBaseColorFactor;
}