package com.sevencats.photomaker;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.sevencats.photomaker.Adapter.OverlayThumbnailAdapter;
import com.sevencats.photomaker.Interface.OverlayListFragmentListener;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OverlayListFragment extends Fragment implements OverlayListFragmentListener, SeekBar.OnSeekBarChangeListener {

    private RecyclerView recyclerView;
    public OverlayThumbnailAdapter adapter;
    private List<ThumbnailItem> thumbnailItems;
    private OverlayListFragmentListener listener;
    public static SeekBar seekBarAlpha;
    private LinearLayout overlayMenu, seekBarAlphaContainer;
    private int currentOverlay = 0;
    private LayoutInflater layoutInflater;
    private Handler handler;

    public void setListener(OverlayListFragmentListener listener) {
        this.listener = listener;
    }

    public OverlayListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_overlay_list, container, false);

        layoutInflater = LayoutInflater.from(getActivity());
        overlayMenu = itemView.findViewById(R.id.overlayMenu);

        List<Integer> overlayTextures = new ArrayList<>();
        overlayTextures.add(R.drawable.f1);
        overlayTextures.add(R.drawable.f2);
        overlayTextures.add(R.drawable.f3);
        overlayTextures.add(R.drawable.f4);
        overlayTextures.add(R.drawable.f5);
        overlayTextures.add(R.drawable.f6);
        overlayTextures.add(R.drawable.f7);
        overlayTextures.add(R.drawable.f8);
        overlayTextures.add(R.drawable.f9);
        overlayTextures.add(R.drawable.f10);
        overlayTextures.add(R.drawable.f11);
        overlayTextures.add(R.drawable.f12);
        overlayTextures.add(R.drawable.f13);

        thumbnailItems = new ArrayList<>();
        adapter = new OverlayThumbnailAdapter(overlayTextures, this, getActivity());
        recyclerView = itemView.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        handler = new Handler();

        return itemView;
    }

    public void applyMenu(View view) {
        ImageView alphaItem = view.findViewById(R.id.alphaMenuItem);
        ImageView flipItem = view.findViewById(R.id.flipMenuItem);
        alphaItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeMenu();
                        View alpha = layoutInflater.inflate(R.layout.alpha_seekbar_menu, overlayMenu, true);
                        applyAlphaMenu(alpha);
                    }
                }, 300);

            }
        });

        flipItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMirrorSelected(currentOverlay);
            }
        });
    }

    private void applyAlphaMenu(final View view) {
        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeMenu();
                        applyMenu(layoutInflater.inflate(R.layout.overlay_menu, overlayMenu, true));
                    }
                }, 300);
            }
        });
        seekBarAlphaContainer = view.findViewById(R.id.seekbar_alpha_container);
        seekBarAlpha = view.findViewById(R.id.seekbar_alpha);
        seekBarAlpha.setMax(255);
        seekBarAlpha.setProgress(255);
        seekBarAlpha.setOnSeekBarChangeListener(OverlayListFragment.this);
    }

    public void removeMenu() {
        overlayMenu.removeAllViews();
    }

    @Override
    public void onOverlaySelected(int resID) {
        currentOverlay = resID;
        removeMenu();
        View menu = layoutInflater.inflate(R.layout.overlay_menu, overlayMenu, true);
        applyMenu(menu);
        if (listener != null) listener.onOverlaySelected(resID);
    }

    @Override
    public void onAlphaChange(int alpha, int resID) {
        if (listener != null) {
            listener.onAlphaChange(alpha, resID);
        }
    }

    @Override
    public void onMirrorSelected(int resID) {
        if (listener != null) listener.onMirrorSelected(resID);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        listener.onAlphaChange(progress, currentOverlay);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}


