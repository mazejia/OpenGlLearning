package com.mzj.openGL.filter

import android.content.res.Resources
import android.opengl.GLES11Ext
import android.opengl.GLES20

class OesFilter(mRes: Resources) : AFilter(mRes) {
    private var mHCoordMatrix:Int = 0
    private var mCoordMatrix = OM.copyOf(16)
    private var colorFlag = 0
    private var colorValue = filter_reset

    companion object {
        //灰度图
        const val filter_gray = 1
        //黑白图
        const val filter_black_white = 2
        //反转图
        const val filter_reverse = 3
        //重置
        const val filter_reset = 0
    }

    override fun onCreate() {
        createProgramByAssetsFile("shader/oes_base_vertex.sh","shader/oes_base_fragment.sh")
        mHCoordMatrix = GLES20.glGetUniformLocation(mProgram,"vCoordMatrix")
        colorFlag = GLES20.glGetUniformLocation(mProgram, "colorFlag")
    }

    fun setGrayFilter(){
        this.colorValue = filter_gray
    }

    fun setBlackWhiteFilter(){
        this.colorValue = filter_black_white
    }

    fun setReverseFilter(){
        this.colorValue = filter_reverse
    }

    fun resetFilter(){
        this.colorValue = filter_reset
    }

    public fun setCoordMatrix(matrix:FloatArray){
        this.mCoordMatrix = matrix
    }

    override fun onSetExpandData() {
        super.onSetExpandData()
        GLES20.glUniformMatrix4fv(mHCoordMatrix,1,false,mCoordMatrix,0)
        GLES20.glUniform1i(colorFlag,colorValue)
    }

    override fun onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId)
        GLES20.glUniform1i(mHTexture,textureType)
    }

    override fun onSizeChanged(width: Int, height: Int) {
    }
}