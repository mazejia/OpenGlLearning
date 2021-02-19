package com.mzj.openGL

import android.app.Activity
import android.os.Bundle
import com.mzj.openGL.view.MyGLSurfaceView
import com.mzj.openGL.view.TextureGLSurfaceView

/**
 * 纹理相关
 */
class TextureActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var surfaceView = TextureGLSurfaceView(this)
        setContentView(surfaceView)
    }
}