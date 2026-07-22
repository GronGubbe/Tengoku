#version 460

in vec2 fragmentUv;

uniform sampler2D albedo;

out vec4 color;

void main() {
    color = texture(albedo, fragmentUv);
}