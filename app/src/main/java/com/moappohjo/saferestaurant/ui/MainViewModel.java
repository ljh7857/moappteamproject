package com.moappohjo.saferestaurant.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.MutableLiveData;

import com.moappohjo.saferestaurant.ui.map.MapFragment;

import java.util.Map;

public class MainViewModel extends androidx.lifecycle.ViewModel {
    private MutableLiveData<MapFragment> mapFragment;
    private MutableLiveData<ListFragment> listFragment;

    public  void setMapFragmentData(MapFragment mapFragmentData) {
        mapFragment.setValue(mapFragmentData);
    }

    public MutableLiveData<MapFragment> getMapFragmentData() {
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
