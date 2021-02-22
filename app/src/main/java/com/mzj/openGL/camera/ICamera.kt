package com.mzj.openGL.camera

import android.graphics.Point
import android.graphics.SurfaceTexture

interface ICamera {

    //打开相机
    fun open(cameraId:Int):Boolean

    //设置配置
    fun setConfig(config:Config)

    //预览
    fun preview():Boolean

    //切换摄像头
    fun switchTo(cameraId:Int)

    //照相
    fun takePhoto(callback:TakePhotoCallback)

    //关闭
    fun close()

    //设置预览
    fun setPreviewTexture(texture:SurfaceTexture)

    //获取预览size
    fun getPreviewSize():Point

    //获取图片size
    fun getPictureSize():Point

    //设置预览回调
    fun setOnPreviewFrameCallback(callback:PreviewFrameCallback)

    class Config{
        var rate:Float = 0.0f
        var minPreviewWidth:Int = 0
        var minPictureWidth:Int = 0
    }


    interface TakePhotoCallback{
        fun onTakePhoto(bytes:ByteArray,width:Int,height:Int):Void
    }

    interface PreviewFrameCallback{
        fun onPreviewFrame(bytes:ByteArray,width:Int,height:Int):Void
    }
}