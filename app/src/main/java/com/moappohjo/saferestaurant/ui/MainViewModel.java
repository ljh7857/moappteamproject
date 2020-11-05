package com.moappohjo.saferestaurant.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.moappohjo.saferestaurant.ui.helper.CardViewItem;
import com.moappohjo.saferestaurant.ui.helper.ListLiveData;
import com.naver.maps.map.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends androidx.lifecycle.ViewModel {
    ListLiveData<CardViewItem> items = new ListLiveData<>();
    ListLiveData<Marker> markers = new ListLiveData<>();
}
