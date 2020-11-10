package com.moappohjo.saferestaurant.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.moappohjo.saferestaurant.pd.model.Restaurant;
import com.moappohjo.saferestaurant.ui.helper.ListLiveData;
import com.naver.maps.map.overlay.Marker;

public class MainViewModel extends androidx.lifecycle.ViewModel {
    public ListLiveData<Restaurant> items = new ListLiveData<>();
    public ListLiveData<Marker> markers = new ListLiveData<>();
    private MutableLiveData<Boolean> isListShowing;

    public MutableLiveData<Boolean> getIsListShowing() {
        if (isListShowing == null) {
            isListShowing = new MutableLiveData<>(false);
        }
        return isListShowing;
    }

    public void setIsListShowing(boolean flag) {
        this.isListShowing.setValue(flag);
    }
}
