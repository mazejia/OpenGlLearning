package com.mzj.openGL.view

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView

class TextureGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: TextureGLRenderer
    init {
        setEGLContextClientVersion(2)
        renderer = TextureGLRenderer(context, "filter/half_color_vertex.sh", "filter/half_color_fragment.sh")
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY

        renderer.setImage(BitmapFactory.decodeStream(resources.assets.open("texture/fengj.png")))

        requestRender()
    }
}