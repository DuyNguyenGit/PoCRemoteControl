package com.samsung.android.poc.remotecontroller.presentation

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.samsung.android.poc.remotecontroller.MainInjector
import com.samsung.android.poc.remotecontroller.R
import com.samsung.android.poc.remotecontroller.databinding.ActivityMainBinding

const val deviceName = "poc_remotecontroller"

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: RCViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        val factory = RCViewModelFactory(
            this,
            MainInjector.getMsfDiscoveryUseCase(this, deviceName)
        )
        viewModel = ViewModelProviders.of(this, factory).get(RCViewModel::class.java)
        binding.viewmodel = viewModel
        binding.btnConnect.setOnClickListener {
            if (TextUtils.isEmpty(binding.edtDevice.text)) {
                Toast.makeText(this, resources.getString(R.string.empty_name), Toast.LENGTH_LONG).show()
                binding.edtDevice.requestFocus()
            } else {
                viewModel.findAndConnect(binding.edtDevice.text.toString())
            }
        }
    }

}