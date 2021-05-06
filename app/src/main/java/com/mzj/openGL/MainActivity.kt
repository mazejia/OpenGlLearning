package com.mzj.openGL

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mzj.openGL.view.MyGLSurfaceView

class MainActivity : Activity(),View.OnClickListener {
    //绘制图形
    private lateinit var chartView: View
    //绘制纹理
    private lateinit var textureView: View
    //相机openGL
    private lateinit var cameraOpenGL:View
    //FBO
    private lateinit var fbo:View
    //光照
    private lateinit var light:View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
    }

    private fun initView() {
        chartView = findViewById(R.id.tv_chart)
        textureView = findViewById(R.id.tv_texture)
        cameraOpenGL = findViewById(R.id.tv_camera_opengl)
        fbo = findViewById(R.id.tv_fbo)
        light = findViewById(R.id.tv_light)
    }

    private fun initData(){
        chartView.setOnClickListener(this)
        textureView.setOnClickListener(this)
        cameraOpenGL.setOnClickListener(this)
        fbo.setOnClickListener(this)
        light.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v) {
            chartView -> {
                val intent = Intent(MainActivity@this,ChartActivity::class.java)
                startActivity(intent)
            }
            textureView -> {
                val intent = Intent(MainActivity@this,TextureActivity::class.java)
                startActivity(intent)
            }
            cameraOpenGL -> {
                val intent = Intent(MainActivity@this,CameraOpenGLActivity::class.java)
                startActivity(intent)
            }
            fbo -> {
                val intent = Intent(MainActivity@this,FBOActivity::class.java)
                startActivity(intent)
            }
            light -> {
                val intent = Intent(this,LightActivity::class.java)
                startActivity(intent)
            }
        }
    }

}