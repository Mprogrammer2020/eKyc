package com.library.ekycnetset

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.library.ekycnetset.base.BaseActivity
import com.library.ekycnetset.base.Constants
import com.library.ekycnetset.databinding.ActivityEKycBinding
import com.library.ekycnetset.fragment.WelcomeVerificationFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

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

        viewDataBinding.toolbarLeftMain.setOnClickListener {
            onBackPressed()
        }

//        getInfoApi()
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

    private fun getInfoApi() {

        showLoading()

        disposable.add(
            apiService.getInfo("8d42a6fb-4e67-4a9a-a15f-b179996c1fcd",kycPref.getApiKey(this)!!,"da5fb7066098256ab5b1896bf84f91b4c4464ea77e96d57c8bb1ae6e5dba3803")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Any>() {

                    override fun onSuccess(model: Any) {
                        hideLoading()

                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                    }
                })
        )

    }

}