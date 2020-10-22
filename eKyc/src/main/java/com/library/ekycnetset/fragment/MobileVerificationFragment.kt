package com.library.ekycnetset.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.databinding.FragmentMobileVerificationLayoutBinding

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class MobileVerificationFragment : EKycBaseFragment<FragmentMobileVerificationLayoutBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun getCurrentFragment(): Fragment {
        return this
    }

    override fun setTitle(): String {
        return getString(R.string.mob_ver_two)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_mobile_verification_layout
    }


}

