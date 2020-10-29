package com.library.ekycnetset.base

import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.library.ekycnetset.EKycActivity
import com.library.ekycnetset.R
import com.library.ekycnetset.base.adapter.RecyclerViewGenricAdapter
import com.library.ekycnetset.databinding.BottomCcLayoutBinding
import com.library.ekycnetset.databinding.ItemCountryLayoutBinding
import java.util.*

class AppPresenter(private var mActivity: EKycActivity) {


    private var countryCode: CountryCode? = null

    init {

        val inputStream = mActivity.resources.openRawResource(R.raw.countrycode)
        val jsonString = Scanner(inputStream).useDelimiter("\\A").next()
        countryCode = Gson().fromJson(jsonString, CountryCode::class.java)

    }



    class CountryCode {

        var codes: ArrayList<Code> = ArrayList()

        class Code {

            var name: String? = null
            var dial_code: String? = null
            var code: String? = null
        }

    }

    private var mAdapter: RecyclerViewGenricAdapter<CountryCode.Code, ItemCountryLayoutBinding>? = null
    private var mMainList: ArrayList<CountryCode.Code> = ArrayList()
    private var mDupList: ArrayList<CountryCode.Code> = ArrayList()

    fun showCountryCodeDialog(isNeedDialCode : Boolean, listener : OnCountrySelectionListener) {

        mMainList.clear()
        mDupList.clear()

        mMainList.addAll(this.countryCode!!.codes)
        mDupList.addAll(this.countryCode!!.codes)

        val binding = DataBindingUtil.inflate<BottomCcLayoutBinding>(
            LayoutInflater.from(mActivity),
            R.layout.bottom_cc_layout,
            null,
            false
        )
        val dialog = Dialog(mActivity, R.style.ThemeFullScreen)
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(false)

        binding.searchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                filter(editable.toString(), binding.noResFoundTxt)
            }
        })

        mAdapter = RecyclerViewGenricAdapter<CountryCode.Code, ItemCountryLayoutBinding>(
            mMainList,
            R.layout.item_country_layout
        ) { binder, model, _, itemView ->


            binder.itemName.text = model.name!!

            if (isNeedDialCode)
                binder.itemCode.text = model.dial_code!!
            else
                binder.itemCode.text = model.code!!

            itemView.setOnClickListener {

//                if (isNeedDialCode)
//                    countryCode.text = model.dial_code!!
//                else
//                    countryCode.text = model.code!!

                listener.selectedCountry(model!!)

                dialog.dismiss()

            }
        }
        val mLayoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        binding.codeRV.layoutManager = mLayoutManager
        binding.codeRV.adapter = mAdapter

        dialog.show()
    }

    interface OnCountrySelectionListener{
        fun selectedCountry(code: CountryCode.Code)
    }

    private fun filter(text: String, linearView: TextView) {
        //new array list that will hold the filtered data
        val filterdNames = ArrayList<CountryCode.Code>()

        //looping through existing elements
        for (s in mDupList) {
            //if the existing elements contains the search input
            if (s.name!!.toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH))) {
                //adding the element to filtered list
                filterdNames.add(s)
            }
        }
        //calling a method of the adapter class and passing the filtered list
        filterList(filterdNames, linearView)
    }

    private fun filterList(filterdNames: ArrayList<CountryCode.Code>, linearView: TextView) {
        mMainList.clear()
        mMainList.addAll(filterdNames)
        mAdapter!!.notifyDataSetChanged()

        if (mMainList.size == 0)
            linearView.visibility = View.VISIBLE
        else
            linearView.visibility = View.GONE
    }

}



