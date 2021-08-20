package com.bosma.logger

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bosma.logger.databinding.ActivityMainBinding
import java.lang.Exception


const val REQUEST_FLOAT_VIEW = 99
const val REQUEST_OVER_LAYER = 98

class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding? = null

    private val mViewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        mBinding?.viewModel = mViewModel
        mViewModel.loggerEnableMD.observe(this) {
            Log.d(javaClass.simpleName, "Status:$it")
            checkFloatViewPermission()
        }
    }

    private fun checkFloatViewPermission() {
        val checkSelfPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_FLOAT_VIEW
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_FLOAT_VIEW -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkOverLayerPermission()
                } else {
                    Toast.makeText(this, "please enable float permission", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun checkOverLayerPermission() {
        if (!Settings.canDrawOverlays(this)) {
            startActivityForResult(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package" + getPackageName(this))
                ), REQUEST_OVER_LAYER
            )
        } else {
            startLoggerService()
        }
    }

    private fun startLoggerService() {
        startService(Intent(this, LoggerService::class.java))
    }

    /**
     * 获取包名
     *
     * @param context 上下文
     * @return 包名
     */
    fun getPackageName(context: Context): String? {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(
                context.packageName, 0
            )
            return packageInfo.packageName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}