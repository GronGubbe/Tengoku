#version 460

layout(location = 0) in vec3 position;

uniform mat4 model;
uniform mat4 lightView;
uniform mat4 lightProjection;

void main() {
    gl_Position = lightProjection * lightView * model * vec4(position, 1.0);
}
