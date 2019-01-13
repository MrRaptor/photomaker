package com.sevencats.photomaker.Adapter;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sevencats.photomaker.Interface.FiltersListFragmentListener;
import com.sevencats.photomaker.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.MyViewHolder> {

    private List<ThumbnailItem> thumbnailItems;
    private FiltersListFragmentListener listener;
    private Context context;
    private int current_position = -1;

    public ThumbnailAdapter(List<ThumbnailItem> thumbnailItems, FiltersListFragmentListener listener, Context context) {
        this.thumbnailItems = thumbnailItems;
        this.listener = listener;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnail, border;

        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            border = itemView.findViewById(R.id.thumbnail_border);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.thumbnail_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final ThumbnailItem thumbnailItem = thumbnailItems.get(position);
        holder.thumbnail.setImageBitmap(thumbnailItem.image);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFilterSelected(thumbnailItem.filter);
                current_position = position;
                notifyDataSetChanged();
            }
        });

        if (current_position == position) {
            holder.border.post(new Runnable() {
                @Override
                public void run() {
                    holder.border.setImageResource(R.drawable.anim_border);
                    Drawable d = holder.border.getDrawable();
                    if (d instanceof AnimatedVectorDrawableCompat) {
                        AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) d;
                        avd.start();
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (d instanceof AnimatedVectorDrawable) {
                            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) d;
                            avd.start();
                        }
                    }
                }
            });
        } else {
            holder.border.setImageResource(android.R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return thumbnailItems.size();
    }

}
