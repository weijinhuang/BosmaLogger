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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bosma.logger.databinding.ActivityMainBinding
import java.lang.Exception


private const val PERMISSION_REQUEST = 999
const val REQUEST_FLOAT_VIEW = 99
const val REQUEST_OVER_LAYER = 98
typealias PermissionCallback = (result: Boolean) -> Unit

var mPermissions: MutableList<String>? = null

private var mPermissionCallback: PermissionCallback? = null

private var mForceGrant: Boolean = false

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
            if (it) {
                checkOverLayerPermission()
            }
        }
    }

    fun checkPermission(
        forceGrant: Boolean,
        permissions: MutableList<String>,
        callback: PermissionCallback
    ) {
        mForceGrant = forceGrant
        mPermissions = permissions
        mPermissionCallback = callback
        doCheckPermission()
    }

    private fun doCheckPermission() {
        mPermissions?.let {
            val permissionIterator: MutableIterator<String> = it.iterator()
            while (permissionIterator.hasNext()) {
                val permission = permissionIterator.next()
                if (ContextCompat.checkSelfPermission(
                        this, permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    permissionIterator.remove()
                }
            }
            if (it.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, it.toTypedArray(), PERMISSION_REQUEST)
            } else {
                Log.d(javaClass.simpleName, "已获得全部权限")
                mPermissionCallback?.invoke(true)
            }
        }
    }

    private fun checkFloatViewPermission() {
        val checkSelfPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            Log.i(javaClass.simpleName, "SYSTEM_ALERT_WINDOW NOT GRANTED!")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW),
                REQUEST_FLOAT_VIEW
            )
        } else {
            Log.i(javaClass.simpleName, "SYSTEM_ALERT_WINDOW GRANTED!")
            checkOverLayerPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_OVER_LAYER->{
                if(resultCode == RESULT_OK){
                    startLoggerService()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults) {
            if (mForceGrant && result != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder(this)
                    .setTitle("权限申请")
                    .setCancelable(false)
                    .setMessage("为更好的使用此App，请赋予App相关的权限")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("好的") { baseDialog, _ ->
                        baseDialog.dismiss()
                        doCheckPermission()
                    }
                    .create()
                    .show()
                return
            }
        }
        mPermissionCallback?.invoke(true)
    }

    private fun checkOverLayerPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Log.i(javaClass.simpleName, "ACTION_MANAGE_OVERLAY_PERMISSION NOT GRANTED!")
            startActivityForResult(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + packageName)
                ), REQUEST_OVER_LAYER
            )
        } else {
            Log.i(javaClass.simpleName, "ACTION_MANAGE_OVERLAY_PERMISSION GRANTED!")
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