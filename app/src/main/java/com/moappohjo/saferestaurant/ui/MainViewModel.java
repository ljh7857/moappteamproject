package com.moappohjo.saferestaurant.ui;

import android.view.View;

import androidx.fragment.app.ListFragment;
import androidx.lifecycle.MutableLiveData;

import com.moappohjo.saferestaurant.ui.map.MapViewFragment;
import com.naver.maps.map.MapFragment;

public class MainViewModel extends androidx.lifecycle.ViewModel {
    private MutableLiveData<MapFragment> mapView;
    private MutableLiveData<ListFragment> listFragment;

    public  void setMapViewData(MapFragment mapViewData) {
        mapView.setValue(mapViewData);
    }

    public MutableLiveData<MapFragment> getMapViewData() {
        if (mapView == null) {
            mapView = new MutableLiveData<>();
        }
        return mapView;
    }

    public  void setListFragmentData(ListFragment listFragmentData) {
        listFragment.setValue(listFragmentData);
    }

    public MutableLiveData<ListFragment> getListFragmentData() {
        if (listFragment == null) {
            listFragment = new MutableLiveData<>();
        }
        return listFragment;
    }
}
