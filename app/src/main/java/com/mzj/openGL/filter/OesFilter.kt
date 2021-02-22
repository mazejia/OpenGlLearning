package com.mzj.openGL.filter

import android.content.res.Resources
import android.opengl.GLES11Ext
import android.opengl.GLES20

class OesFilter(mRes: Resources) : AFilter(mRes) {
    private var mHCoordMatrix:Int = 0
    private var mCoordMatrix = OM.copyOf(16)

    override fun onCreate() {
        createProgramByAssetsFile("shader/oes_base_vertex.sh","shader/oes_base_fragment.sh")
        mHCoordMatrix = GLES20.glGetUniformLocation(mProgram,"vCoordMatrix")
    }

    public fun setCoordMatrix(matrix:FloatArray){
        this.mCoordMatrix = matrix
    }

    override fun onSetExpandData() {
        super.onSetExpandData()
        GLES20.glUniformMatrix4fv(mHCoordMatrix,1,false,mCoordMatrix,0)
    }

    override fun onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId)
        GLES20.glUniform1i(mHTexture,textureType)
    }

    override fun onSizeChanged(width: Int, height: Int) {
    }
}