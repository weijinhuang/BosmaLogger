package com.bosma.logger

import android.app.Application

/**
 * Created by Android Studio.
 * User: H.W.J
 * Date: 2021/8/20
 * Time: 16:33
 */
class BosmaApplication : Application() {


    companion object {

        lateinit var INSTANCE: BosmaApplication

        @JvmStatic
        fun getInstance() = INSTANCE
    }


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}