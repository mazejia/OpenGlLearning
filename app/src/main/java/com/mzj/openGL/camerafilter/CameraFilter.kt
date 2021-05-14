package com.mzj.openGL.camerafilter

import android.content.res.Resources
import android.opengl.GLES20
import com.mzj.openGL.utils.CommonUtil

class CameraFilter(resources: Resources) : CameraOesFilter(resources) {

    //后置相机，顺时针旋转90度
    lateinit var textureCoordCameraBack:FloatArray

    //前置相机，逆时针旋转90度
    lateinit var textureCoordCameraFront: FloatArray


    var frameBuffer = IntArray(1)
    var frameTexture = IntArray(1)

    private var useFront:Boolean = false

    fun isUseFront(): Boolean {
        return useFront
    }

    fun setUseFront(useFront: Boolean) {
        if (this.useFront != useFront) {
            this.useFront = useFront
            textureCoordBuffer =
                if (useFront) CommonUtil.getFloatBuffer(textureCoordCameraFront) else CommonUtil.getFloatBuffer(
                    textureCoordCameraBack
                )
        }
    }

    override fun initBuffer() {
        initVer()
        vertexBuffer = CommonUtil.getFloatBuffer(super.vertex)
        textureCoordBuffer =
            if (useFront) {
                CommonUtil.getFloatBuffer(textureCoordCameraFront)
            } else{
                CommonUtil.getFloatBuffer(textureCoordCameraBack)
            }
    }

    private fun initVer(){
        textureCoordCameraBack = floatArrayOf(
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
        )
        textureCoordCameraFront = floatArrayOf(
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
        )
    }

    override fun getOutputTextureId(): IntArray? {
        return frameTexture
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        if(this.width != width || this.height != height){
            this.width = width;
            this.height = height
            delFrameBufferAndTexture()
            genFrameBufferAndTexture()
        }
    }

    override fun onDraw() {
        bindFrameBufferAndTexture()
        super.onDraw()
        unBindFrameBuffer()
    }

    fun delFrameBufferAndTexture(){
        GLES20.glDeleteFramebuffers(frameBuffer.size, frameBuffer, 0)
        GLES20.glDeleteTextures(frameTexture.size, frameTexture, 0)
    }

    fun genFrameBufferAndTexture(){
        GLES20.glGenFramebuffers(frameBuffer.size, frameBuffer, 0)

        GLES20.glGenTextures(frameTexture.size, frameTexture, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameTexture[0])
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            width,
            height,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            null
        )
        setTextureParameters()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    fun setTextureParameters() {
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE.toFloat()
        )
    }

    fun bindFrameBufferAndTexture() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0])
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
            frameTexture[0], 0
        )
    }

    fun unBindFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

}