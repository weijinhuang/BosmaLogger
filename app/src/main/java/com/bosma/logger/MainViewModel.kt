package com.bosma.logger

import android.content.Context
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData

/**
 * Created by Android Studio.
 * User: H.W.J
 * Date: 2021/8/20
 * Time: 16:21
 */


class MainViewModel : BaseObservableViewModel() {

    @Bindable
    var loggerEnable: Boolean = false
        get() {
            val sharedPreferences =
                BosmaApplication.INSTANCE.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(LOG_ENABLE, false)
        }
        set(value) {
            val sharedPreferences =
                BosmaApplication.INSTANCE.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(LOG_ENABLE, value).apply()
            loggerEnableMD.postValue(value)
            field = value
        }

    val loggerEnableMD = MutableLiveData(loggerEnable)
}