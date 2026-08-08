#version 460

in vec2 fragmentUv;
in vec3 fragmentPosition;
in vec3 fragmentNormal;

uniform sampler2D albedo;
uniform vec4 tint;

uniform vec3 cameraPosition;

uniform vec3 sunDirection;
uniform vec3 sunColor;
uniform float sunIntensity;

uniform vec3 ambientColor;
uniform float ambientIntensity;

out vec4 color;

void main() {
    vec3 albedoColor = texture(albedo, fragmentUv).rgb * tint.rgb;

    vec3 normal = normalize(fragmentNormal);
    vec3 lightDirection = normalize(-sunDirection);
    vec3 viewDirection = normalize(cameraPosition - fragmentPosition);
    vec3 halfDirection = normalize(lightDirection + viewDirection);

    float diffuse = max(dot(normal, lightDirection), 0.0);
    float specular = 0.0;

    if (diffuse > 0.0) {
        specular = pow(max(dot(normal, halfDirection), 0.0), 32.0);
    }

    vec3 ambient = albedoColor * ambientColor * ambientIntensity;
    vec3 diffuseLight = albedoColor * sunColor * sunIntensity * diffuse;
    vec3 specularLight = sunColor * sunIntensity * specular;

    vec3 lighting = ambient + diffuseLight + specularLight;

    color = vec4(lighting, tint.a);
}