#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES vTexture;
uniform int colorFlag;

vec3 rgb2hsl(vec3 color){
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(color.bg, K.wz), vec4(color.gb, K.xy), step(color.b, color.g));
    vec4 q = mix(vec4(p.xyw, color.r), vec4(color.r, p.yzx), step(p.x, color.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsl2rgb(vec3 color){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(color.xxx + K.xyz) * 6.0 - K.www);
    return color.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), color.y);
}

void grey(inout vec4 color){
    float weightMean = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;
    color.r = color.g = color.b = weightMean;
}

void blackAndWhite(inout vec4 color){
    float threshold = 0.5;
    float mean = (color.r + color.g + color.b) / 3.0;
    color.r = color.g = color.b = mean >= threshold ? 1.0 : 0.0;
}

void reverse(inout vec4 color){
    color.r = 1.0 - color.r;
    color.g = 1.0 - color.g;
    color.b = 1.0 - color.b;
}

void light(inout vec4 color){
    vec3 hslColor = vec3(rgb2hsl(color.rgb));
    hslColor.z += 0.15;
    color = vec4(hsl2rgb(hslColor), color.a);
}

void light2(inout vec4 color){
    color.r += 0.15;
    color.g += 0.15;
    color.b += 0.15;
}

void posterization(inout vec4 color){
    //计算灰度值
    float grayValue = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;
    //转换到hsl颜色空间
    vec3 hslColor = vec3(rgb2hsl(color.rgb));
    //根据灰度值区分阴影和高光，分别处理
    if(grayValue < 0.3){
        //添加蓝色
        if(hslColor.x < 0.68 || hslColor.x > 0.66){
            hslColor.x = 0.67;
        }
        //增加饱和度
        hslColor.y += 0.3;
    }else if(grayValue > 0.7){
        //添加黄色
        if(hslColor.x < 0.18 || hslColor.x > 0.16){
            hslColor.x = 0.17;
        }
        //降低饱和度
        hslColor.y -= 0.3;
    }
    color = vec4(hsl2rgb(hslColor), color.a);
}

void main() {
    vec4 tmpColor = texture2D( vTexture, textureCoordinate );
    if(colorFlag == 1){
      grey(tmpColor);
    } else if(colorFlag == 2){
      blackAndWhite(tmpColor);
    } else if(colorFlag == 3){
      reverse(tmpColor);
    }

    gl_FragColor = tmpColor;
}