package com.library.ekycnetset.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.databinding.FragmentUploadDocLayoutBinding

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class UploadDocumentFragment : EKycBaseFragment<FragmentUploadDocLayoutBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setGlide(R.drawable.ic_id,viewDataBinding.passportID.iconOne)
        viewDataBinding.passportID.titleTxt.text = getString(R.string.cust_id)

        setGlide(R.drawable.ic_bank_st,viewDataBinding.bankStatement.iconOne)
        viewDataBinding.bankStatement.titleTxt.text = getString(R.string.bank_doc)

        setGlide(R.drawable.ic_income_st,viewDataBinding.incomeStatement.iconOne)
        viewDataBinding.incomeStatement.titleTxt.text = getString(R.string.income_statement)
        viewDataBinding.incomeStatement.astTxt.visibility = View.GONE

        viewDataBinding.nextClick.setOnClickListener {
            displayIt(TakeSelfieFragment(), TakeSelfieFragment::class.java.canonicalName, true)
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


}

