package com.samsung.android.poc.remotecontroller.presentation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.samsung.android.poc.remotecontroller.datastore.DataStore
import com.samsung.android.poc.remotecontroller.domain.usecases.DiscoveryMsfUseCase
import io.reactivex.disposables.CompositeDisposable

class RCViewModel(
    private val context: Context,
    private val discoveryMsfUseCase: DiscoveryMsfUseCase
) : ViewModel() {
    private val TAG = RCViewModel::class.simpleName
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val compositeDisposable = CompositeDisposable()

    fun findAndConnect(deviceName: String) {
        _isLoading.value = true
        discoveryMsfUseCase.dataSource.setDeviceName(deviceName.trim())
        compositeDisposable.add(discoveryMsfUseCase.execute().subscribe({
            Log.d(TAG, "discoveryDevices: >>>success token = ${it.channel.token}")
            DataStore.saveTVToken(context, it.channel.token)
            _isLoading.value = false
            Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show()
        }, {
            Log.e(TAG, "failed to search msf service, err:${it.localizedMessage}")
            _isLoading.value = false
            Toast.makeText(context, "Cannot connect to this TV", Toast.LENGTH_LONG).show()
        }))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}