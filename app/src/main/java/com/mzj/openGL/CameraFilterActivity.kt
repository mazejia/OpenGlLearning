package com.mzj.openGL

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.SurfaceTexture
import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.hardware.camera2.*
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.Surface
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import com.mzj.openGL.camerafilter.ColorFilter
import com.mzj.openGL.render.CameraPreviewRender
import com.mzj.openGL.utils.CameraUtils
import java.util.*

/**
 * 实时滤镜
 */
class CameraFilterActivity : Activity() {

    private lateinit var cameraPreviewRender:CameraPreviewRender
    private lateinit var glSurfaceView:GLSurfaceView
    private lateinit var cameraManager:CameraManager
    private var cameraId:String? = null
    private var outputSizes:List<Size?>? = null
    private var photoSize:Size? = null
    private var useFront:Boolean = false
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var previewRequestBuilder: CaptureRequest.Builder? = null
    private var previewRequest: CaptureRequest? = null
    private var surfaceTexture: SurfaceTexture? = null
    private var surface: Surface? = null
    private lateinit var btnCamera:Button
    private lateinit var btnColorFilter:Button
    private lateinit var btnPhoto:Button


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        CameraUtils.init(this)
        initCamera()
        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initCamera(){
        cameraManager = CameraUtils.getCameraManager()
        cameraId = CameraUtils.getCameraId(useFront)
        outputSizes = CameraUtils.getCameraOutputSizes(cameraId, SurfaceTexture::class.java)
        photoSize = outputSizes?.get(16)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initViews(){
        glSurfaceView = findViewById(R.id.glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        cameraPreviewRender = CameraPreviewRender(resources)
        glSurfaceView.setRenderer(cameraPreviewRender)

        btnColorFilter = findViewById(R.id.btnColorFilter)
        btnColorFilter.setOnClickListener(View.OnClickListener {
            if (ColorFilter.COLOR_FLAG < 7) {
                ColorFilter.COLOR_FLAG++
            } else {
                ColorFilter.COLOR_FLAG = 0
            }
        })

        btnPhoto = findViewById(R.id.btnPhoto)
        btnPhoto.setOnClickListener(View.OnClickListener { cameraPreviewRender.setTakingPhoto(true) })

        btnCamera = findViewById(R.id.btnCamera)
        btnCamera.setOnClickListener(View.OnClickListener { //切换相机
            changeCamera()
        })
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        openCamera()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPause() {
//        glSurfaceView.onPause();
        releaseCamera()
        super.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    private fun openCamera() {
        try {
            cameraManager.openCamera(cameraId!!, cameraStateCallback, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun changeCamera() {
        releaseCamera()
        useFront = !useFront
        cameraId = CameraUtils.getCameraId(useFront)
        openCamera()
        cameraPreviewRender.setUseFront(useFront)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun releaseCamera() {
        CameraUtils.releaseCameraSession(captureSession)
        CameraUtils.releaseCameraDevice(cameraDevice)
    }

    var cameraStateCallback: CameraDevice.StateCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            surfaceTexture = cameraPreviewRender.getSurfaceTexture()
            if (surfaceTexture == null) {
                return
            }
            surfaceTexture!!.setDefaultBufferSize(photoSize!!.width, photoSize!!.height)
            surfaceTexture!!.setOnFrameAvailableListener(OnFrameAvailableListener { glSurfaceView.requestRender() })
            surface = Surface(surfaceTexture)
            try {
                cameraDevice = camera
                previewRequestBuilder =
                    cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                previewRequestBuilder!!.addTarget(surface!!)
                previewRequest = previewRequestBuilder!!.build()
                cameraDevice!!.createCaptureSession(
                    Arrays.asList(surface),
                    sessionsStateCallback,
                    null
                )
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }

        override fun onDisconnected(camera: CameraDevice) {
        }

        override fun onError(camera: CameraDevice, error: Int) {
        }
    }

    var sessionsStateCallback: CameraCaptureSession.StateCallback =
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                if (null == cameraDevice) {
                    return
                }
                captureSession = session
                try {
                    captureSession!!.setRepeatingRequest(
                        previewRequest!!,
                        null,
                        null
                    )
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
            }
        }
}