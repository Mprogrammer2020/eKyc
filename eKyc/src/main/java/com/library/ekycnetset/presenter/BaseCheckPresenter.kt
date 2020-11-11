package com.library.ekycnetset.presenter

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.application.bubble.view.SpinnerAction
import com.library.ekycnetset.EKycActivity
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.base.AppPresenter
import com.library.ekycnetset.databinding.FragmentStepOneLayoutBinding
import com.library.ekycnetset.fragment.MobileVerificationFragment
import com.library.ekycnetset.fragment.StepOneFragment
import com.library.ekycnetset.model.EKycModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class BaseCheckPresenter(private val context: EKycActivity, private val frag : StepOneFragment ,private val viewDataBinding: FragmentStepOneLayoutBinding) {

    private var birthdayDay : String ?= null
    private var birthdayMonth : String ?= null
    private var birthdayYear : String ?= null
    private var mAppPresenter : AppPresenter ?= null
    private var countryNationality : String ?= null

    init {

        mAppPresenter = AppPresenter(context)

        viewDataBinding.prevClick.setOnClickListener {

            when (viewDataBinding.signUpViewFlipper.displayedChild) {

                1 -> {

                    viewDataBinding.prevClick.visibility = View.GONE
                    viewDataBinding.signUpViewFlipper.showPrevious()
                    viewDataBinding.lineOne.background = ContextCompat.getDrawable(context, R.color.grey)
                    viewDataBinding.textTwo.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                    viewDataBinding.stepTwoView.background = ContextCompat.getDrawable(context, R.drawable.circle_unselected)

                }
                2 -> {
                    viewDataBinding.signUpViewFlipper.showPrevious()
                    viewDataBinding.lineTwo.background = ContextCompat.getDrawable(context, R.color.grey)
                    viewDataBinding.textThree.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                    viewDataBinding.stepThreeView.background = ContextCompat.getDrawable(context, R.drawable.circle_unselected)
                }

            }

        }

        viewDataBinding.nextClick.setOnClickListener {

            when (viewDataBinding.signUpViewFlipper.displayedChild) {

                0 -> {

                    if (frag.validateEditText(viewDataBinding.oneStep.firstNameET) &&
                        frag.validateEditText(viewDataBinding.oneStep.lastNameET)) {

                    viewDataBinding.prevClick.visibility = View.VISIBLE
                    viewDataBinding.signUpViewFlipper.showNext()
                    viewDataBinding.lineOne.background = ContextCompat.getDrawable(context, R.color.colorEfxBlue)
                    viewDataBinding.textTwo.setTextColor(ContextCompat.getColor(context, R.color.colorEfxBlue))
                    viewDataBinding.stepTwoView.background = ContextCompat.getDrawable(context, R.drawable.circle_selected)


                    }
                }

                1 -> {

                    if (frag.validateEditText(viewDataBinding.twoStep.emailET) &&
                        frag.isEmailValid(viewDataBinding.twoStep.emailET) &&
                        frag.validateEditText(viewDataBinding.twoStep.mobileET) && validateDOB()) {

                    viewDataBinding.signUpViewFlipper.showNext()
                    viewDataBinding.lineTwo.background = ContextCompat.getDrawable(context, R.color.colorEfxBlue)
                    viewDataBinding.textThree.setTextColor(ContextCompat.getColor(context, R.color.colorEfxBlue))
                    viewDataBinding.stepThreeView.background = ContextCompat.getDrawable(context, R.drawable.circle_selected)

                    }
                }
                2 ->{

                    if (validateCountry()) {

                        baseCheckApi()
//                        goToMobVerification("Empty",0)

                    }

                }

            }
        }


        viewDataBinding.twoStep.dob.setOnClickListener {

            frag.commonDOBSelection(viewDataBinding.twoStep.dob, object :
                EKycBaseFragment.OnSelectedDOB {

                override fun selectedDate(date: String, month: String, year: Int) {

                    Log.e("D.O.B", "${date}-${month}-${year}")

                    birthdayDay = date
                    birthdayMonth = month
                    birthdayYear = year.toString()

                }

            }, true)

        }

        val genders: ArrayList<String> = ArrayList()
        genders.add("Male")
        genders.add("Female")
        genders.add("Other")

        SpinnerAction(context,viewDataBinding.twoStep.genderPicker,viewDataBinding.twoStep.genderTxt,genders)

        viewDataBinding.twoStep.genderClick.setOnClickListener {
            viewDataBinding.twoStep.genderPicker.performClick()
        }

        viewDataBinding.twoStep.codeClick.setOnClickListener {
            mAppPresenter!!.showCountryCodeDialog(true, object : AppPresenter.OnCountrySelectionListener{

                override fun selectedCountry(code: AppPresenter.CountryCode.Code) {

                    viewDataBinding.twoStep.codeTxt.text = code.dial_code!!

                }

            })
        }

        viewDataBinding.threeStep.countryClick.setOnClickListener {
            mAppPresenter!!.showCountryCodeDialog(false, object : AppPresenter.OnCountrySelectionListener{

                override fun selectedCountry(code: AppPresenter.CountryCode.Code) {

                    viewDataBinding.threeStep.countryTxt.text = code.name!!
                    countryNationality = code.code!!

                }

            })
        }

    }

    private fun validateCountry() : Boolean{

        if (viewDataBinding.threeStep.countryTxt.text.toString() == "Select Country"){
            context.showToast("Please select your country.")
            return false
        }else
            return true

    }

    private fun validateDOB() : Boolean{

        if (birthdayYear.isNullOrEmpty()){
            context.showToast("Please select your D.O.B.")
            return false
        }else
            return true

    }

    private fun goToMobVerification() {
        val bundle = Bundle()
        bundle.putString("CODE",viewDataBinding.twoStep.codeTxt.text.toString())
        bundle.putString("MOB",viewDataBinding.twoStep.mobileET.text.toString())
//        bundle.putString("HASH",userHash)
//        bundle.putInt("USER_ID",userId)
        context.displayIt(context.setArguments(MobileVerificationFragment(),bundle), MobileVerificationFragment::class.java.canonicalName, true)
    }

    private fun baseCheckApi() {

        frag.showLoading()

        var gender = "0"
        when(viewDataBinding.twoStep.genderTxt.text.toString()){

            "Male"   -> gender = "0"
            "Female" -> gender = "1"
            "Other"  -> gender = "2"

        }

        val jsonObject = JSONObject()
        jsonObject.put("key", context.kycPref.getApiKey(context)!!)
        jsonObject.put("first_name", viewDataBinding.oneStep.firstNameET.text.toString())
        jsonObject.put("last_name", viewDataBinding.oneStep.lastNameET.text.toString())
        jsonObject.put("middle_name", viewDataBinding.oneStep.middleNameET.text.toString())
        jsonObject.put("email", viewDataBinding.twoStep.emailET.text.toString())
        jsonObject.put("phone", "${viewDataBinding.twoStep.codeTxt.text}${viewDataBinding.twoStep.mobileET.text}")
        jsonObject.put("phone2", "")
        jsonObject.put("gender", gender)
        jsonObject.put("birthday_day", birthdayDay!!)
        jsonObject.put("birthday_month", birthdayMonth!!)
        jsonObject.put("birthday_year", birthdayYear!!)
        jsonObject.put("country_nationality", countryNationality!!)
        jsonObject.put("country_residence", countryNationality!!)
        jsonObject.put("city", viewDataBinding.threeStep.cityET.text.toString())
        jsonObject.put("address", viewDataBinding.threeStep.addressET.text.toString())
        jsonObject.put("zip", viewDataBinding.threeStep.zipET.text.toString())

        Log.e("Check Base", jsonObject.toString())

        val requestBody =
            jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        frag.disposable.add(
            frag.apiService.baseCheck(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<EKycModel.BaseCheck>() {

                    override fun onSuccess(model: EKycModel.BaseCheck) {
                        frag.hideLoading()

                        context.kycPref.storeHash(context,model.user_hash!!)
                        context.kycPref.storeUserId(context,model.user_id!!)

                        goToMobVerification()
                    }

                    override fun onError(e: Throwable) {
                        frag.hideLoading()
                        frag.showError(e, context)
                    }
                })
        )

    }

}