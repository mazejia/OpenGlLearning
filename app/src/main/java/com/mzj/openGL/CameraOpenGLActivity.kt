package com.mzj.openGL

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.mzj.openGL.utils.PermissionUtils
import com.mzj.openGL.view.OpenGLCameraView

/**
 * 相机（openGL）
 */
class CameraOpenGLActivity : Activity() {

    private lateinit var mCameraView: OpenGLCameraView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionUtils.askPermission(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            10,
            initViewRunnable
        )
    }


    private var initViewRunnable = Runnable {
        setContentView(R.layout.activity_camera)
        mCameraView = findViewById(R.id.mCameraView)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(
            requestCode == 10, grantResults, initViewRunnable,
            Runnable {
                Toast.makeText(this@CameraOpenGLActivity, "没有获得必要的权限", Toast.LENGTH_SHORT).show()
                finish()
            }
        );
    }

    override fun onResume() {
        super.onResume()
        mCameraView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mCameraView.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("切换摄像头").setTitle("切换摄像头").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var name = item.title.toString()
        if(name == "切换摄像头"){
            mCameraView.switchCamera()
        }
        return super.onOptionsItemSelected(item)
    }

}