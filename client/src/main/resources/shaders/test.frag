#version 460

in vec2 fragmentUv;

uniform sampler2D albedo;
uniform vec4 tint;

out vec4 color;

void main() {
    color = texture(albedo, fragmentUv) * tint;
}