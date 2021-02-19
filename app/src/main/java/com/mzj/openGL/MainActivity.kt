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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
    }

    private fun initView() {
        chartView = findViewById(R.id.tv_chart)
        textureView = findViewById(R.id.tv_texture)
    }

    private fun initData(){
        chartView.setOnClickListener(this)
        textureView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if(v == chartView){
            val intent = Intent(MainActivity@this,ChartActivity::class.java)
            startActivity(intent)
        } else if(v == textureView){
            val intent = Intent(MainActivity@this,TextureActivity::class.java)
            startActivity(intent)
        }
    }

}