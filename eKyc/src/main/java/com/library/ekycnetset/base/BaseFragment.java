package com.library.ekycnetset.base;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.application.bubble.local.PrefUtils;
import com.application.linkodes.network.ApiService;
import com.bumptech.glide.Glide;
import org.jetbrains.annotations.NotNull;

import io.reactivex.disposables.CompositeDisposable;

// by :- Deepak Kumar
// at :- Netset Software
// in :- Java

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {

    private BaseActivity mActivity;
    private T mViewDataBinding;
    private View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mViewDataBinding == null) {
            mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
            mRootView = mViewDataBinding.getRoot();
        }

        return mRootView;
    }


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            this.mActivity = (BaseActivity) context;
        }
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    public T getViewDataBinding() {
        return mViewDataBinding;
    }

    public void showLoading() {
        mActivity.showLoading();
    }

    public void hideLoading() {
        mActivity.hideLoading();
    }

    public CompositeDisposable getDisposable() {
        return mActivity.getDisposable();
    }

    public ApiService getApiService() {
        return mActivity.getApiService();
    }

    public PrefUtils getKycPref() {
        return mActivity.getKycPref();
    }

    public void displayIt(final Fragment mFragment, final String tag, final boolean isBack) {
        mActivity.displayIt(mFragment, tag, isBack);
    }

    public Fragment setArguments(final Fragment mFragment, Bundle mBundle) {
        return mActivity.setArguments(mFragment, mBundle);
    }

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    public void showToast(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    public void setGlide(int url, ImageView img) {
        Glide.with(mActivity).load(url).into(img);
    }

    public void setGlide(Uri uri, ImageView img) {
        Glide.with(mActivity).load(uri).into(img);
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){



        if(html == null){
            // return an empty spannable if the html is null
            return new SpannableString("");
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }
}
