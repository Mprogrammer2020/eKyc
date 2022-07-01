package com.library.ekycnetset.presenter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.application.bubble.view.SpinnerAction
import com.application.bubble.view.SpinnerActionOccupation
import com.google.gson.Gson
import com.library.ekycnetset.EKycActivity
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.base.AppPresenter
import com.library.ekycnetset.base.Constants
import com.library.ekycnetset.databinding.FragmentStepOneLayoutBinding
import com.library.ekycnetset.fragment.MobileVerificationFragment
import com.library.ekycnetset.fragment.StepOneFragment
import com.library.ekycnetset.fragment.UploadDocumentFragment
import com.library.ekycnetset.model.EKycModel
import com.library.ekycnetset.model.Occupation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class BaseCheckPresenterUpdated(
    private val context: EKycActivity,
    private val frag: StepOneFragment,
    private val viewDataBinding: FragmentStepOneLayoutBinding
) {

    private var birthdayDay: String = ""
    private var birthdayMonth: String = ""
    private var birthdayYear: String = ""
    private var mAppPresenter: AppPresenter? = null
    private var countryNationality: String = ""
    private var countryResidence: String = ""

    init {
        mAppPresenter = AppPresenter(context)

        notWithSpace(viewDataBinding.threeStep.cityET)
        notWithSpace(viewDataBinding.threeStep.addressET)
        notWithSpace(viewDataBinding.threeStep.zipET)

        countryNationality = "MYS"
        countryResidence = "MYS"

        viewDataBinding.oneStep.firstNameET.setText(
            context.kycPref.getUserAppInfo(
                context,
                Constants.F_NAME
            )
        )
        viewDataBinding.oneStep.lastNameET.setText(
            context.kycPref.getUserAppInfo(
                context,
                Constants.L_NAME
            )
        )
        viewDataBinding.twoStep.emailET.setText(
            context.kycPref.getUserAppInfo(
                context,
                Constants.EMAIL
            )
        )
        viewDataBinding.twoStep.mobileET.setText(
            context.kycPref.getUserAppInfo(
                context,
                Constants.PHONE_NUMBER
            )
        )
        viewDataBinding.twoStep.codeTxt.text =
            context.kycPref.getUserAppInfo(context, Constants.PHONE_CODE)
        viewDataBinding.threeStep.addressET.setText(
            context.kycPref.getUserAppInfo(
                context,
                Constants.ADDRESS
            )
        )

        viewDataBinding.threeStep.streetET.setText(
            context.kycPref.getUserAppInfo(
                context,
                Constants.STREET
            )
        )

        viewDataBinding.threeStep.cityET.setText(
            context.kycPref.getUserAppInfo(
                context,
                Constants.CITY
            )
        )

        viewDataBinding.threeStep.zipET.setText(
            context.kycPref.getUserAppInfo(
                context,
                Constants.ZIP_CODE
            )
        )
        viewDataBinding.threeStep.countryTxt.text = context.getString(R.string.malaysia)
        viewDataBinding.threeStep.countryResTxt.text = context.getString(R.string.malaysia)


        // First Set Mandatory

//        if (context.getAdminSettings()[0].value)
//            viewDataBinding.oneStep.astFN.visibility = View.VISIBLE
//        else
//            viewDataBinding.oneStep.astFN.visibility = View.GONE
//
//        if (context.getAdminSettings()[1].value)
//            viewDataBinding.oneStep.astLN.visibility = View.VISIBLE
//        else
//            viewDataBinding.oneStep.astLN.visibility = View.GONE
//
//        if (context.getAdminSettings()[2].value)
//            viewDataBinding.oneStep.astMN.visibility = View.VISIBLE
//        else
//            viewDataBinding.oneStep.astMN.visibility = View.GONE
//
//        // Second Set Mandatory
//
//        if (context.getAdminSettings()[3].value)
//            viewDataBinding.twoStep.astEm.visibility = View.VISIBLE
//        else
//            viewDataBinding.twoStep.astEm.visibility = View.GONE
//
//        if (context.getAdminSettings()[4].value)
//            viewDataBinding.twoStep.astMN.visibility = View.VISIBLE
//        else
//            viewDataBinding.twoStep.astMN.visibility = View.GONE
//
//        if (context.getAdminSettings()[5].value)
//            viewDataBinding.twoStep.astDOB.visibility = View.VISIBLE
//        else
//            viewDataBinding.twoStep.astDOB.visibility = View.GONE
//
//        if (context.getAdminSettings()[6].value)
//            viewDataBinding.twoStep.astGen.visibility = View.VISIBLE
//        else
//            viewDataBinding.twoStep.astGen.visibility = View.GONE
//
//        if (context.getAdminSettings()[7].value)
//            viewDataBinding.twoStep.astOcc.visibility = View.VISIBLE
//        else
//            viewDataBinding.twoStep.astOcc.visibility = View.GONE
//
//        // Third Set Mandatory
//
//        if (context.getAdminSettings()[8].value)
//            viewDataBinding.threeStep.astCC.visibility = View.VISIBLE
//        else
//            viewDataBinding.threeStep.astCC.visibility = View.GONE
//
//        if (context.getAdminSettings()[9].value)
//            viewDataBinding.threeStep.astCR.visibility = View.VISIBLE
//        else
//            viewDataBinding.threeStep.astCR.visibility = View.GONE
//
//        if (context.getAdminSettings()[10].value)
//            viewDataBinding.threeStep.astAdd.visibility = View.VISIBLE
//        else
//            viewDataBinding.threeStep.astAdd.visibility = View.GONE
//
//        if (context.getAdminSettings()[11].value)
//            viewDataBinding.threeStep.astCity.visibility = View.VISIBLE
//        else
//            viewDataBinding.threeStep.astCity.visibility = View.GONE
//
//        if (context.getAdminSettings()[12].value)
//            viewDataBinding.threeStep.astZip.visibility = View.VISIBLE
//        else
//            viewDataBinding.threeStep.astZip.visibility = View.GONE



        viewDataBinding.prevClick.setOnClickListener {

            when (viewDataBinding.signUpViewFlipper.displayedChild) {

                1 -> {

                    viewDataBinding.prevClick.visibility = View.GONE
                    viewDataBinding.signUpViewFlipper.showPrevious()
                    viewDataBinding.lineOne.background = ContextCompat.getDrawable(
                        context,
                        R.color.grey
                    )
                    viewDataBinding.textTwo.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorBlack
                        )
                    )
                    viewDataBinding.stepTwoView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.circle_unselected
                    )

                }
                2 -> {
                    viewDataBinding.signUpViewFlipper.showPrevious()
                    viewDataBinding.lineTwo.background = ContextCompat.getDrawable(
                        context,
                        R.color.grey
                    )
                    viewDataBinding.textThree.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorBlack
                        )
                    )
                    viewDataBinding.stepThreeView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.circle_unselected
                    )
                }

            }

        }

        viewDataBinding.nextClick.setOnClickListener {

            when (viewDataBinding.signUpViewFlipper.displayedChild) {

                0 -> {

                    if (isDataValidForStepOne() && isMandatory) {

                        viewDataBinding.prevClick.visibility = View.VISIBLE
                        viewDataBinding.signUpViewFlipper.showNext()
                        viewDataBinding.lineOne.background = ContextCompat.getDrawable(
                            context,
                            R.color.colorEfxBlue
                        )
                        viewDataBinding.textTwo.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorEfxBlue
                            )
                        )
                        viewDataBinding.stepTwoView.background = ContextCompat.getDrawable(
                            context,
                            R.drawable.circle_selected
                        )

                    }
                }

                1 -> {

//                    if (frag.validateEditText(viewDataBinding.twoStep.emailET) &&
//                        frag.isEmailValid(viewDataBinding.twoStep.emailET) &&
//                        frag.validateEditText(viewDataBinding.twoStep.mobileET) && validateMob() && validateDOB()
//                    )
                    if (isDataValidForStepTwo() && isMandatory) {

                        viewDataBinding.signUpViewFlipper.showNext()
                        viewDataBinding.lineTwo.background = ContextCompat.getDrawable(
                            context,
                            R.color.colorEfxBlue
                        )
                        viewDataBinding.textThree.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorEfxBlue
                            )
                        )
                        viewDataBinding.stepThreeView.background = ContextCompat.getDrawable(
                            context,
                            R.drawable.circle_selected
                        )

                    }
                }
                2 -> {

                    if (isDataValidForStepThree() && isMandatory) {

                        baseCheckApi()
//                        goToMobVerification("Empty",0)

                    }

                }

            }
        }


        if (!context.kycPref.getUserAppInfo(context, Constants.DOB).isNullOrEmpty()) {

            Log.e("pre dob", context.kycPref.getUserAppInfo(context, Constants.DOB)!!)

            val preDob =
                context.kycPref.getUserAppInfo(context, Constants.DOB)!!.split("-").toTypedArray()

            birthdayDay = preDob[2]
            birthdayMonth = preDob[1]
            birthdayYear = preDob[0]

            Log.e(
                "pre dob formatted",
                setDateInFormat(context.kycPref.getUserAppInfo(context, Constants.DOB)!!)
            )
            viewDataBinding.twoStep.dob.setText(
                setDateInFormat(
                    context.kycPref.getUserAppInfo(
                        context,
                        Constants.DOB
                    )!!
                )
            )
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
        genders.add("Select")
        genders.add("Male")
        genders.add("Female")
//        genders.add("Other")

        SpinnerAction(
            context,
            viewDataBinding.twoStep.genderPicker,
            viewDataBinding.twoStep.genderTxt,
            genders
        )

        viewDataBinding.twoStep.genderClick.setOnClickListener {
            viewDataBinding.twoStep.genderPicker.performClick()
        }

        //Occupation started

        val inputStream = context.resources.openRawResource(R.raw.occupation)
        val jsonString = Scanner(inputStream).useDelimiter("\\A").next()
//        Log.e("Occupation", jsonString)
        val occupation = Gson().fromJson(jsonString, Occupation::class.java)

        SpinnerActionOccupation(
            context,
            viewDataBinding.twoStep.occPicker,
            viewDataBinding.twoStep.occTxt,
            occupation.occupations
        )

        viewDataBinding.twoStep.occClick.setOnClickListener {
            viewDataBinding.twoStep.occPicker.performClick()
        }

        //Occupation ended

        viewDataBinding.twoStep.codeClick.setOnClickListener {
            mAppPresenter!!.showCountryCodeDialog(
                true,
                object : AppPresenter.OnCountrySelectionListener {

                    override fun selectedCountry(code: AppPresenter.CountryCode.Code) {

                        viewDataBinding.twoStep.codeTxt.text = code.dial_code!!

                    }

                })
        }

        viewDataBinding.threeStep.countryClick.setOnClickListener {
            mAppPresenter!!.showCountryCodeDialog(
                false,
                object : AppPresenter.OnCountrySelectionListener {

                    override fun selectedCountry(code: AppPresenter.CountryCode.Code) {

                        viewDataBinding.threeStep.countryTxt.text = code.name!!
                        countryNationality = code.code!!

                    }

                })
        }

        // Country Res
        viewDataBinding.threeStep.countryResClick.setOnClickListener {
            mAppPresenter!!.showCountryCodeDialog(
                false,
                object : AppPresenter.OnCountrySelectionListener {

                    override fun selectedCountry(code: AppPresenter.CountryCode.Code) {

                        viewDataBinding.threeStep.countryResTxt.text = code.name!!
                        countryResidence = code.code!!

                    }

                })
        }

    }

    @Throws(ParseException::class)
    private fun setDateInFormat(dateInString: String): String {

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val outputFormatDate = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)

        val result = format.parse(dateInString)

        return outputFormatDate.format(result!!)
    }


    private var isMandatory = true

    private fun isDataValidForStepOne(): Boolean {
        isMandatory = frag.validateEditText(viewDataBinding.oneStep.firstNameET)
        if (!isMandatory) {
            return false
        }

        isMandatory = frag.validateEditText(viewDataBinding.oneStep.lastNameET)
        if (!isMandatory) {
            return false
        }

        return true
    }

    private fun isDataValidForStepTwo(): Boolean {
        isMandatory = (frag.validateEditText(viewDataBinding.twoStep.emailET) && frag.isEmailValid(
            viewDataBinding.twoStep.emailET
        ))
        if (!isMandatory) {
            return false
        }

        isMandatory = validateDOB()
        if (!isMandatory) {
            return false
        }

        return true
    }

    private fun isDataValidForStepThree(): Boolean {
        isMandatory = validateCountryC()
        if (!isMandatory) {
            return false
        }

        isMandatory = validateCountryR()
        if (!isMandatory) {
            return false
        }

        isMandatory = frag.validateEditText(viewDataBinding.threeStep.addressET)
        if (!isMandatory) {
            return false
        }

        isMandatory = frag.validateEditText(viewDataBinding.threeStep.streetET)
        if (!isMandatory) {
            return false
        }

        isMandatory = frag.validateEditText(viewDataBinding.threeStep.cityET)
        if (!isMandatory) {
            return false
        }

        isMandatory = frag.validateEditText(viewDataBinding.threeStep.zipET)
        if (!isMandatory) {
            return false
        }
        return true
    }


    private fun validateCountryC(): Boolean {

        if (viewDataBinding.threeStep.countryTxt.text.toString() == "Select Country") {
            context.showToast("Please select your country of citizenship.")
            return false
        } else
            return true

    }

    private fun validateCountryR(): Boolean {

        if (viewDataBinding.threeStep.countryResTxt.text.toString() == "Select Country") {
            context.showToast("Please select your country of residence.")
            return false
        } else
            return true

    }

    private fun validateMob(): Boolean {

        if (viewDataBinding.twoStep.mobileET.text.toString().length < 6) {
            context.showToast("Please enter valid mobile number.")
            return false
        } else
            return true
    }

    private fun validateDOB(): Boolean {

        if (birthdayYear.isEmpty()) {
            context.showToast("Please select your D.O.B.")
            return false
        } else
            return true

    }

    private fun goToMobVerification() {
        val bundle = Bundle()
        bundle.putString("CODE", viewDataBinding.twoStep.codeTxt.text.toString())
        bundle.putString("MOB", viewDataBinding.twoStep.mobileET.text.toString())
//        bundle.putString("HASH",userHash)
//        bundle.putInt("USER_ID",userId)
        context.displayIt(
            context.setArguments(MobileVerificationFragment(), bundle),
            MobileVerificationFragment::class.java.canonicalName,
            true
        )
    }

    private fun gotoDocFrag() {
        context.displayIt(
            UploadDocumentFragment(),
            UploadDocumentFragment::class.java.canonicalName,
            true
        )
    }

    private fun baseCheckApi() {
        frag.showLoading()
        val jsonObject = JSONObject()
        jsonObject.put("firstName", viewDataBinding.oneStep.firstNameET.text.toString())
        jsonObject.put("lastName", viewDataBinding.oneStep.lastNameET.text.toString())
        jsonObject.put("email", viewDataBinding.twoStep.emailET.text.toString())
        jsonObject.put("dob", frag.changeDateFormat(viewDataBinding.twoStep.dob.text.toString()))
        jsonObject.put("countryOfResidence", countryResidence)
        jsonObject.put("buildingNumber", viewDataBinding.threeStep.addressET.text.toString())
        jsonObject.put("street", viewDataBinding.threeStep.streetET.text.toString())
        jsonObject.put("town", viewDataBinding.threeStep.cityET.text.toString())
        jsonObject.put("postcode", viewDataBinding.threeStep.zipET.text.toString())
        jsonObject.put("country", countryNationality)

        Log.e("Check Base", jsonObject.toString())

        val requestBody =
            jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        frag.disposable.add(
            frag.apiService.uploadBasicInfo(context.kycPref.getUserAppInfo(context, Constants.USER_ID).toString(), requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<EKycModel>() {
                    override fun onSuccess(model: EKycModel) {
                        frag.hideLoading()
                        Log.e("applicant Id", model.data?.applicantId.toString())
                        frag.startDocumentVerification(model.data?.token.toString())
                    }

                    override fun onError(e: Throwable) {
                        frag.hideLoading()

                        val body = (e as com.jakewharton.retrofit2.adapter.rxjava2.HttpException).response()!!.errorBody()

                        val errorConverter: Converter<ResponseBody, Error> =
                            context.retrofitClient.responseBodyConverter(
                                Error::class.java, arrayOfNulls<Annotation>(0)
                            )
                        // Convert the error body into our Error type.
                        // Convert the error body into our Error type.
                        try {
                            val error: Error = errorConverter.convert(body)!!
                            Log.e("", "ERROR: " + error)
                            frag.showError(error.message.toString(), context)
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                        }
                    }
                })
        )

    }

    internal class Error {
        var error: String? = null
        var message: String? = null
    }
    fun createCheckApi(videoIdForOnfido: String?, keysToCreateCheck: String) {
        val jsonObject = JSONObject()
        jsonObject.put("reports", keysToCreateCheck)

        Log.e("Check Base", jsonObject.toString())

        val requestBody =
            jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())


        frag.showLoading()
        frag.disposable.add(
            frag.apiService.createCheckApi(context.kycPref.getUserAppInfo(context, Constants.USER_ID).toString(),
                videoIdForOnfido.toString(), requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<EKycModel>() {
                    override fun onSuccess(model: EKycModel) {
                        frag.hideLoading()
                        context.setResultOk()
                    }

                    override fun onError(e: Throwable) {
                        frag.hideLoading()
                        frag.showError(e, context)
                    }
                })
        )

    }


    private fun notWithSpace(editTextSansRegular: EditText) {
        val myWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (editTextSansRegular.text.toString()
                        .equals(" ")
                ) editTextSansRegular.setText("")
            }
        }
        editTextSansRegular.addTextChangedListener(myWatcher)
    }

}