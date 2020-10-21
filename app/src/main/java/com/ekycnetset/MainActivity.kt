package com.ekycnetset

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.library.ekycnetset.EKycActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        eKyc.setOnClickListener {

            val intent = Intent(this,EKycActivity::class.java)
            startActivity(intent)

        }

    }
}