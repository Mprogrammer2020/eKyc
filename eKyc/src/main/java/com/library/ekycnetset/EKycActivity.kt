package com.library.ekycnetset

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.library.ekycnetset.base.BaseActivity
import com.library.ekycnetset.base.Constants
import com.library.ekycnetset.databinding.ActivityEKycBinding
import com.library.ekycnetset.fragment.WelcomeVerificationFragment

// Dependency e-KYC
// Developed by : Deepak Kumar

class EKycActivity : BaseActivity<ActivityEKycBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent.extras

        if (bundle != null){

            if (bundle.containsKey(Constants.API_KEY)){

                if (bundle.getString(Constants.API_KEY).isNullOrEmpty()){
                    showToast("BasisID Api key is missing.")
                    setResultCancelled()
                }else{
                    kycPref.storeApiKey(this,bundle.getString(Constants.API_KEY)!!)
                }
            }else{
                showToast("BasisID Api key is missing.")
                setResultCancelled()
            }

        }else{
            showToast("BasisID Api key is missing.")
            setResultCancelled()
        }

        displayIt(WelcomeVerificationFragment(), WelcomeVerificationFragment::class.java.canonicalName, true)
//        displayIt(TakeSelfieFragment(), TakeSelfieFragment::class.java.canonicalName, true)

        viewDataBinding.toolbarLeftMain.setOnClickListener {
            onBackPressed()
        }

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

    fun setTitle(title: String){
        viewDataBinding.toolbarTitle.text = title
    }

    fun setResultCancelled() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    fun setResultOk() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {

        if (currentFragment is WelcomeVerificationFragment)
            setResultCancelled()
        else
            super.onBackPressed()

    }
}