package com.library.ekycnetset

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.library.ekycnetset.fragment.TermsAndPrivacyWebViewFragment
import com.library.ekycnetset.base.BaseActivity
import com.library.ekycnetset.base.Constants
import com.library.ekycnetset.databinding.ActivityEKycBinding
import com.library.ekycnetset.fragment.UpdateFragment
import com.library.ekycnetset.fragment.WelcomeVerificationFragment
import com.library.ekycnetset.model.Data

// Dependency e-KYC
// Developed by : Deepak Kumar

class EKycActivity : BaseActivity<ActivityEKycBinding>() {

    private var adminSettingsList = ArrayList<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        kycPref.clearPrefs(this)

        val bundle = intent.extras
        if (bundle != null){
            if (bundle.containsKey(Constants.USER_ID)){
                if (bundle.getString(Constants.USER_ID).isNullOrEmpty()){
                    showToast("User id key is missing.")
                    setResultCancelled()
                } else {
                    kycPref.storeAuthKey(this,bundle.getString(Constants.USER_AUTH_TOKEN)!!)

                    kycPref.storeUserAppInfo(this,Constants.USER_ID,bundle.getString(Constants.USER_ID)?: "")
                    kycPref.storeUserAppInfo(this,Constants.F_NAME,bundle.getString(Constants.F_NAME)?: "")
                    kycPref.storeUserAppInfo(this,Constants.L_NAME,bundle.getString(Constants.L_NAME)?: "")
                    kycPref.storeUserAppInfo(this,Constants.EMAIL,bundle.getString(Constants.EMAIL)?: "")
                    kycPref.storeUserAppInfo(this,Constants.PHONE_NUMBER,bundle.getString(Constants.PHONE_NUMBER)?: "")
                    kycPref.storeUserAppInfo(this,Constants.PHONE_CODE,bundle.getString(Constants.PHONE_CODE)?: "")
                    kycPref.storeUserAppInfo(this,Constants.ADDRESS,bundle.getString(Constants.ADDRESS)?: "")
                    kycPref.storeUserAppInfo(this,Constants.DOB,bundle.getString(Constants.DOB)?: "")
                    kycPref.storeUserAppInfo(this,Constants.NATIONALITY,bundle.getString(Constants.NATIONALITY)?: "")


                    adminSettingsList = bundle.getSerializable(Constants.ADMIN_SETTINGS_LIST) as ArrayList<Data>

//                    if (bundle.getString(Constants.BASIS_USER_HASH).isNullOrEmpty()){
//                        Log.e("BASIS USER HASH","NOT AVAILABLE")
//                    }else{
//                        kycPref.storeUserAppInfo(this,Constants.BASIS_USER_HASH,bundle.getString(Constants.BASIS_USER_HASH)!!)
//                    }
//
//                    if (bundle.getString(Constants.BASIS_USER_ID).isNullOrEmpty()){
//                        Log.e("BASIS USER ID","NOT AVAILABLE")
//                    } else{
//                        kycPref.storeUserAppInfo(this,Constants.BASIS_USER_ID,bundle.getString(Constants.BASIS_USER_ID)!!)
//                    }
                }
            } else{
                showToast("User id key is missing.")
                setResultCancelled()
            }
        } else{
            showToast("User id key is missing.")
            setResultCancelled()
        }

        displayIt(WelcomeVerificationFragment(), WelcomeVerificationFragment::class.java.canonicalName, true)
//        if (kycPref.getUserAppInfo(this,Constants.BASIS_USER_HASH).isNullOrEmpty()){
//            displayIt(WelcomeVerificationFragment(), WelcomeVerificationFragment::class.java.canonicalName, true)
//        }else{
//            displayIt(WelcomeVerificationFragment(), UpdateFragment::class.java.canonicalName, true)
//        }


//        displayIt(TakeSelfieFragment(), TakeSelfieFragment::class.java.canonicalName, true)

        viewDataBinding.toolbarLeftMain.setOnClickListener {
            onBackPressed()
        }
    }

//    @RequiresPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//    @RequiresApi(Build.VERSION_CODES.R)
//    fun requestFullStorageAccess(frag : WelcomeVerificationFragment) {
//        frag.startActivityForResult(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION),5000)
//    }

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
//        intent.putExtra(Constants.BASIS_USER_HASH,kycPref.getHash(this)!!)
//        intent.putExtra(Constants.BASIS_USER_ID,kycPref.getUserId(this)!!)
//
        if (kycPref.getUserAppInfo(this,Constants.CHECK_ONE) !=null ){
            intent.putExtra(Constants.CHECK_ONE,kycPref.getUserAppInfo(this,Constants.CHECK_ONE)!!)
            intent.putExtra(Constants.CHECK_TWO,kycPref.getUserAppInfo(this,Constants.CHECK_TWO)!!)
        }

        setResult(RESULT_OK, intent)
        this.finish()
    }

    fun getAdminSettings() : ArrayList<Data>{
        return adminSettingsList
    }

    override fun onBackPressed() {
        if (currentFragment is WelcomeVerificationFragment) {
            setResultCancelled()
        } else if (currentFragment is TermsAndPrivacyWebViewFragment) {
            (currentFragment as TermsAndPrivacyWebViewFragment).onBackPress()
        } else {
            super.onBackPressed()
        }

    }
}