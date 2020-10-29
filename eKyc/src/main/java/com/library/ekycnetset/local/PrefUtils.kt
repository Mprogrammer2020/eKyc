package com.application.bubble.local

import android.content.Context
import android.content.SharedPreferences

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class PrefUtils{

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("EKYC_PREF", Context.MODE_PRIVATE)
    }

    fun storeApiKey(context: Context, apiKey: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString("API_KEY", apiKey)
        editor.apply()
    }

    fun getApiKey(context: Context): String? {
        return getSharedPreferences(context).getString("API_KEY", null)
    }

    fun clearPrefs(context: Context){
        val sharedPrefs = context.getSharedPreferences("EKYC_PREF", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
    }

}