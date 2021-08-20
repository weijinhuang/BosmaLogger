package com.bosma.logger

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel

/**
 * Created by Android Studio.
 * User: H.W.J
 * Date: 2021/8/20
 * Time: 16:28
 */
open class BaseObservableViewModel : ViewModel(), Observable {


    private val propertyChangeRegistry: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        propertyChangeRegistry.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        propertyChangeRegistry.remove(callback)
    }

    fun notifyChange() {
        propertyChangeRegistry.notifyCallbacks(this, 0, null)
    }


    fun notifyPropertyChanged(fieldId: Int) {
        propertyChangeRegistry.notifyCallbacks(this, fieldId, null)
    }
}