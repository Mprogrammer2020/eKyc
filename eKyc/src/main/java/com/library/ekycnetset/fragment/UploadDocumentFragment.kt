package com.library.ekycnetset.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.databinding.FragmentUploadDocLayoutBinding
import com.library.ekycnetset.document.DocumentPresenter
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
            displayIt(TakeSelfieFragment(), TakeSelfieFragment::class.java.canonicalName, true)
        }

        viewDataBinding.passportID.uploadTxt.setOnClickListener {
            DocumentPresenter(getContainerActivity(), this, 1000).onScanClick()
        }

        viewDataBinding.passportID.uploadSideTwoTxt.setOnClickListener {
            DocumentPresenter(getContainerActivity(), this, 2000).onScanClick()
        }

        viewDataBinding.bankStatement.uploadTxt.setOnClickListener {

        }

        viewDataBinding.incomeStatement.uploadTxt.setOnClickListener {

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

        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {

                val uri: Uri = data!!.getParcelableExtra("path")!!
                Log.e("Path",uri.path!!)
                sendDocumentApi("1",uri.path!!)

            }
        }else if (requestCode == 2000){

            if (resultCode == Activity.RESULT_OK) {

                val uri: Uri = data!!.getParcelableExtra("path")!!
                Log.e("Path",uri.path!!)
                sendDocumentApi("2",uri.path!!)

            }

        }
    }

    private fun sendDocumentApi(side : String, filePath : String) {

        showLoading()

        val file = File(filePath)
        val mFile = MultipartBody.Part.createFormData("file", file.name,
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

//        val mBuilder = MultipartBody.Builder()
//        mBuilder.setType(MultipartBody.FORM)
//            .addFormDataPart("file", file.name, RequestBody.create("image/*".toMediaTypeOrNull(), file))
//            .build()
//        val requestBody = mBuilder.build()

        disposable.add(
            apiService.sendDocument("ced8648e-8d53-4203-b4b4-72aeabea157e",1523412, side, mFile)
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

