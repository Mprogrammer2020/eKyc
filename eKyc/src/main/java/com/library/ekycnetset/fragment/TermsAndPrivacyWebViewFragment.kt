package com.application.efx.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.base.Constants
import com.library.ekycnetset.databinding.LayoutWebViewBinding

class TermsAndPrivacyWebViewFragment(var title: String) : EKycBaseFragment<LayoutWebViewBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.layout_web_view
    }

    override fun getCurrentFragment(): Fragment {
        return this
    }

    override fun setTitle(): String {
       return title
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getContainerActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        WebView.setWebContentsDebuggingEnabled(true)

        viewDataBinding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadsImagesAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
        }

        viewDataBinding.webView.webViewClient = MyWebClient(viewDataBinding.spinLoader)

        if (title.equals(getString(R.string.terms_conditions), true)) {
            viewDataBinding.webView.loadUrl(Constants.TERMS_AND_CONDITIONS)
        } else {
            viewDataBinding.webView.loadUrl(Constants.PRIVACY_POLICY)
        }
    }

    fun onBackPress() {
        val intent = Intent()
        intent.action = "broadcast"
        intent.putExtra("data", "backPress")
        LocalBroadcastManager.getInstance(getContainerActivity()).sendBroadcast(intent)
        getContainerActivity().supportFragmentManager.popBackStack()
    }

    inner class MyWebClient(val proBar: ProgressBar) : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            Log.e("Web Request", request.toString())
            return true
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Log.e("Success URl", url)
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            Log.e("URl onPageFinished", url)
            proBar.visibility = View.GONE
        }
    }

}