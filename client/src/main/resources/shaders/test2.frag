#version 460

in vec2 fragmentUv;

uniform sampler2D albedo;
uniform vec4 tint;

out vec4 color;

void main() {
    vec4 tex = texture(albedo, fragmentUv) * tint;

    const float scale = 10.0;

    vec2 cell = floor(fragmentUv * scale);

    float checker = mod(cell.x + cell.y, 2.0);

    vec3 result = mix(tex.rgb, tex.rgb * 0.2, checker);

    color = vec4(result, tex.a);
}