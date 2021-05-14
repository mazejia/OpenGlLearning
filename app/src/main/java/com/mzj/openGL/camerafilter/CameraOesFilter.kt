package com.mzj.openGL.camerafilter

import android.content.res.Resources
import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.mzj.openGL.utils.Gl2Utils

open class CameraOesFilter(res: Resources) : BaseFilter(res) {

    override fun initProgram(): Int {
        return Gl2Utils.createGlProgramByRes(
            super.res,
            "filter/texture_vertex_shader.sh",
            "filter/texture_oes_fragtment_shader.sh"
        )
    }

    override fun bindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, getTextureId()!![0])
        GLES20.glUniform1i(hTexture, 0)
    }
}