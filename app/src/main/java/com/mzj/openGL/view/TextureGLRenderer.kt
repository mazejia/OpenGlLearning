package com.mzj.openGL.view

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import com.mzj.openGL.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TextureGLRenderer : GLSurfaceView.Renderer {
    private var context:Context
    private var mProgram:Int = 0
    private var glHPosition = 0
    private var glHTexture = 0
    private var glHCoordinate = 0
    private var glHMatrix = 0
    private var mBitmap: Bitmap? = null

    private var bPos: FloatBuffer
    private var bCoord: FloatBuffer
    private var textureId = 0
    private var vertex: String
    private var fragment: String
    private var mViewMatrix = FloatArray(16)
    private var mProjectMatrix = FloatArray(16)
    private var mMVPMatrix = FloatArray(16)

    private var sPos = floatArrayOf(
        -1.0f, 1.0f,
        -1.0f, -1.0f,
        1.0f, 1.0f,
        1.0f, -1.0f
    )

    private var sCoord = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
    )

    fun setImage(bitmap: Bitmap){
        this.mBitmap = bitmap
    }

    public constructor(context: Context, vertexData: String, fragmentData: String){
        this.context = context
        this.vertex = vertexData
        this.fragment = fragmentData

        var bb = ByteBuffer.allocateDirect(sPos.size * 4)
        bb.order(ByteOrder.nativeOrder())
        bPos = bb.asFloatBuffer()
        bPos.put(sPos)
        bPos.position(0)

        val cc = ByteBuffer.allocateDirect(sCoord.size * 4)
        cc.order(ByteOrder.nativeOrder())
        bCoord=cc.asFloatBuffer()
        bCoord.put(sCoord)
        bCoord.position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_TEXTURE_2D)
        mProgram=ShaderUtils.createProgram(context.resources, vertex, fragment)
        glHPosition = GLES20.glGetAttribLocation(mProgram, "vPosition")
        glHCoordinate = GLES20.glGetAttribLocation(mProgram, "vCoordinate")
        glHTexture = GLES20.glGetUniformLocation(mProgram, "vTexture")
        glHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //设置窗口大小，前两个参数为左下角坐标，后两个参数为宽度和高度
        GLES20.glViewport(0, 0, width, height);

        val w = if(mBitmap == null) 0 else mBitmap!!.width
        val h = if(mBitmap == null) 0 else mBitmap!!.height

        var sWH = w.toFloat() / h.toFloat()
        var sWidthHeight = width.toFloat() / height.toFloat()

        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(
                    mProjectMatrix,
                    0,
                    -sWidthHeight * sWH,
                    sWidthHeight * sWH,
                    -1f,
                    1f,
                    3f,
                    5f
                )
            } else {
                Matrix.orthoM(
                    mProjectMatrix,
                    0,
                    -sWidthHeight / sWH,
                    sWidthHeight / sWH,
                    -1f,
                    1f,
                    3f,
                    5f
                )
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(
                    mProjectMatrix,
                    0,
                    -1f,
                    1f,
                    -1 / sWidthHeight * sWH,
                    1 / sWidthHeight * sWH,
                    3f,
                    5f
                )
            } else {
                Matrix.orthoM(
                    mProjectMatrix,
                    0,
                    -1f,
                    1f,
                    -sWH / sWidthHeight,
                    sWH / sWidthHeight,
                    3f,
                    5f
                )
            }
        }
        //设置相机位置
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        //计算变换矩阵
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(mProgram)

        GLES20.glUniformMatrix4fv(glHMatrix, 1, false, mMVPMatrix, 0)
        GLES20.glEnableVertexAttribArray(glHPosition)
        GLES20.glEnableVertexAttribArray(glHCoordinate)
        GLES20.glUniform1i(glHTexture, 0)
        textureId=createTexture()
        GLES20.glVertexAttribPointer(glHPosition, 2, GLES20.GL_FLOAT, false, 0, bPos)
        GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 0, bCoord)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    private fun createTexture():Int{
        var texture = intArrayOf(1)
        if (mBitmap != null && !mBitmap!!.isRecycled) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0)
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST.toFloat()
            )
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR.toFloat()
            )
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            //根据以上指定参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,mBitmap,0)
            return texture[0]
        }
        return 0
    }


}