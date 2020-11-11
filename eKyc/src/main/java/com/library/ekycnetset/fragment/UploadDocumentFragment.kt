package com.library.ekycnetset.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.databinding.FragmentUploadDocLayoutBinding
import com.library.ekycnetset.document.DocumentPresenter
import com.library.ekycnetset.view.RealPath
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class UploadDocumentFragment : EKycBaseFragment<FragmentUploadDocLayoutBinding>() {

    private var isP1Send = false
    private var isP2Send = false
    private var isBankStatementSend = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setGlide(R.drawable.ic_id,viewDataBinding.passportID.iconOne)
        viewDataBinding.passportID.titleTxt.text = getString(R.string.cust_id)
        viewDataBinding.passportID.sideTwo.visibility = View.VISIBLE
        viewDataBinding.passportID.uploadTxt.text = getString(R.string.upload_side_one)
        viewDataBinding.passportID.uploadSideTwoTxt.text = getString(R.string.upload_side_two)
        viewDataBinding.passportID.uploadTxt.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        viewDataBinding.passportID.uploadSideTwoTxt.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        setGlide(R.drawable.ic_bank_st,viewDataBinding.bankStatement.iconOne)
        viewDataBinding.bankStatement.titleTxt.text = getString(R.string.bank_doc)

        setGlide(R.drawable.ic_income_st,viewDataBinding.incomeStatement.iconOne)
        viewDataBinding.incomeStatement.titleTxt.text = getString(R.string.income_statement)
        viewDataBinding.incomeStatement.astTxt.visibility = View.GONE

        viewDataBinding.nextClick.setOnClickListener {
            if (isP1Send && isP2Send && isBankStatementSend)
                displayIt(TakeSelfieFragment(), TakeSelfieFragment::class.java.canonicalName, true)
            else
                showToast("Please upload mandatory documents to proceed further.")
        }

        viewDataBinding.passportID.uploadTxt.setOnClickListener {
            DocumentPresenter(getContainerActivity(), this, 1000).onScanClick()
        }

        viewDataBinding.passportID.uploadSideTwoTxt.setOnClickListener {
            DocumentPresenter(getContainerActivity(), this, 2000).onScanClick()
        }

        viewDataBinding.bankStatement.uploadTxt.setOnClickListener {
            DocumentPresenter(getContainerActivity(), this, 3000).onFilePickerClick()
        }

        viewDataBinding.incomeStatement.uploadTxt.setOnClickListener {
            DocumentPresenter(getContainerActivity(),this,4000).onFilePickerClick()
        }

    }

    override fun getCurrentFragment(): Fragment {
        return this
    }

    override fun setTitle(): String {
        return getString(R.string.upload_doc)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_upload_doc_layout
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1000 -> {
                if (resultCode == Activity.RESULT_OK) {

                    val uri: Uri = data!!.getParcelableExtra("path")!!
                    Log.e("Path",uri.path!!)
                    sendDocumentApi("1",uri.path!!)

                }
            }
            2000 -> {
                if (resultCode == Activity.RESULT_OK) {

                    val uri: Uri = data!!.getParcelableExtra("path")!!
                    Log.e("Path",uri.path!!)
                    sendDocumentApi("2",uri.path!!)

                }
            }
            3000 -> {
                if (resultCode == Activity.RESULT_OK) {

                    val fileUri = data!!.data
                    Log.e("Path", fileUri!!.path!!)

                    sendFileApi("send-statement",RealPath.getPathFromURI(getContainerActivity(),fileUri), object : OnSuccess{

                        override fun onRes() {

                            isBankStatementSend = true

                            viewDataBinding.bankStatement.uploadTxt.text = fromHtml(getString(R.string.upload_again))
                            viewDataBinding.bankStatement.uploadedTxt.visibility = View.VISIBLE
                            viewDataBinding.bankStatement.bg.background = ContextCompat.getDrawable(context!!, R.drawable.green_stroke_rect)

                        }

                    })

                }
            }
            4000 -> {
                if (resultCode == Activity.RESULT_OK){

                    val fileUri = data!!.data
                    Log.e("Path", fileUri!!.path!!)

                    sendFileApi("send-proof-income",RealPath.getPathFromURI(getContainerActivity(),fileUri), object : OnSuccess{

                        override fun onRes() {

                            viewDataBinding.incomeStatement.uploadTxt.text = fromHtml(getString(R.string.upload_again))
                            viewDataBinding.incomeStatement.uploadedTxt.visibility = View.VISIBLE
                            viewDataBinding.incomeStatement.bg.background = ContextCompat.getDrawable(context!!, R.drawable.green_stroke_rect)

                        }

                    })

                }
            }
        }
    }

    private fun sendDocumentApi(side : String, filePath : String) {

        showLoading()

        val file = File(filePath)
//        val mFile = MultipartBody.Part.createFormData("file", file.name,
//            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//        )

        val mBuilder = MultipartBody.Builder()
        mBuilder.setType(MultipartBody.FORM)
            .addFormDataPart("user_hash",kycPref.getHash(getContainerActivity())!!)
            .addFormDataPart("check_id", kycPref.getUserId(getContainerActivity())!!.toString())
            .addFormDataPart("step", side)
            .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
            .build()
        val requestBody = mBuilder.build()

        disposable.add(
            apiService.sendDocument(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Any>() {

                    override fun onSuccess(model: Any) {
                        hideLoading()

                        if (side == "1"){
                            isP1Send = true
                            viewDataBinding.passportID.uploadTxt.text = fromHtml(getString(R.string.upload_again))
                            viewDataBinding.passportID.uploadedTxt.visibility = View.VISIBLE
                            viewDataBinding.passportID.bg.background = ContextCompat.getDrawable(context!!, R.drawable.green_stroke_rect)
                        }else if (side == "2"){
                            isP2Send = true
                            viewDataBinding.passportID.uploadSideTwoTxt.text = fromHtml(getString(R.string.upload_again))
                            viewDataBinding.passportID.uploadedSideTwoTxt.visibility = View.VISIBLE
                            viewDataBinding.passportID.bg.background = ContextCompat.getDrawable(context!!, R.drawable.green_stroke_rect)
                        }

                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        showError(e, getContainerActivity())
                    }
                })
        )
    }

}

