package com.mzj.openGL.view

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {

    val vertexData =
            "attribute vec4 vPosition;" +
            "void main() {" +
            "    gl_Position = vPosition;" +
            "}"

    val fragmentData =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "    gl_FragColor = vColor;" +
            "}"

    lateinit var vertexBuffer:FloatBuffer
    var mProgram:Int = 0

    var triangleCoords = floatArrayOf(
        0.5f, 0.5f, 0.0f,  // top
        -0.5f, -0.5f, 0.0f,  // bottom left
        0.5f, -0.5f, 0.0f // bottom right
    )

    var color = floatArrayOf(
        1.0f, 1.0f, 1.0f, 1.0f
    ) //白色

    //顶点句柄
    var mPositionHandle:Int = 0
    //颜色句柄
    var mColorHandle:Int = 0
    private val COORDS_PER_VERTEX = 3


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //设置清空颜色后所使用的颜色（黑色）
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        //申请底层空间
        val bb = ByteBuffer.allocateDirect(vertexData.length * 4)
        bb.order(ByteOrder.nativeOrder())

        //将坐标数据转换为FloatBuffer,用以传入给OpenGL ES程序
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)

        var vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexData)
        var fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentData)

        //创建一个空的OpenGLES程序
        mProgram  = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram,vertexShader)
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram,fragmentShader)
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //设置窗口大小，前两个参数为左下角坐标，后两个参数为宽度和高度
        GLES20.glViewport(0, 0, width, height);
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glUseProgram(mProgram)

        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram,"vPosition")
        //启用顶点句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        //准备三角形坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle,COORDS_PER_VERTEX,GLES20.GL_FLOAT,false,COORDS_PER_VERTEX * 4,vertexBuffer)

        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram,"vColor")
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle,1,color,0)

        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,triangleCoords.size / COORDS_PER_VERTEX)
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle)

    }



    fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

}