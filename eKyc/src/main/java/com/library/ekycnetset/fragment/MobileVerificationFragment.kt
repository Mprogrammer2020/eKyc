package com.library.ekycnetset.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.base.AppPresenter
import com.library.ekycnetset.databinding.FragmentMobileVerificationLayoutBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class MobileVerificationFragment : EKycBaseFragment<FragmentMobileVerificationLayoutBinding>() {

    private var mAppPresenter : AppPresenter ?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mAppPresenter == null){
            mAppPresenter = AppPresenter(getContainerActivity())

            viewDataBinding.codeTxt.text = arguments!!.getString("CODE")
            viewDataBinding.mobileET.setText(arguments!!.getString("MOB"))

//            viewDataBinding.codeClick.setOnClickListener {
//                mAppPresenter!!.showCountryCodeDialog(true, object : AppPresenter.OnCountrySelectionListener{
//
//                    override fun selectedCountry(code: AppPresenter.CountryCode.Code) {
//
//                        viewDataBinding.codeTxt.text = code.dial_code!!
//
//                    }
//
//                })
//            }

            viewDataBinding.nextClick.setOnClickListener {
                displayIt(UploadDocumentFragment(), UploadDocumentFragment::class.java.canonicalName, true)
            }

            viewDataBinding.getOtpClick.setOnClickListener {
                    sendSMSApi()
            }


        }
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

    private fun sendSMSApi() {

        showLoading()

        val jsonObject = JSONObject()
//        jsonObject.put("user_hash", arguments!!.getString("HASH"))
        jsonObject.put("user_hash", "60b85e15-0f56-428c-b1b8-66076a61ff94")
//        jsonObject.put("check_id", arguments!!.getInt("USER_ID"))
        jsonObject.put("check_id", 1515419)

        Log.e("Send SMS", jsonObject.toString())

        val requestBody =
            jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        disposable.add(
            apiService.sendSMS(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Any>() {

                    override fun onSuccess(model: Any) {
                        hideLoading()

                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        showError(e, getContainerActivity())
                    }
                })
        )

    }


}

