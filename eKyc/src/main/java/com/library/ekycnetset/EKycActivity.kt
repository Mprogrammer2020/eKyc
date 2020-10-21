package com.library.ekycnetset

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.library.ekycnetset.base.BaseActivity
import com.library.ekycnetset.databinding.ActivityEKycBinding
import com.library.ekycnetset.fragment.WelcomeVerificationFragment

// Dependency e-KYC
// Developed by : Deepak Kumar

class EKycActivity : BaseActivity<ActivityEKycBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        displayIt(WelcomeVerificationFragment(), WelcomeVerificationFragment::class.java.canonicalName, true)

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_e_kyc
    }

    override fun setContainerLayout(): Int {
        return viewDataBinding.frameContainer.id
    }

    fun getCurrentFragment(fragment: Fragment) {
        currentFragment = fragment
    }

    fun setTitle(title : String){
        viewDataBinding.toolbarTitle.text = title
    }

}