package com.library.ekycnetset.base.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RecyclerViewGenricAdapter<T, VM extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerViewGenricAdapter.RecyclerViewHolder> {

    private ArrayList<T> items;
    private int layoutId;
    private RecyclerCallback<VM, T> bindingInterface;


    public RecyclerViewGenricAdapter(ArrayList<T> items, int layoutId, RecyclerCallback<VM, T> bindingInterface) {
        this.items = items;
        this.layoutId = layoutId;
        this.bindingInterface = bindingInterface;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        VM binding;

        public RecyclerViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        public void bindData(T model, final int pos) {
            bindingInterface.bindData(binding, model, pos, itemView);
        }
    }

    @NotNull
    @Override
    public RecyclerViewGenricAdapter.RecyclerViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new RecyclerViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NotNull RecyclerViewGenricAdapter.RecyclerViewHolder holder, final int position) {
        T item = items.get(position);
        holder.itemView.setTag(position);
        holder.bindData(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

//    public int getItemViewType(int position) {
//        return position;
//    }

    public  void upDateAdapter(){
        notifyDataSetChanged();
    }
}
