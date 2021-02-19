precision mediump float;

uniform sampler2D vTexture;
varying vec2 aCoordinate;

void main(){
    vec4 nColor=texture2D(vTexture,aCoordinate);
    gl_FragColor=nColor;
}