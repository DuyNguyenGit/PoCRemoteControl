package com.samsung.android.poc.remotecontroller.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.samsung.android.poc.remotecontroller.domain.usecases.DiscoveryMsfUseCase

class RCViewModelFactory(
    private val context: Context,
    private val discoveryMsfUseCase: DiscoveryMsfUseCase
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RCViewModel::class.java)) {
            return RCViewModel(context, discoveryMsfUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}