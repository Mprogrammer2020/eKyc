package com.library.ekycnetset.presenter

import android.util.Log
import com.library.ekycnetset.EKycActivity
import com.library.ekycnetset.base.Constants
import com.library.ekycnetset.databinding.FragmentDocumentSelectionBinding
import com.library.ekycnetset.fragment.DocumentTypeSelectionFragment
import com.library.ekycnetset.model.EKycModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import java.io.IOException

class DocumentSelectionPresenter(
    private val context: EKycActivity,
    private val frag: DocumentTypeSelectionFragment,
    private val viewDataBinding: FragmentDocumentSelectionBinding
) {

    init {
        viewDataBinding.passportLay.setOnClickListener {
            if (frag.cameFromScreen == "normalFlow") {
                getOnfidoSdkToken("passport")
            } else {
                baseCheckApi("passport")
            }

        }

        viewDataBinding.nationalIdLay.setOnClickListener {
            if (frag.cameFromScreen == "normalFlow") {
                getOnfidoSdkToken("nationalId")
            } else {
                baseCheckApi("nationalId")
            }
        }
    }

    fun baseCheckApi(documentType: String) {
        frag.showLoading()
        Log.e("Check Base", frag.inputtedData.toString())
        val json = JSONObject(frag.inputtedData.toString())
        if (documentType == "nationalId") {
            json.put("documentType", "national_identity_card")
        } else {
            json.put("documentType", documentType)
        }


        val requestBody =
            json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        frag.disposable.add(
            frag.apiService.uploadBasicInfo(context.kycPref.getUserAppInfo(context, Constants.USER_ID).toString(), requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<EKycModel>() {
                    override fun onSuccess(model: EKycModel) {
                        frag.hideLoading()
                        Log.e("applicant Id", model.data?.applicantId.toString())
                        frag.startDocumentVerification(model.data?.token.toString(), documentType)
                    }

                    override fun onError(e: Throwable) {
                        frag.hideLoading()
                        try {
                            val body = (e as com.jakewharton.retrofit2.adapter.rxjava2.HttpException).response()!!.errorBody()

                            val errorConverter: Converter<ResponseBody, BaseCheckPresenterUpdated.Error> = context.retrofitClient.responseBodyConverter(
                                BaseCheckPresenterUpdated.Error::class.java, arrayOfNulls<Annotation>(0))
                            val error: BaseCheckPresenterUpdated.Error = errorConverter.convert(body)!!
                            frag.showError(error.message.toString(), context)
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                        }
                    }
                })
        )

    }

    private fun getOnfidoSdkToken(documentType: String) {
        frag.showLoading()
        frag.disposable.add(
            frag.apiService.getOnfidoSdkToken(frag.kycPref.getUserAppInfo(context, Constants.USER_ID).toString(), documentType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<EKycModel>() {

                    override fun onSuccess(efx: EKycModel) {
                        frag.hideLoading()
                        frag.startDocumentVerification(efx.data?.token.toString(), documentType)
                    }

                    override fun onError(e: Throwable) {
                        frag.hideLoading()
                        frag.showError(e, context)
                    }
                })
        )
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


}