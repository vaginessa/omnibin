package com.f0x1d.dogbin.adapter.base;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public abstract class BaseAdapter<T, R extends BaseViewHolder<T>> extends RecyclerView.Adapter<R> {

    protected List<T> mElements = Collections.emptyList();

    protected abstract R createHolder(ViewGroup parent, LayoutInflater layoutInflater);

    @NonNull
    @Override
    public R onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createHolder(parent, LayoutInflater.from(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull R holder, int position) {
        holder.bindTo(mElements.get(position));
    }

    @Override
    public int getItemCount() {
        return mElements.size();
    }

    public void setElements(List<T> elements) {
        this.mElements = elements;
        notifyDataSetChanged();
    }
}
