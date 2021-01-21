package com.library.ekycnetset.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.base.AppPresenter
import com.library.ekycnetset.base.Constants
import com.library.ekycnetset.databinding.UpdateInfoLayoutBinding
import com.library.ekycnetset.model.EKycModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class UpdateFragment : EKycBaseFragment<UpdateInfoLayoutBinding>() {

    private var mAppPresenter : AppPresenter ?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAppPresenter = AppPresenter(getContainerActivity())

        viewDataBinding.addressET.setText(kycPref.getUserAppInfo(getContainerActivity(), Constants.ADDRESS))
        viewDataBinding.mobileET.setText(kycPref.getUserAppInfo(getContainerActivity(), Constants.PHONE_NUMBER))
        viewDataBinding.codeTxt.text = kycPref.getUserAppInfo(getContainerActivity(), Constants.PHONE_CODE)


        viewDataBinding.codeClick.setOnClickListener {
            mAppPresenter!!.showCountryCodeDialog(
                true,
                object : AppPresenter.OnCountrySelectionListener {

                    override fun selectedCountry(code: AppPresenter.CountryCode.Code) {

                        viewDataBinding.codeTxt.text = code.dial_code!!

                    }

                })
        }

        viewDataBinding.updateClick.setOnClickListener {
                baseCheckUpdateApi()
        }


    }

    override fun getCurrentFragment(): Fragment {
        return this
    }

    override fun setTitle(): String {
        return getString(R.string.update_info)
    }

    override fun getLayoutId(): Int {
        return R.layout.update_info_layout
    }

    private fun baseCheckUpdateApi() {

        showLoading()

        val jsonObject = JSONObject()
        jsonObject.put("key", kycPref.getApiKey(getContainerActivity())!!)

        jsonObject.put("first_name", "Deepak")
        jsonObject.put("last_name", "Kumar")
        jsonObject.put("email", "deepak@gmail.com")

        jsonObject.put("birthday_day", "13")
        jsonObject.put("birthday_month", "03")
        jsonObject.put("birthday_year", "1993")

        jsonObject.put("country_nationality", "IN")

        if (kycPref.getUserAppInfo(getContainerActivity(), Constants.PHONE_NUMBER) == viewDataBinding.mobileET.text.toString()){
            Log.e("Mobile","SKIP")
        }else{

            jsonObject.put(
                "phone",
                "${viewDataBinding.codeTxt.text}${viewDataBinding.mobileET.text}"
            )

        }

        jsonObject.put("address", viewDataBinding.addressET.text.toString())


        if (kycPref.getUserAppInfo(getContainerActivity(),Constants.BASIS_USER_HASH).isNullOrEmpty()){
            Log.e("BASIS USER HASH","PLS. NOT USE")
        }else{
            jsonObject.put("user_hash", kycPref.getUserAppInfo(getContainerActivity(),Constants.BASIS_USER_HASH)!!)
        }

        if (kycPref.getUserAppInfo(getContainerActivity(),Constants.BASIS_USER_ID).isNullOrEmpty()){
            Log.e("BASIS USER ID","PLS. NOT USE")
        }else{
            jsonObject.put("check_id", kycPref.getUserAppInfo(getContainerActivity(),Constants.BASIS_USER_ID)!!)
        }

        Log.e("Check Base", jsonObject.toString())

        val requestBody =
            jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        disposable.add(
            apiService.baseCheck(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<EKycModel.BaseCheck>() {

                    override fun onSuccess(model: EKycModel.BaseCheck) {
                        hideLoading()

                        if (model.status!! == "bad") {
                            showToast("Server Error")
                        } else {
                            kycPref.storeHash(getContainerActivity(), model.user_hash!!)
                            kycPref.storeUserId(getContainerActivity(), model.user_id!!)


                            if (kycPref.getUserAppInfo(getContainerActivity(), Constants.PHONE_NUMBER) == viewDataBinding.mobileET.text.toString()){

                                Log.e("Mobile","SKIP")
                                getContainerActivity().setResultOk()

                            }else{

                                goToMobVerification()

                            }
                        }

                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        showError(e, getContainerActivity())
                    }
                })
        )

    }

    private fun goToMobVerification() {
        val bundle = Bundle()
        bundle.putString("CODE", viewDataBinding.codeTxt.text.toString())
        bundle.putString("MOB", viewDataBinding.mobileET.text.toString())
        displayIt(setArguments(MobileVerificationFragment(), bundle), MobileVerificationFragment::class.java.canonicalName, true)
    }

}
