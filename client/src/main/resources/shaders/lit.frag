#version 460

in vec3 fragmentPosition;
in vec3 fragmentNormal;
in vec2 fragmentUv;

uniform sampler2D albedo;
uniform vec4 tint;

uniform vec3 cameraPosition;

uniform vec3 sunDirection;
uniform vec3 sunColor;
uniform float sunIntensity;

uniform vec3 ambientColor;
uniform float ambientIntensity;

uniform sampler2DShadow shadowMap;
uniform mat4 shadowView;
uniform mat4 shadowProjection;

const int MAX_POINT_LIGHTS = 16;
const float SHADOW_BIAS = 0.0015;

uniform int pointLightCount;
uniform vec3 pointLightPositions[MAX_POINT_LIGHTS];
uniform vec3 pointLightColors[MAX_POINT_LIGHTS];
uniform float pointLightRanges[MAX_POINT_LIGHTS];

out vec4 color;

void main() {
    vec3 albedoColor = texture(albedo, fragmentUv).rgb * tint.rgb;

    vec3 viewDirection = normalize(cameraPosition - fragmentPosition);

    vec3 normal = normalize(fragmentNormal);

    vec3 lighting = ambientColor * ambientIntensity;

    vec3 lightDirection = normalize(-sunDirection);

    float diffuse = max(dot(normal, lightDirection), 0.0);

    vec3 halfDirection = normalize(lightDirection + viewDirection);
    float specular = pow(max(dot(normal, halfDirection), 0.0), 32.0);

    float shadow = 1.0;

    vec4 shadowPosition = shadowProjection * shadowView * vec4(fragmentPosition, 1.0);

    shadowPosition.xyz /= shadowPosition.w;

    vec3 shadowCoordinates = shadowPosition.xyz * 0.5 + 0.5;

    shadowCoordinates.z -= SHADOW_BIAS;

    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);

    shadow = 0.0;

    for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
            vec2 offset = vec2(x, y) * texelSize;

            shadow += texture(
                    shadowMap,
                    vec3(shadowCoordinates.xy + offset, shadowCoordinates.z)
            );
        }
    }

    shadow /= 9.0;

    lighting += sunColor * sunIntensity * (diffuse + specular) * shadow;

    for (int i = 0; i < pointLightCount; i++) {
        vec3 toPointLight = pointLightPositions[i] - fragmentPosition;
        float distanceToPointLight = length(toPointLight);

        if (distanceToPointLight >= pointLightRanges[i]) {
            continue;
        }

        vec3 pointDirection = toPointLight / distanceToPointLight;

        float attenuation = 1.0 - distanceToPointLight / pointLightRanges[i];
        attenuation *= attenuation;

        float pointDiffuse = max(dot(normal, pointDirection), 0.0);

        vec3 pointHalfDirection = normalize(pointDirection + viewDirection);
        float pointSpecular = pow(max(dot(normal, pointHalfDirection), 0.0), 32.0);

        lighting += pointLightColors[i] * attenuation * (pointDiffuse + pointSpecular);
    }

    color = vec4(albedoColor * lighting, tint.a);
}