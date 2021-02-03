package com.mzj.openGL

import android.app.Activity
import android.os.Bundle
import com.mzj.openGL.view.MyGLSurfaceView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var surfaceView = MyGLSurfaceView(this)
        setContentView(surfaceView)
    }
}