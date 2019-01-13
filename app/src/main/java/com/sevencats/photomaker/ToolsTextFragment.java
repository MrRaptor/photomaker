package com.sevencats.photomaker;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.sevencats.photomaker.Interface.TextFragmentListener;

import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class ToolsTextFragment extends BottomSheetDialogFragment {

    private List<String> fontName = new ArrayList<>();
    private List<String> font = new ArrayList<>();
    private String[][] fonts =
            {{"Marck Script", "marckscript.ttf"},
                    {"Underdog", "underdog.ttf"},
                    {"Neucha","neucha.ttf"},
                    {"Pangolin","pangolin.ttf"},
                    {"BlackAndWhite","blackandwhite.ttf"}};
    private Spinner spinner;
    private SpinnerAdapter adapter;
    private Button btnAddText;
    private ImageView colorView;
    private EditText editText;
    private TextFragmentListener listener;
    private Typeface fontTypeface;
    private int defaultColor = Color.parseColor("#000000");

    public void setListener(TextFragmentListener listener) {
        this.listener = listener;
    }

    public ToolsTextFragment() {
        // Required empty public constructor
    }

    static ToolsTextFragment instance;

    public static ToolsTextFragment getInstance() {
        if (instance == null) instance = new ToolsTextFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text, container, false);

        spinner = view.findViewById(R.id.font_spinner);
        colorView = view.findViewById(R.id.color_picker);
        btnAddText = view.findViewById(R.id.btn_add_text);
        editText = view.findViewById(R.id.edt_add_text);
        adapter = new com.sevencats.photomaker.Adapter.SpinnerAdapter(fonts, getActivity());
        spinner.setAdapter(adapter);
        colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                fontTypeface = Typeface.createFromAsset(getActivity().getAssets(), fonts[position][1]);
                editText.setTypeface(fontTypeface);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddTextButtonClick(fontTypeface, editText.getText().toString(), defaultColor);
            }
        });

        return view;
    }

    private void openColorPicker() {

        final AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(getContext(), defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                colorView.setBackgroundColor(defaultColor);
                editText.setTextColor(defaultColor);
            }
        });

        colorPicker.show();
    }
}
