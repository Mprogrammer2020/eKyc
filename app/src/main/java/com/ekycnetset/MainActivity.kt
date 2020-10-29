package com.ekycnetset

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.library.ekycnetset.EKycActivity
import com.library.ekycnetset.base.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eKyc.setOnClickListener {

            val intent = Intent(this,EKycActivity::class.java)

            startActivityForResult(intent,1000)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000) {

            if (resultCode == Activity.RESULT_OK) {
                Log.e("RESULT","OK")
            }

            if (resultCode == RESULT_CANCELED){
                Log.e("RESULT","CANCELED")
            }

        }

    }
}