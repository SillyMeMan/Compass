#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 uv = texCoord;

    vec2 center = vec2(0.5, 0.5);
    vec2 offset = uv - center;
    float dist = length(offset);
    uv += offset * dist * 0.03;

    vec2 texel = 1.0 / InSize;
    vec3 sum = vec3(0.0);
    float count = 0.0;
    for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
            sum += texture(DiffuseSampler, uv + vec2(x, y) * texel).rgb;
            count += 1.0;
        }
    }
    vec3 blurred = sum / count;

    float shift = 0.002;
    float r = texture(DiffuseSampler, uv + vec2( shift, 0.0)).r;
    float g = texture(DiffuseSampler, uv).g;
    float b = texture(DiffuseSampler, uv + vec2(-shift, 0.0)).b;

    vec3 color = mix(vec3(r,g,b), blurred, 0.6);

    fragColor = vec4(color, 1.0);
}
