package com.library.ekycnetset.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.library.ekycnetset.R;

import java.util.ArrayList;

public class SimpleSpinnerAdapter extends BaseAdapter {

    private LayoutInflater inflter;
    private ArrayList<String> codes;

    public SimpleSpinnerAdapter(Context applicationContext, ArrayList<String> pojo) {
        this.codes = pojo;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return codes.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.simple_spinner_item_layout, null);
        TextView names = view.findViewById(R.id.item_name);
        names.setText(codes.get(i));
        return view;
    }
}