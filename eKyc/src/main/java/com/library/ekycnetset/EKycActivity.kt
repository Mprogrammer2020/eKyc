package com.library.ekycnetset

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.library.ekycnetset.base.BaseActivity
import com.library.ekycnetset.base.Constants
import com.library.ekycnetset.databinding.ActivityEKycBinding
import com.library.ekycnetset.fragment.UpdateFragment
import com.library.ekycnetset.fragment.WelcomeVerificationFragment

// Dependency e-KYC
// Developed by : Deepak Kumar

class EKycActivity : BaseActivity<ActivityEKycBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        kycPref.clearPrefs(this)

        val bundle = intent.extras

        if (bundle != null){

            if (bundle.containsKey(Constants.API_KEY)){

                if (bundle.getString(Constants.API_KEY).isNullOrEmpty()){
                    showToast("BasisID Api key is missing.")
                    setResultCancelled()
                }else{
                    kycPref.storeApiKey(this,bundle.getString(Constants.API_KEY)!!)

                    kycPref.storeUserAppInfo(this,Constants.F_NAME,bundle.getString(Constants.F_NAME)!!)
                    kycPref.storeUserAppInfo(this,Constants.L_NAME,bundle.getString(Constants.L_NAME)!!)
                    kycPref.storeUserAppInfo(this,Constants.EMAIL,bundle.getString(Constants.EMAIL)!!)
                    kycPref.storeUserAppInfo(this,Constants.PHONE_NUMBER,bundle.getString(Constants.PHONE_NUMBER)!!)
                    kycPref.storeUserAppInfo(this,Constants.PHONE_CODE,bundle.getString(Constants.PHONE_CODE)!!)
                    kycPref.storeUserAppInfo(this,Constants.ADDRESS,bundle.getString(Constants.ADDRESS)!!)

                    if (bundle.getString(Constants.BASIS_USER_HASH).isNullOrEmpty()){
                        Log.e("BASIS USER HASH","NOT AVAILABLE")
                    }else{
                        kycPref.storeUserAppInfo(this,Constants.BASIS_USER_HASH,bundle.getString(Constants.BASIS_USER_HASH)!!)
                    }

                    if (bundle.getString(Constants.BASIS_USER_ID).isNullOrEmpty()){
                        Log.e("BASIS USER ID","NOT AVAILABLE")
                    }else{
                        kycPref.storeUserAppInfo(this,Constants.BASIS_USER_ID,bundle.getString(Constants.BASIS_USER_ID)!!)
                    }


                }
            }else{
                showToast("BasisID Api key is missing.")
                setResultCancelled()
            }

        }else{
            showToast("BasisID Api key is missing.")
            setResultCancelled()
        }

        if (kycPref.getUserAppInfo(this,Constants.BASIS_USER_HASH).isNullOrEmpty()){
            displayIt(WelcomeVerificationFragment(), WelcomeVerificationFragment::class.java.canonicalName, true)
        }else{
            displayIt(UpdateFragment(), UpdateFragment::class.java.canonicalName, true)
        }


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
        intent.putExtra(Constants.BASIS_USER_HASH,kycPref.getHash(this)!!)
        intent.putExtra(Constants.BASIS_USER_ID,kycPref.getUserId(this)!!)
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