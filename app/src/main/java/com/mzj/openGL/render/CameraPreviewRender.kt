package com.mzj.openGL.render

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Environment
import android.util.Log
import com.mzj.openGL.camerafilter.CameraFilter
import com.mzj.openGL.camerafilter.ColorFilter
import com.mzj.openGL.utils.MatrixUtils
import java.io.*
import java.nio.ByteBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraPreviewRender : GLSurfaceView.Renderer {

    private var useFront = false
    var matrix = FloatArray(16)
    private var takingPhoto = false
    private var recordingVideo = false
    private var surfaceTexture: SurfaceTexture? = null
    var cameraTexture = IntArray(1)
    var width = 0
    var height:Int = 0
    var exportFrame = IntArray(1)
    var exportTexture = IntArray(1)
    var cameraFilter: CameraFilter? = null
    var colorFilter: ColorFilter? = null

    constructor(res:Resources){
        cameraFilter = CameraFilter(res)
        colorFilter = ColorFilter(res)
    }


    fun setUseFront(useFront: Boolean) {
        if (this.useFront != useFront) {
            this.useFront = useFront
            cameraFilter!!.setUseFront(useFront)
            matrix = MatrixUtils.flip(matrix, true, false)
        }
    }

    fun getSurfaceTexture(): SurfaceTexture? {
        return surfaceTexture
    }

    fun isTakingPhoto(): Boolean {
        return takingPhoto
    }

    fun setTakingPhoto(takingPhoto: Boolean) {
        this.takingPhoto = takingPhoto
    }

    fun isRecordingVideo(): Boolean {
        return recordingVideo
    }

    fun setRecordingVideo(recordingVideo: Boolean) {
        this.recordingVideo = recordingVideo
    }

    private fun createTexture() {
        GLES20.glGenTextures(cameraTexture.size, cameraTexture, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        createTexture()
        surfaceTexture = SurfaceTexture(cameraTexture[0])

        cameraFilter!!.onSurfaceCreated()
        colorFilter!!.onSurfaceCreated()
        matrix = MatrixUtils.flip(colorFilter!!.getMatrix(), false, true)
        colorFilter!!.setMatrix(matrix)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        if(this.width != width || this.height != height){
            this.width = width;
            this.height = height;

            cameraFilter!!.onSurfaceChanged(width, height)
            colorFilter!!.onSurfaceChanged(width, height)
            delFrameBufferAndTexture()
            genFrameBufferAndTexture()
        }

    }

    override fun onDrawFrame(gl: GL10?) {
        if(surfaceTexture != null){
            surfaceTexture!!.updateTexImage()
        }

        cameraFilter!!.setTextureId(cameraTexture)
        cameraFilter!!.onDraw()

        colorFilter!!.setTextureId(cameraFilter!!.getOutputTextureId())

        if (isTakingPhoto()) {
            val exportBuffer = ByteBuffer.allocate(width * height * 4)
            bindFrameBufferAndTexture()
            colorFilter!!.setMatrix(MatrixUtils.flip(matrix, false, true))
            colorFilter!!.onDraw()
            GLES20.glReadPixels(
                0,
                0,
                width,
                height,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                exportBuffer
            )
            savePhoto(exportBuffer)
            unBindFrameBuffer()
            setTakingPhoto(false)
            colorFilter!!.setMatrix(MatrixUtils.flip(matrix, false, true))
        } else {
            colorFilter!!.onDraw()
        }
    }
    fun delFrameBufferAndTexture() {
        GLES20.glDeleteFramebuffers(exportFrame.size, exportFrame, 0)
        GLES20.glDeleteTextures(exportTexture.size, exportTexture, 0)
    }

    fun genFrameBufferAndTexture() {
        GLES20.glGenFramebuffers(exportFrame.size, exportFrame, 0)
        GLES20.glGenTextures(exportTexture.size, exportTexture, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, exportTexture[0])
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
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, exportFrame[0])
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
            exportTexture[0], 0
        )
    }

    fun unBindFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    //    public ByteBuffer getPixelBuffer(){
    //        final ByteBuffer buffer = ByteBuffer.allocate(width * height * 4);
    //        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    //        return buffer;
    //    }
    fun savePhoto(buffer: ByteBuffer?) {
        Thread(Runnable {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap!!.copyPixelsFromBuffer(buffer)
            val folderPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/"
            val folder = File(folderPath)
            if (!folder.exists() && !folder.mkdirs()) {
                Log.e("demos", "图片目录异常")
                return@Runnable
            }
            val filePath = folderPath + System.currentTimeMillis() + ".jpg"
            var bos: BufferedOutputStream? = null
            try {
                val fos = FileOutputStream(filePath)
                bos = BufferedOutputStream(fos)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                if (bos != null) {
                    try {
                        bos.flush()
                        bos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                bitmap?.recycle()
            }
        }).start()
    }
}