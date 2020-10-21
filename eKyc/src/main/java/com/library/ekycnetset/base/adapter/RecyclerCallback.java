package com.library.ekycnetset.base.adapter;

import android.view.View;

import androidx.databinding.ViewDataBinding;

public interface RecyclerCallback<VM extends ViewDataBinding, T> {
    void bindData(VM binder, T model, final int position, View itemView);
}
