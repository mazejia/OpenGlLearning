package com.mzj.openGL.utils

import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi
import java.util.*

/**
 * Description:
 */
object CameraUtils {

    private lateinit var appContext: Context
    private lateinit var cameraManager: CameraManager

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun init(context: Context) {
        appContext = context.applicationContext
        cameraManager = appContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    fun getCameraManager(): CameraManager {
        return cameraManager
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getFrontCameraId(): String? {
        return getCameraId(true)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getBackCameraId(): String? {
        return getCameraId(false)
    }

    /**
     * 获取相机id
     * @param useFront 是否使用前置相机
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getCameraId(useFront: Boolean): String? {
        try {
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId!!)
                val cameraFacing = characteristics.get(CameraCharacteristics.LENS_FACING)!!
                if (useFront) {
                    if (cameraFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                        return cameraId
                    }
                } else {
                    if (cameraFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        return cameraId
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 根据输出类获取指定相机的输出尺寸列表，降序排序
     * @param cameraId 相机id
     * @param clz 输出类
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getCameraOutputSizes(cameraId: String?, clz: Class<*>): List<Size?>? {
        try {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId!!)
            val configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            val sizes: List<Size?> = listOf(*configs!!.getOutputSizes(clz))

            Collections.sort(sizes) {
                    o1, o2 -> o1!!.width * o1!!.height - o2!!.width * o2!!.height 
            }

            Collections.reverse(sizes)
            return sizes
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 根据输出格式获取指定相机的输出尺寸列表
     * @param cameraId 相机id
     * @param format 输出格式
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getCameraOutputSizes(cameraId: String?, format: Int): List<Size?>? {
        try {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId!!)
            val configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            return Arrays.asList(*configs!!.getOutputSizes(format))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 释放相机资源
     * @param cameraDevice
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun releaseCameraDevice(cameraDevice: CameraDevice?) {
        var cameraDevice = cameraDevice
        if (cameraDevice != null) {
            cameraDevice.close()
            cameraDevice = null
        }
    }

    /**
     * 关闭相机会话
     * @param session
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun releaseCameraSession(session: CameraCaptureSession?) {
        var session = session
        if (session != null) {
            session.close()
            session = null
        }
    }

    /**
     * 关闭 ImageReader
     * @param reader
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun releaseImageReader(reader: ImageReader?) {
        var reader = reader
        if (reader != null) {
            reader.close()
            reader = null
        }
    }
}