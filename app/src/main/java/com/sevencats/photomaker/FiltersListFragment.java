package com.sevencats.photomaker;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sevencats.photomaker.Adapter.ThumbnailAdapter;
import com.sevencats.photomaker.Interface.FiltersListFragmentListener;
import com.sevencats.photomaker.utils.BitmapUtils;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersListFragment extends Fragment implements FiltersListFragmentListener {
    RecyclerView recyclerView;
    ThumbnailAdapter adapter;
    List<ThumbnailItem> thumbnailItems;
    FiltersListFragmentListener listener;

    public void setListener(FiltersListFragmentListener listener) {
        this.listener = listener;
    }

    public FiltersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_filters_list, container, false);

        thumbnailItems = new ArrayList<>();
        adapter = new ThumbnailAdapter(thumbnailItems, this, getActivity());
        recyclerView = itemView.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        displayThumbnail(BitmapUtils.decodeBitmap(150,150,Home.path));

        return itemView;
    }

    public void displayThumbnail(final Bitmap bitmap) {
        Bitmap thumbImg;
        thumbImg = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
        if (thumbImg == null) return;
        ThumbnailsManager.clearThumbs();
        thumbnailItems.clear();

        List<Filter> filters = FilterPack.getFilterPack(getActivity());

        for (Filter filter : filters) {
            ThumbnailItem tI = new ThumbnailItem();
            tI.image = thumbImg;
            tI.filter = filter;
            tI.filterName = filter.getName();
            ThumbnailsManager.addThumb(tI);
        }

        thumbnailItems.addAll(ThumbnailsManager.processThumbs(getActivity()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFilterSelected(Filter filter) {
        if (listener != null) listener.onFilterSelected(filter);
    }
}


