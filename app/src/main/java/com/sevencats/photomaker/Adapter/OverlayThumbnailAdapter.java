package com.sevencats.photomaker.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
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

import com.sevencats.photomaker.Interface.OverlayListFragmentListener;
import com.sevencats.photomaker.R;
import com.sevencats.photomaker.utils.BitmapUtils;

import java.util.List;

public class OverlayThumbnailAdapter extends RecyclerView.Adapter<OverlayThumbnailAdapter.MyViewHolder> {

    private List<Integer> thumbnailItems;
    private OverlayListFragmentListener listener;
    private Context context;
    private int current_position = -1;

    public OverlayThumbnailAdapter(List<Integer> thumbnailItems, OverlayListFragmentListener listener, Context context) {
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
        Bitmap image = BitmapUtils.decodeSampledBitmapFromResource(context.getResources(), thumbnailItems.get(position), 100, 100);
        holder.thumbnail.setImageBitmap(image);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.onOverlaySelected(thumbnailItems.get(position));
                current_position = position;
                notifyDataSetChanged();
            }
        });

        if (current_position == position) {
            holder.border.post(new Runnable() {
                @Override
                public void run() {
                    holder.border.setVisibility(View.VISIBLE);
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
