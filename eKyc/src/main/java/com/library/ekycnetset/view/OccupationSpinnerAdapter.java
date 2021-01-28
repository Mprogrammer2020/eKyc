package com.library.ekycnetset.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.library.ekycnetset.R;
import com.library.ekycnetset.model.Occupation;

import java.util.ArrayList;

public class OccupationSpinnerAdapter extends BaseAdapter {

    private LayoutInflater inflter;
    private ArrayList<Occupation.Code> codes;

    public OccupationSpinnerAdapter(Context applicationContext, ArrayList<Occupation.Code> pojo) {
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
        names.setText(codes.get(i).getName());
        return view;
    }
}