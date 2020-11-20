package com.moappohjo.saferestaurant.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.moappohjo.saferestaurant.pd.model.Restaurant;
import com.moappohjo.saferestaurant.ui.helper.ListLiveData;
import com.moappohjo.saferestaurant.ui.views.MainActivity;
import com.naver.maps.geometry.LatLng;
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

    public void refresh()
    {
        //flush Listlivedata
        items.clear();
        markers.clear();
        //set, 메인액티비티의 메소드를 재활용??
        ((MainActivity)MainActivity.mContext).setMarkers();
        ((MainActivity)MainActivity.mContext).setRecyclerView();
    }

    public void search(String str, String opt)
    {
        //주소나 업종 등 검색옵션을 opt로 받고 검색str을 DB와 비교할 예정
        //opt는 DB의 키 값으로 사용, ui에서 스피너뷰로 받으면??
    }
    //현위치 받기
    public LatLng getMyLocation()
    {
        double lat=0;
        double lng=0;
        //여기에 구현

        LatLng mLocation = new LatLng(lat,lng);
        return mLocation;
    }
}
