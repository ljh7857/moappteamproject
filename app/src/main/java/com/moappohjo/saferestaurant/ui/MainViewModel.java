package com.moappohjo.saferestaurant.ui;

import com.moappohjo.saferestaurant.ui.helper.CardViewItem;
import com.moappohjo.saferestaurant.ui.helper.ListLiveData;
import com.naver.maps.map.overlay.Marker;

public class MainViewModel extends androidx.lifecycle.ViewModel {
    ListLiveData<CardViewItem> items = new ListLiveData<>();
    ListLiveData<Marker> markers = new ListLiveData<>();
}
