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


    fun storeHash(context: Context, hash: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString("HASH", hash)
        editor.apply()
    }

    fun getHash(context: Context): String? {
        return getSharedPreferences(context).getString("HASH", null)
    }

    fun storeUserId(context: Context, userId: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt("USER_ID", userId)
        editor.apply()
    }

    fun getUserId(context: Context): Int? {
        return getSharedPreferences(context).getInt("USER_ID", 0)
    }

    fun clearPrefs(context: Context){
        val sharedPrefs = context.getSharedPreferences("EKYC_PREF", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
    }

    fun storeUserAppInfo(context: Context, key: String, info : String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(key, info)
        editor.apply()
    }

    fun getUserAppInfo(context: Context, key: String): String? {
        return getSharedPreferences(context).getString(key, null)
    }

}