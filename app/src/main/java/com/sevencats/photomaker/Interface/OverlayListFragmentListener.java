package com.sevencats.photomaker.Interface;

import com.zomato.photofilters.imageprocessors.Filter;

public  interface OverlayListFragmentListener {

     void onOverlaySelected(int resID);

     void onAlphaChange (int alpha, int resID);

     void onMirrorSelected(int resID);
}
