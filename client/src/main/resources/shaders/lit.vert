#version 460

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 uv;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat3 normalMatrix;

out vec3 fragmentPosition;
out vec3 fragmentNormal;
out vec2 fragmentUv;

void main() {
    vec4 worldPosition = model * vec4(position, 1.0);

    fragmentPosition = worldPosition.xyz;
    fragmentNormal = normalize(normalMatrix * normal);
    fragmentUv = uv;

    gl_Position = projection * view * worldPosition;
}