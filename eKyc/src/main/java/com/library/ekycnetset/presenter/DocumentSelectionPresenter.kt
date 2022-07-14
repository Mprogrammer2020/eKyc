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
import retrofit2.Converter
import java.io.IOException

class DocumentSelectionPresenter(
    private val context: EKycActivity,
    private val frag: DocumentTypeSelectionFragment,
    private val viewDataBinding: FragmentDocumentSelectionBinding
) {

    init {
        viewDataBinding.passportLay.setOnClickListener {
            baseCheckApi("passport")
        }

        viewDataBinding.nationalIdLay.setOnClickListener {
            baseCheckApi("nationalId")
        }
    }

    public fun baseCheckApi(documentType: String) {
        frag.showLoading()


        Log.e("Check Base", frag.inputtedData.toString())

        val requestBody =
            frag.inputtedData.toString().toRequestBody("application/json".toMediaTypeOrNull())

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

}