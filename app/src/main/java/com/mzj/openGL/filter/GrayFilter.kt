package com.mzj.openGL.filter

import android.content.res.Resources

class GrayFilter : AFilter {

    constructor(mRes:Resources):super(mRes){
    }

    override fun onCreate() {
        createProgramByAssetsFile("shader/base_vertex.sh",
            "shader/color/gray_fragment.frag")
    }

    override fun onSizeChanged(width: Int, height: Int) {
    }
}