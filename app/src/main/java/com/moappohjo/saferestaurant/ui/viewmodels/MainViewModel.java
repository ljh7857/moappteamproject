package com.moappohjo.saferestaurant.ui.viewmodels;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.moappohjo.saferestaurant.dm.DataManager;
import com.moappohjo.saferestaurant.dm.myDBHelper;
import com.moappohjo.saferestaurant.pd.model.MarkerDTO;
import com.moappohjo.saferestaurant.pd.model.Restaurant;
import com.moappohjo.saferestaurant.ui.helper.ListLiveData;
import com.moappohjo.saferestaurant.ui.views.MainActivity;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.Marker;

import java.io.File;
import java.io.IOException;

public class MainViewModel extends androidx.lifecycle.ViewModel {
    public ListLiveData<Restaurant> items = new ListLiveData<>();
    public ListLiveData<Marker> markers = new ListLiveData<>();
    private MutableLiveData<Boolean> isListShowing;
    String path = "/data/data/com.moappohjo.saferestaurant/databases/groupDB";
    SQLiteDatabase db = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
    private Context context;

    public MutableLiveData<Boolean> getIsListShowing() {
        if (isListShowing == null) {
            isListShowing = new MutableLiveData<>(false);
        }
        return isListShowing;
    }

    public void setIsListShowing(boolean flag) {
        this.isListShowing.setValue(flag);
    }

    //현 위치의 위경도를 인자로 받음
    public void refresh(double lat, double lng)
    {
        //flush Listlivedata
        items.clear();
        markers.clear();
        // 위경도를 주소로 역 지오코딩
        Address addr = getFromLocation(lat,lng);
        // 주요 지명 받기(안드로이드 자체 메소드)
        String si = addr.getAdminArea();
        if(si.equals("Daegu"))
            si = "대구광역시";
        else if (si.equals("Seoul"))
            si = "서울특별시";
        //set,
        Cursor c = db.rawQuery("SELECT * FROM Restaurant WHERE SI_NM == '"+si+"';",null);
        processData(c);
    }

    public void search(String str, String opt)
    {
        //주소나 업종 등 검색옵션을 opt로 받고 검색str을 DB와 비교할 예정
        //opt는 DB의 키 값으로 사용, ui에서 스피너뷰로 받으면??
        Cursor c = db.rawQuery("SELECT * FROM Restaurant WHERE "+opt+" == '"+str+"';",null);
        processData(c);
    }
    public void processData(Cursor c)
    {
        while (c.moveToNext())
        {
            Restaurant  r = new Restaurant( //id, name, address, type, tell
                    c.getInt(0),c.getString(4),c.getString(6),
                    c.getString(9),c.getString(10));
            Marker marker = new Marker();
            marker.setZIndex(c.getInt(0));
            marker.setPosition(new LatLng(c.getDouble(11), c.getDouble(12)));
            marker.setCaptionText(c.getString(4));
            marker.setCaptionColor(Color.BLUE);
            marker.setCaptionHaloColor(Color.rgb(200,255,200));
            marker.setCaptionTextSize(18);
            items.add(r);
            markers.add(marker);

        }
        c.close();
    }
    //현위치 받기 (DataManager의 메소드에서 가져옴)
    //문제점: 구글꺼라 그런지 전부 영문으로 온다. 쿼리에 쓰려면 변환 필요
    public Address getFromLocation(double latitude, double longitude) {
        Geocoder g = new Geocoder(MainActivity.mContext);
        Address converted = null;
        try {
            converted = g.getFromLocation(latitude, longitude, 1).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
//            Log.i("gps", addresses.get(0).getLatitude()+ ", " + addresses.get(0).getLongitude());
        return converted;
    }
}
