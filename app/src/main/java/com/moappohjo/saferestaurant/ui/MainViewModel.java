package com.moappohjo.saferestaurant.ui;

import androidx.fragment.app.ListFragment;
import androidx.lifecycle.MutableLiveData;

import com.moappohjo.saferestaurant.ui.map.MapViewFragment;

public class MainViewModel extends androidx.lifecycle.ViewModel {
    private MutableLiveData<MapViewFragment> mapFragment;
    private MutableLiveData<ListFragment> listFragment;

    public  void setMapFragmentData(MapViewFragment mapViewFragmentData) {
        mapFragment.setValue(mapViewFragmentData);
    }

    public MutableLiveData<MapViewFragment> getMapFragmentData() {
        if (mapFragment == null) {
            mapFragment = new MutableLiveData<>();
        }
        return mapFragment;
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
