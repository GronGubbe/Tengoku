#version 330 core

in vec2 vPos;
out vec4 fragColor;

void main() {
    vec3 color = vec3(vPos * 0.5 + 0.5, 0.0);
    fragColor = vec4(color, 1.0);
}