#version 330 core

layout (location = 0) in vec3 position;

uniform vec2 color;

out vec4 vertexColor;

//uniform mat4 model;
//uniform mat4 view;
//uniform mat4 projection;

void main()
{
    //mat4 mvp = projection * view * model;
    //gl_Position = mvp * vec4(position, 1.0);
    gl_Position = vec4(position, 1.0);
    vertexColor = vec4(color, 1.0, 1.0);
}
