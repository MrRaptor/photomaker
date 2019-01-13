package com.sevencats.photomaker.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sevencats.photomaker.R;
import com.sevencats.photomaker.ToolsTextFragment;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {

    private String[][] fonts;
    private Activity activity;
    private LayoutInflater inflater;

    public SpinnerAdapter(String[][] fonts, Activity activity) {
        this.fonts = fonts;
        this.activity = activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return fonts.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null) view = inflater.inflate(R.layout.font_spinner_item, null);
        TextView tv = view.findViewById(R.id.font_name);
        tv.setText(fonts[position][0]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            tv.setTypeface(Typeface.createFromAsset(activity.getAssets(),fonts[position][1]));
        }

        return view;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);

        return view;
    }
}
