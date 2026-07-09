#version 330 core

uniform vec3 uTint;
uniform float uOpacity;

out vec4 fragColor;

void main() {
    fragColor = vec4(uTint, uOpacity);
}