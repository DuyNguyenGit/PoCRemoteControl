package com.samsung.android.poc.remotecontroller.presentation

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.samsung.android.poc.remotecontroller.MainInjector
import com.samsung.android.poc.remotecontroller.R
import com.samsung.android.poc.remotecontroller.data.extensions.canDrawOverlay
import com.samsung.android.poc.remotecontroller.data.extensions.goToPermissionSetting
import com.samsung.android.poc.remotecontroller.data.extensions.showAlert
import com.samsung.android.poc.remotecontroller.data.extensions.showToast
import com.samsung.android.poc.remotecontroller.databinding.ActivityMainBinding
import com.samsung.android.poc.remotecontroller.datastore.DataStore
import com.samsung.android.poc.remotecontroller.datastore.DataStore.isOpenedPermissionAlready
import com.samsung.android.poc.remotecontroller.datastore.OVERLAY_PERMISSION_ACTION

const val deviceName = "poc_remotecontroller"

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: RCViewModel
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        val factory = RCViewModelFactory(
            this,
            MainInjector.getMsfDiscoveryUseCase(this, deviceName)
        )
        viewModel = ViewModelProviders.of(this, factory).get(RCViewModel::class.java)
        binding.viewmodel = viewModel
        binding.btnConnect.setOnClickListener {
            if (canDrawOverlay) {
                validateTvName()
            } else if (isOpenedPermissionAlready(this)) {
                goToPermissionSetting(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    OVERLAY_PERMISSION_ACTION
                )
            } else {
                showAlert(R.string.title, R.string.message) {
                    DataStore.saveOpenedPermissionSetting(this, true)
                    goToPermissionSetting(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        OVERLAY_PERMISSION_ACTION
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_ACTION) {
            if (canDrawOverlay) {
                validateTvName()
            } else {
                showToast(getString(R.string.overlay_permission_not_allowed))
            }
        }
    }

    private fun validateTvName() {
        if (TextUtils.isEmpty(binding.edtDevice.text)) {
            showToast(getString(R.string.empty_name))
            binding.edtDevice.requestFocus()
        } else {
            viewModel.findAndConnect(binding.edtDevice.text.toString())
        }
    }
}