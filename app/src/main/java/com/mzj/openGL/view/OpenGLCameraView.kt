package com.mzj.openGL.view

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.mzj.openGL.camera.CameraDrawer
import com.mzj.openGL.camera.KitkatCamera
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLCameraView:GLSurfaceView,GLSurfaceView.Renderer {
    private var mCamera2: KitkatCamera? = null
    private var mCameraDrawer: CameraDrawer? = null
    private var cameraId = 1

    private var mRunnable: Runnable? = null


    constructor(context: Context?):super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?):super(context,attrs){
        init()
    }

    private fun init() {
        //第一步，设置版本，渲染render模式，初始化相机、相机绘制类
        setEGLContextClientVersion(2)
        setRenderer(this)
        renderMode = RENDERMODE_WHEN_DIRTY
        mCamera2 = KitkatCamera()
        mCameraDrawer = CameraDrawer(resources)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mCameraDrawer!!.onSurfaceCreated(gl, config)
        if (mRunnable != null) {
            mRunnable!!.run()
            mRunnable = null
        }
        //第二部，打开相机，设置纹理，显示预览
        mCamera2!!.open(cameraId)
        mCameraDrawer!!.setCameraId(cameraId)
        val point = mCamera2!!.getPreviewSize()
        mCameraDrawer!!.setDataSize(point.x, point.y)
        mCamera2!!.setPreviewTexture(mCameraDrawer!!.getSurfaceTexture())
        mCameraDrawer!!.getSurfaceTexture().setOnFrameAvailableListener { requestRender() }
        mCamera2!!.preview()
    }

    fun switchCamera() {
        mRunnable = Runnable {
            mCamera2!!.close()
            cameraId = if (cameraId == 1) 0 else 1
        }
        onPause()
        onResume()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mCameraDrawer!!.setViewSize(width, height)
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        mCameraDrawer!!.onDrawFrame(gl)
    }

    override fun onPause() {
        super.onPause()
        mCamera2!!.close()
    }
}