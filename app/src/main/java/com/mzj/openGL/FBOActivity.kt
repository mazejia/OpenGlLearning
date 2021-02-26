package com.mzj.openGL

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.mzj.openGL.fbo.FBORender
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

/**
 * FBO测试
 */
class FBOActivity : Activity(),FBORender.Callback {

    private lateinit var mRender:FBORender
    private lateinit var mImage:ImageView
    private lateinit var mGLView:GLSurfaceView

    private var mBmpWidth = 0
    private var mBmpHeight = 0

    private var mImgPath:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fbo)

        mGLView = findViewById(R.id.mGLView)
        mGLView.setEGLContextClientVersion(2)
        mRender = FBORender(resources)
        mRender.setCallback(this)
        mGLView.setRenderer(mRender)
        mGLView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        mImage = findViewById(R.id.mImage)
    }

    public fun onClick(view: View){
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val selectedImage = data.data
            val filePathColumns = arrayOf(MediaStore.Images.Media.DATA)
            val c = contentResolver.query(selectedImage!!, filePathColumns, null, null, null)
            c!!.moveToFirst()
            val columnIndex = c.getColumnIndex(filePathColumns[0])
            mImgPath = c.getString(columnIndex)
            Log.e("FBOActivity", "img->$mImgPath")
            val bmp = BitmapFactory.decodeFile(mImgPath)
            mBmpWidth = bmp.width
            mBmpHeight = bmp.height
            mRender.setBitmap(bmp)
            mGLView.requestRender()
            c.close()
        }
    }

    override fun onCall(data: ByteBuffer) {
        Thread {
            val bitmap = Bitmap.createBitmap(mBmpWidth, mBmpHeight, Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(data)
            saveBitmap(bitmap)
            data.clear()
        }.start()
    }

    public fun saveBitmap(b: Bitmap){
        val path = mImgPath!!.substring(0, mImgPath!!.lastIndexOf("/" + 1))
        var folder = File(path)
        if (!folder.exists() && !folder.mkdirs()) {
            runOnUiThread {
                Toast.makeText(this@FBOActivity, "无法保存照片", Toast.LENGTH_SHORT).show()
            }
            return
        }
        val dataTake = System.currentTimeMillis()
        val jpegName = "$path$dataTake.jpg"
        try {
            val fout = FileOutputStream(jpegName)
            val bos = BufferedOutputStream(fout)
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        runOnUiThread {
            Toast.makeText(this@FBOActivity, "保存成功->$jpegName", Toast.LENGTH_SHORT).show()
            mImage.setImageBitmap(b)
        }
    }

}