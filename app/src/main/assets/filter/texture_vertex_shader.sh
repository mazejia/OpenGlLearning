attribute vec4 a_Position;
attribute vec2 a_texCoord;
uniform mat4 u_matrix;
varying vec2 v_texCoord;

void main(){
    gl_Position=u_matrix*a_Position;
    v_texCoord=a_texCoord;
}