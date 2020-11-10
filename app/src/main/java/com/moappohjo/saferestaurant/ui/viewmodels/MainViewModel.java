package com.moappohjo.saferestaurant.ui.viewmodels;

import com.moappohjo.saferestaurant.pd.model.Restaurant;
import com.moappohjo.saferestaurant.ui.helper.ListLiveData;
import com.naver.maps.map.overlay.Marker;

public class MainViewModel extends androidx.lifecycle.ViewModel {
    public ListLiveData<Restaurant> items = new ListLiveData<>();
    public ListLiveData<Marker> markers = new ListLiveData<>();
}
