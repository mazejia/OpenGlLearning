package com.mzj.openGL.camerafilter

import android.content.res.Resources
import android.opengl.GLES20
import android.util.Log
import com.mzj.openGL.R
import com.mzj.openGL.utils.Gl2Utils

class ColorFilter(resources: Resources) : BaseFilter(resources) {

    var hColorFlag = 0
    var hTextureLUT = 0
    private var LUTTextureId = 0

    companion object{
        val UNIFORM_COLOR_FLAG = "colorFlag"
        val UNIFORM_TEXTURE_LUT = "textureLUT"
        var COLOR_FLAG = 0
        var COLOR_FLAG_USE_LUT = 6
    }

    override fun onSurfaceCreated() {
        super.onSurfaceCreated()
        LUTTextureId = Gl2Utils.loadTextureFromRes(super.res,R.drawable.amatorka)
    }

    override fun initProgram(): Int {
        return Gl2Utils.createGlProgramByRes(super.res,
            "filter/texture_vertex_shader.sh",
            "filter/texture_color_fragtment_shader.sh"
        )
    }

    override fun initAttribLocations() {
        super.initAttribLocations()

        hColorFlag = GLES20.glGetUniformLocation(
            program, UNIFORM_COLOR_FLAG
        )
        hTextureLUT = GLES20.glGetUniformLocation(
            program, UNIFORM_TEXTURE_LUT
        )
    }

    override fun setExtend() {
        super.setExtend()
        GLES20.glUniform1i(hColorFlag, COLOR_FLAG)
    }

    override fun bindTexture() {
        super.bindTexture()
        if (COLOR_FLAG == COLOR_FLAG_USE_LUT) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, LUTTextureId)
            GLES20.glUniform1i(hTextureLUT, 1)
        }
    }
}