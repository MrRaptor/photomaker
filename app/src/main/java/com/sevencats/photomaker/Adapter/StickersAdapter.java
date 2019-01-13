package com.sevencats.photomaker.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.sevencats.photomaker.Home;
import com.sevencats.photomaker.Interface.OnStickerSelectedListener;
import com.sevencats.photomaker.R;


import java.util.List;

public class StickersAdapter extends RecyclerView.Adapter<StickersAdapter.StickerViewHolder>   {

    private final List<Integer> stickerIds;
    private final Context context;
    private final LayoutInflater layoutInflater;
    private OnStickerSelectedListener listener;


    public StickersAdapter( @NonNull Context context, OnStickerSelectedListener listener ,@NonNull List<Integer> stickerIds) {
        this.context = context;
        this.listener = listener;
        this.stickerIds = stickerIds;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public StickersAdapter.StickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StickersAdapter.StickerViewHolder(layoutInflater.inflate(R.layout.sticker_item, parent, false));
    }

    @Override
    public void onBindViewHolder(StickersAdapter.StickerViewHolder holder,  int position) {
        holder.image.setImageDrawable(ContextCompat.getDrawable(context, getItem(position)));

    }

    @Override
    public int getItemCount() {
        return stickerIds.size();
    }

    private int getItem(int position) {
        return stickerIds.get(position);
    }




    public class StickerViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        StickerViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.sticker_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos >= 0) {
                       listener.onStickerSelected(getItem(pos));
                    }
                }
            });
        }
    }
}