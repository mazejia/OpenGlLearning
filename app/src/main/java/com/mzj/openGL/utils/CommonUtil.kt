package com.mzj.openGL.utils

import android.app.ActivityManager
import android.content.Context
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object CommonUtil {

    const val TAG = "CommonUtil"

    const val BYTES_PER_FLOAT = 4

    fun checkGLVersion(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val ci = am.deviceConfigurationInfo
        return ci.reqGlEsVersion >= 0x20000
    }

    fun getFloatBuffer(array: FloatArray): FloatBuffer {
        //将顶点数据拷贝映射到 native 内存中，以便opengl能够访问
        val buffer = ByteBuffer
            .allocateDirect(array.size * BYTES_PER_FLOAT) //直接分配 native 内存，不会被gc
            .order(ByteOrder.nativeOrder()) //和本地平台保持一致的字节序（大/小头）
            .asFloatBuffer() //将底层字节映射到FloatBuffer实例，方便使用

        buffer
            .put(array) //将顶点拷贝到 native 内存中
            .position(0) //每次 put position 都会 + 1，需要在绘制前重置为0
        return buffer
    }
}