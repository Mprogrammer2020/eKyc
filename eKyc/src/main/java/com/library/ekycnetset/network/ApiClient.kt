package com.library.ekycnetset.network

import android.content.Context
import android.text.TextUtils
import com.application.bubble.local.PrefUtils
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.library.ekycnetset.BuildConfig
import com.library.ekycnetset.base.Constants
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class ApiClient {

    private var retrofit: Retrofit? = null
    private val requestTimeout = 60
    private var okHttpClient: OkHttpClient? = null

    fun getClient(context: Context, baseUrl: String): Retrofit {

        if (okHttpClient == null)
            initOkHttp(context)

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient!!)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    private fun initOkHttp(context: Context) {

        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(requestTimeout.toLong(), TimeUnit.SECONDS)
            .readTimeout(requestTimeout.toLong(), TimeUnit.SECONDS)
            .writeTimeout(requestTimeout.toLong(), TimeUnit.SECONDS)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        httpClient.addInterceptor(interceptor)

        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("deviceType", "Android")
                .addHeader("appVersion", BuildConfig.VERSION_NAME)

            if (!TextUtils.isEmpty(PrefUtils().getAuthKey(context))) {
                requestBuilder.addHeader("Authorization", "Bearer "+PrefUtils().getAuthKey(context)!!)
            }
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        okHttpClient = httpClient.build()
    }

}