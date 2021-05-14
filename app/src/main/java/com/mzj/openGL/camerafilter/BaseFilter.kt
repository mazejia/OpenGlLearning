package com.mzj.openGL.camerafilter

import android.content.res.Resources
import android.opengl.GLES20
import com.mzj.openGL.utils.CommonUtil
import com.mzj.openGL.utils.Gl2Utils
import java.nio.FloatBuffer

open class BaseFilter {
    val VERTEX_ATTRIB_POSITION = "a_Position"
    val VERTEX_ATTRIB_POSITION_SIZE = 3
    val VERTEX_ATTRIB_TEXTURE_POSITION = "a_texCoord"
    val VERTEX_ATTRIB_TEXTURE_POSITION_SIZE = 2
    val UNIFORM_TEXTURE = "s_texture"
    val UNIFORM_MATRIX = "u_matrix"


    val vertex = floatArrayOf(
        -1f, 1f, 0.0f,  //左上
        -1f, -1f, 0.0f,  //左下
        1f, -1f, 0.0f,  //右下
        1f, 1f, 0.0f //右上
    )

    val textureCoord = floatArrayOf(
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
    )

    private var matrix = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    var vertexBuffer: FloatBuffer? = null
    var textureCoordBuffer: FloatBuffer? = null

    private var textureId: IntArray? = null
    var program = 0
    var hVertex = 0
    var hMatrix:Int = 0
    var hTextureCoord:Int = 0
    var hTexture:Int = 0

    var width = 0
    var height:Int = 0
    lateinit var res:Resources;

    fun getMatrix():FloatArray{
        return matrix
    }

    fun setMatrix(array: FloatArray){
        this.matrix = array
    }

    constructor(res:Resources){
        this.res = res;
        initBuffer()
    }

    open fun initBuffer(){
        vertexBuffer = CommonUtil.getFloatBuffer(vertex)
        textureCoordBuffer = CommonUtil.getFloatBuffer(textureCoord)
    }


    fun getTextureId(): IntArray? {
        return textureId
    }

    fun setTextureId(textureId: IntArray?) {
        this.textureId = textureId
    }

    open fun getOutputTextureId(): IntArray? {
        return null
    }

    open fun onSurfaceCreated(){
        program = initProgram()
        initAttribLocations()
    }

    open fun onSurfaceChanged(width: Int, height: Int){
        this.width = width
        this.height = height
    }

    open fun onDraw(){
        setViewPort()
        useProgram()
        setExtend()
        bindTexture()
        enableVertexAttribs()
        clear()
        draw()
        disableVertexAttribs()
    }

    open fun initProgram(): Int {
        return Gl2Utils.createGlProgramByRes(res,
            "filter/texture_vertex_shader.sh",
            "filter/texture_fragtment_shader.sh"
        )
    }

    open fun initAttribLocations() {
        hVertex = GLES20.glGetAttribLocation(program, VERTEX_ATTRIB_POSITION)
        hMatrix = GLES20.glGetUniformLocation(program, UNIFORM_MATRIX)
        hTextureCoord =
            GLES20.glGetAttribLocation(program, VERTEX_ATTRIB_TEXTURE_POSITION)
        hTexture = GLES20.glGetUniformLocation(program, UNIFORM_TEXTURE)
    }

    fun setViewPort() {
        GLES20.glViewport(0, 0, width, height)
    }

    fun clear() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
    }

    fun useProgram() {
        GLES20.glUseProgram(program)
    }

    open fun setExtend() {
        GLES20.glUniformMatrix4fv(hMatrix, 1, false, getMatrix(), 0)
    }

    open fun bindTexture(){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getTextureId()!![0])
        GLES20.glUniform1i(hTexture, 0)
    }

    fun enableVertexAttribs() {
        GLES20.glEnableVertexAttribArray(hVertex)
        GLES20.glEnableVertexAttribArray(hTextureCoord)
        GLES20.glVertexAttribPointer(
            hVertex,
            VERTEX_ATTRIB_POSITION_SIZE,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer!!
        )
        GLES20.glVertexAttribPointer(
            hTextureCoord,
            VERTEX_ATTRIB_TEXTURE_POSITION_SIZE,
            GLES20.GL_FLOAT,
            false,
            0,
            textureCoordBuffer!!
        )
    }

    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertex.size / 3)
    }

    fun disableVertexAttribs() {
        GLES20.glDisableVertexAttribArray(hVertex)
        GLES20.glDisableVertexAttribArray(hTextureCoord)
    }


}