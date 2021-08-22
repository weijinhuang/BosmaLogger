package com.bosma.logger

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Created by Android Studio.
 * User: H.W.J
 * Date: 2021/8/20
 * Time: 15:58
 */
class LoggerService : Service() {

    private val mBinder = object : IBosmaLog.Stub() {
        override fun log(msg: String?) {
        }

        override fun logEnable(): Boolean {
            val sharedPreferences =
                BosmaApplication.INSTANCE.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(LOG_ENABLE, false)
        }


    }

    override fun onCreate() {
        super.onCreate()
        FloatViewManager.init(this)
        Log.i(javaClass.simpleName, "--LoggerService onCreate--")
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(javaClass.simpleName, "--LoggerService onDestroy--")
    }
}