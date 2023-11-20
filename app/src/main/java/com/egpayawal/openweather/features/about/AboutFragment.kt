package com.egpayawal.openweather.features.about

import android.os.Bundle
import android.view.View
import com.egpayawal.common.base.BaseFragment
import com.egpayawal.openweather.BuildConfig
import com.egpayawal.openweather.R
import com.egpayawal.openweather.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_about

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtVersion.text =
            getString(R.string.version_format, BuildConfig.VERSION_NAME)
    }
}
