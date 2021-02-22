package com.mzj.openGL.camera

import android.content.res.Resources
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.mzj.openGL.filter.AFilter
import com.mzj.openGL.filter.OesFilter
import com.mzj.openGL.utils.Gl2Utils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraDrawer : GLSurfaceView.Renderer {
    private var matrix = FloatArray(16)
    private lateinit var surfaceTexture:SurfaceTexture
    private var width = 0
    private var height = 0
    private var dataWidth = 0
    private var dataHeight = 0
    private var mOesFilter: AFilter
    private var cameraId = 1

    public constructor(res: Resources){
        mOesFilter = OesFilter(res)
    }

    public fun setDataSize(dataWidth: Int, dataHeight: Int){
        this.dataWidth = dataWidth
        this.dataHeight = dataHeight
        calculateMatrix()
    }

    public fun setViewSize(width: Int, height: Int){
        this.width = width
        this.height = height
        calculateMatrix()
    }

    private fun calculateMatrix(){
       Gl2Utils.getShowMatrix(matrix, this.dataWidth, this.dataHeight, this.width, this.height)
        if (cameraId == 1){
            Gl2Utils.flip(matrix, x = true, y = false)
            Gl2Utils.rotate(matrix, 90f)
        } else {
            Gl2Utils.rotate(matrix, 270f)
        }
        mOesFilter.matrix = matrix
    }

    public fun getSurfaceTexture():SurfaceTexture{
        return surfaceTexture
    }

    public fun setCameraId(id: Int){
        this.cameraId = id
        calculateMatrix()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        var texture = createTextureID()
        surfaceTexture = SurfaceTexture(texture)
        mOesFilter.create()
        mOesFilter.textureId = texture
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        setViewSize(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        surfaceTexture.updateTexImage()
        mOesFilter.draw()
    }

    private fun createTextureID():Int{
        var texture = intArrayOf(1)
        GLES20.glGenTextures(1, texture, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0])
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MIN_FILTER,
            GL10.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE
        )
        return texture[0]
    }

}