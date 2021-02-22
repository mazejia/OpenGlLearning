package com.mzj.openGL.camera

import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.Camera
import java.io.IOException
import java.util.*

class KitkatCamera : ICamera{
    private var mConfig:ICamera.Config = ICamera.Config()
    private var mCamera:Camera? = null
    private var sizeComparator:CameraSizeComparator
    private lateinit var picSize:Camera.Size
    private lateinit var preSize:Camera.Size
    private lateinit var mPicSize:Point
    private lateinit var mPreSize:Point

    init {
        mConfig.minPreviewWidth = 720
        mConfig.minPictureWidth = 720
        mConfig.rate = 1.778f
        sizeComparator = CameraSizeComparator()
    }

    override fun open(cameraId: Int): Boolean {
        mCamera = Camera.open(cameraId)
        if (mCamera == null){
            return false
        }
        val param = mCamera!!.parameters
        picSize = getPropPictureSize(
            param.supportedPictureSizes,
            mConfig.rate,
            mConfig.minPictureWidth
        )
        preSize = getPropPreviewSize(
            param.supportedPreviewSizes,
            mConfig.rate,
            mConfig.minPreviewWidth
        )
        param.setPictureSize(picSize.width, picSize.height)
        param.setPreviewSize(preSize.width, preSize.height)
        mCamera!!.parameters = param
        var pre = param.previewSize
        var pic = param.pictureSize
        mPicSize = Point(pic.height, pic.width)
        mPreSize = Point(pre.height, pre.width)
        return true;
    }

    override fun setConfig(config: ICamera.Config) {
        this.mConfig = config
    }

    override fun preview(): Boolean {
        if (mCamera != null){
            mCamera!!.startPreview()
            return true
        }
        return false
    }

    override fun switchTo(cameraId: Int) {
        close()
        open(cameraId)
    }

    override fun takePhoto(callback: ICamera.TakePhotoCallback) {
    }

    override fun close() {
        if(mCamera == null){
            return
        }
        try {
            mCamera!!.stopPreview()
            mCamera!!.release()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun setPreviewTexture(texture: SurfaceTexture) {
        if (mCamera == null){
            return
        }
        try {
            mCamera!!.setPreviewTexture(texture)
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    override fun getPreviewSize(): Point {
        return mPreSize
    }

    override fun getPictureSize(): Point {
        return mPicSize
    }

    override fun setOnPreviewFrameCallback(callback: ICamera.PreviewFrameCallback) {
        if(mCamera == null){
            return
        }
        mCamera!!.setPreviewCallback { data, camera ->
            callback.onPreviewFrame(data, mPreSize.x, mPreSize.y)
        }
    }

    fun addBuffer(buffer: ByteArray?) {
        if (mCamera != null) {
            mCamera!!.addCallbackBuffer(buffer)
        }
    }

    public fun setOnPreviewFrameCallbackWithBuffer(callBack: ICamera.PreviewFrameCallback) {
        if (mCamera != null) {
            mCamera!!.setPreviewCallbackWithBuffer { data, camera ->
                callBack.onPreviewFrame(
                    data,
                    mPreSize.x,
                    mPreSize.y
                )
            }
        }
    }

    private fun getPropPreviewSize(list: List<Camera.Size>, th: Float, minWidth: Int):Camera.Size{
        Collections.sort(list, sizeComparator)
        var i = 0
        for (s in list){
            if (s.height >= minWidth && equalRate(s, th)){
                break;
            }
            i++;
        }
        if (i == list.size){
            i = 0;
        }
        return list[i]
    }

    private fun getPropPictureSize(list: List<Camera.Size>, th: Float, minWidth: Int): Camera.Size {
        Collections.sort(list, sizeComparator)
        var i = 0
        for (s in list) {
            if (s.height >= minWidth && equalRate(s, th)) {
                break
            }
            i++
        }
        if (i == list.size) {
            i = 0
        }
        return list[i]
    }

    private fun equalRate(s: Camera.Size, rate: Float): Boolean {
        val r = s.width.toFloat() / s.height.toFloat()
        return Math.abs(r - rate) <= 0.03
    }

    @Suppress("DEPRECATION")
    private class CameraSizeComparator : Comparator<android.hardware.Camera.Size>{
        override fun compare(o1: android.hardware.Camera.Size, o2: android.hardware.Camera.Size): Int {
            return when {
                o1.height == o2.height -> {
                    0;
                }
                o1.height > o2.height -> {
                    1
                }
                else -> {
                    -1
                }
            }
        }
    }
}