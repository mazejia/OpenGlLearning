package com.mzj.openGL

import android.app.Activity
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import com.mzj.openGL.utils.MatrixUtils
import com.mzj.openGL.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * openGL 光照
 */
class LightActivity : Activity() , GLSurfaceView.Renderer, CompoundButton.OnCheckedChangeListener{

    private lateinit var glView:GLSurfaceView
    private lateinit var buffer:FloatBuffer

    private val data = floatArrayOf(
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
        0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
        0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
        0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
        -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,

        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
        0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
        -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,

        -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
        -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
        -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
        -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,

        0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
        0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
        0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
        0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,

        -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
        0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
        0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
        -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
        -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,

        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
        0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
        0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
        -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
        -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f
    )

    private var glProgramId = 0
    private var glUMatrix = 0
    private var glULightPosition = 0
    private var glAPosition = 0
    private var glACoord = 0
    private var glANormal = 0
    private var glUAmbientStrength = 0
    private var glUDiffuseStrength = 0
    private var glUSpecularStrength = 0
    private var glUBaseColor = 0
    private var glULightColor = 0

    private var matrix:FloatArray? = null
    private var lambMatrix: FloatArray? = null

    private val DEFAULT_AMBIENT = 0.3f
    private val DEFAULT_DIFFUSE = 0.5f
    private val DEFAULT_SPECULAR = 0.8f

    private var ambientStrength = 0f
    private var diffuseStrength = 0f
    private var specularStrength = 0f
    private var lx:Float = 0f
    private var ly:Float = 0f
    private var lz:Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_light)

        glView = findViewById(R.id.glView)
        glView.setEGLContextClientVersion(2)
        glView.setRenderer(this)

        buffer = ByteBuffer.allocateDirect(data.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.position(0)
        buffer.put(data)

        val switchId = intArrayOf(R.id.ambient, R.id.diffuse, R.id.specular)
        for (data in switchId){
            findViewById<Switch>(data).setOnCheckedChangeListener(this)
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glProgramId = ShaderUtils.createProgram(resources, "light/light.vert", "light/light.frag")
        glAPosition = GLES20.glGetAttribLocation(glProgramId, "aPosition")
        glACoord = GLES20.glGetAttribLocation(glProgramId, "aCoord")
        glANormal = GLES20.glGetAttribLocation(glProgramId, "aNormal")
        glUMatrix = GLES20.glGetUniformLocation(glProgramId, "uMatrix")
        glULightPosition = GLES20.glGetUniformLocation(glProgramId, "uLightPosition")
        glUAmbientStrength = GLES20.glGetUniformLocation(glProgramId, "uAmbientStrength")
        glUDiffuseStrength = GLES20.glGetUniformLocation(glProgramId, "uDiffuseStrength")
        glUSpecularStrength = GLES20.glGetUniformLocation(glProgramId, "uSpecularStrength")
        glULightColor = GLES20.glGetUniformLocation(glProgramId, "uLightColor")
        glUBaseColor = GLES20.glGetUniformLocation(glProgramId, "uBaseColor")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        lx = 0f
        ly = 0.8f
        lz = -1f

        matrix = MatrixUtils.getOriginalMatrix()
        Matrix.scaleM(matrix, 0, 0.5f, 0.5f * width / height.toFloat(), 0.5f)
        lambMatrix = MatrixUtils.getOriginalMatrix()
        Matrix.translateM(lambMatrix, 0, lx, ly, lz)
        Matrix.scaleM(lambMatrix, 0, 0.09f, 0.09f * width / height, 0.09f)
    }

    override fun onDrawFrame(gl: GL10?) {
        Matrix.rotateM(matrix, 0, 2f, -1f, -1f, 1f)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        GLES20.glUseProgram(glProgramId)
        GLES20.glUniformMatrix4fv(glUMatrix, 1, false, matrix, 0)

        //环境光强度
        GLES20.glUniform1f(glUAmbientStrength, ambientStrength)
        //漫反射光强度
        GLES20.glUniform1f(glUDiffuseStrength, diffuseStrength)
        //镜面光强度
        GLES20.glUniform1f(glUSpecularStrength, specularStrength)
        //光源颜色
        GLES20.glUniform3f(glULightColor, 1.0f, 0.0f, 0.0f)
        //物体颜色
        GLES20.glUniform4f(glUBaseColor, 1.0f, 1.0f, 1.0f, 1.0f)
        //光源位置
        GLES20.glUniform3f(glULightPosition, lx, ly, lz)
        //传入顶点信息
        GLES20.glEnableVertexAttribArray(glAPosition)
        buffer.position(0)

        GLES20.glVertexAttribPointer(glAPosition, 3, GLES20.GL_FLOAT, false, 6 * 4, buffer)
        //传入法线信息
        GLES20.glEnableVertexAttribArray(glANormal)
        buffer.position(3)
        GLES20.glVertexAttribPointer(glANormal, 3, GLES20.GL_FLOAT, false, 6 * 4, buffer)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_FRONT)
        GLES20.glFrontFace(GLES20.GL_CW)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, data.size / 6)

        //再绘制一个立方体，标记光源位置
        //再绘制一个立方体，标记光源位置
        GLES20.glUniformMatrix4fv(glUMatrix, 1, false, lambMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, data.size / 6)
        GLES20.glDisable(GLES20.GL_CULL_FACE)
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        GLES20.glDisableVertexAttribArray(glAPosition)
        GLES20.glDisableVertexAttribArray(glANormal)

    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.ambient -> ambientStrength = if (isChecked) DEFAULT_AMBIENT else 0f
            R.id.diffuse -> diffuseStrength = if (isChecked) DEFAULT_DIFFUSE else 0f
            R.id.specular -> specularStrength = if (isChecked) DEFAULT_SPECULAR else 0f
            else -> {
            }
        }
    }
}