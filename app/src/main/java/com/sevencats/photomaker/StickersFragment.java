package com.sevencats.photomaker;


import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sevencats.photomaker.Adapter.StickersAdapter;
import com.sevencats.photomaker.Interface.OnStickerSelectedListener;

import java.util.ArrayList;
import java.util.List;


public class StickersFragment extends BottomSheetDialogFragment {

    private static StickersFragment instance;
    private OnStickerSelectedListener listener;

    private final int[] stickerIds = {
            R.drawable.abra,
            R.drawable.bellsprout,
            R.drawable.bracelet,
            R.drawable.bullbasaur,
            R.drawable.camera,
            R.drawable.candy,
            R.drawable.caterpie,
            R.drawable.charmander,
            R.drawable.mankey,
            R.drawable.map,
            R.drawable.mega_ball,
            R.drawable.meowth,
            R.drawable.pawprints,
            R.drawable.pidgey,
            R.drawable.pikachu,
            R.drawable.pikachu_2,
            R.drawable.player,
            R.drawable.pointer,
            R.drawable.pokebag,
            R.drawable.pokeball,
            R.drawable.pokecoin,

    };

    public StickersFragment() {
        // Required empty public constructor
    }

    public void setListener(OnStickerSelectedListener listener) { this.listener = listener;}

    public static StickersFragment getInstance() {
        instance = new StickersFragment();
        return instance;
    }

    @Override
    public void onDestroyView() {
        listener.hideStickerFragment(true);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_stickers, container, false);

        RecyclerView recyclerView =  view.findViewById(R.id.stickers_recycler_view);
        GridLayoutManager glm = new GridLayoutManager(getContext(), 6);
        recyclerView.setLayoutManager(glm);

        List<Integer> stickers = new ArrayList<>(stickerIds.length);
        for (Integer id : stickerIds) {
            stickers.add(id);
        }
        StickersAdapter adapter = new StickersAdapter(getActivity(), listener,stickers);
        recyclerView.setAdapter(adapter);

        return view;
    }

}
