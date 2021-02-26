package com.application.bubble.view

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.library.ekycnetset.view.SimpleSpinnerAdapter

class SpinnerAction(activity : AppCompatActivity, spinner : Spinner, et : TextView, data : ArrayList<String>) {

    init {

        val mAdapter = SimpleSpinnerAdapter(activity, data)
        spinner.adapter = mAdapter

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {

//                    if (isMandatory){
//
//                        if (position == 0){
//                            Toast.makeText(activity,"Please select gender",Toast.LENGTH_SHORT).show()
//                        }else{
//                            et.text = data[position]
//                        }
//
//                    }

                    et.text = data[position]


                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Log.e("onNothingSelected", "It's Calling...")
                }
            }

    }

}