package com.application.bubble.view

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.library.ekycnetset.model.Occupation
import com.library.ekycnetset.view.OccupationSpinnerAdapter
import com.library.ekycnetset.view.SimpleSpinnerAdapter

class SpinnerActionOccupation(activity : AppCompatActivity, spinner : Spinner, et : TextView, data : ArrayList<Occupation.Code>) {

    init {

        val mAdapter = OccupationSpinnerAdapter(activity, data)
        spinner.adapter = mAdapter

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    et.text = data[position].name
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Log.e("onNothingSelected", "It's Calling...")
                }
            }

    }

}