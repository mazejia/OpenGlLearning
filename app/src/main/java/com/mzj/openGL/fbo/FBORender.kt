package com.mzj.openGL.fbo

import android.content.res.Resources
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.mzj.openGL.filter.AFilter
import com.mzj.openGL.filter.GrayFilter
import com.mzj.openGL.utils.Gl2Utils
import java.nio.ByteBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FBORender : GLSurfaceView.Renderer {

    private lateinit var res:Resources
    private lateinit var mFilter:AFilter
    private var mBitmap:Bitmap? = null
    private lateinit var mBuffer:ByteBuffer

    private var fFrame = intArrayOf(1)
    private var fRender = intArrayOf(1)
    private var fTexture = intArrayOf(2)

    private var mCallback:Callback? = null

    public constructor(res: Resources){
        mFilter = GrayFilter(res)
    }

    public fun setCallback(callback: Callback){
        this.mCallback = callback
    }

    public fun setBitmap(bitmap: Bitmap){
        this.mBitmap = bitmap
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mFilter.create()
        mFilter.matrix = Gl2Utils.flip(Gl2Utils.originalMatrix, x = false, y = true)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    }

    override fun onDrawFrame(gl: GL10?) {
        if (mBitmap == null || mBitmap!!.isRecycled) {
            return
        }
        //TODO
        createEnvi()
        //绑定FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0])
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D, fTexture[1], 0
        )
        GLES20.glFramebufferRenderbuffer(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
            GLES20.GL_RENDERBUFFER, fRender[0]
        )
        GLES20.glViewport(0, 0, mBitmap!!.width, mBitmap!!.height)
        mFilter.textureId = fTexture[0]
        mFilter.draw()

        GLES20.glReadPixels(
            0, 0, mBitmap!!.width, mBitmap!!.height, GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE, mBuffer
        )

        if(mCallback != null){
            mCallback!!.onCall(mBuffer)
        }
        deleteEnvi()
        mBitmap!!.recycle()
    }

    public fun createEnvi(){
        //生成FBO
        GLES20.glGenFramebuffers(1, fFrame, 0)
        //生成renderBuffer
        GLES20.glGenRenderbuffers(1, fRender, 0)
        //绑定renderBuffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, fRender[0])
        GLES20.glRenderbufferStorage(
            GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
            mBitmap!!.width, mBitmap!!.height
        )
        GLES20.glFramebufferRenderbuffer(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
            GLES20.GL_RENDERBUFFER, fRender[0]
        )
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0)
        GLES20.glGenTextures(2, fTexture, 0)
        for (i in 0..1) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[i])
            if (i == 0) {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap, 0)
            } else {
                GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap!!.width, mBitmap!!.height,
                    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
                )
            }
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
        mBuffer = ByteBuffer.allocate(mBitmap!!.width * mBitmap!!.height * 4)
    }


    private fun deleteEnvi(){
        GLES20.glDeleteTextures(2,fTexture,0)
        GLES20.glDeleteRenderbuffers(1,fRender,0)
        GLES20.glDeleteFramebuffers(1,fFrame,0)
    }

    public interface Callback{
        fun onCall(data: ByteBuffer)
    }
}