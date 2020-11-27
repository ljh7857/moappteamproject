package com.moappohjo.saferestaurant.ui.viewmodels;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
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
    public asyncTask aT = new asyncTask();
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
        String si;
        if (addr == null)
            si = "대구광역시";
        else si = convertAddr(addr);

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
    public String convertAddr(Address addr)
    {
        String si = addr.getAdminArea();
        if (TextUtils.isEmpty(si))
            si = "대구광역시";
       switch (si) {
           case "Daegu":
               si = "대구광역시";
           case "Seoul":
               si = "서울특별시";
           case "Gyeongsangbuk-do" :
               si = "경상북도";
           case "Gyeongsangnam-do":
               si = "경상남도";
           case "Jeollanam-do":
               si = "전라남도";
           case "Jeollabuk-do":
               si = "전라북도";
           case "Chungcheongbuk-do":
               si = "충청북도";
           case "Chungcheongnam-do" :
               si = "충청남도";
           case "Gangwon-do" :
               si = "강원도";
           case "Jeju-do":
               si = "제주도";
           case "Incheon":
               si = "인천광역시";
           case "Gyeonggi-do":
               si = "경기도";
           case "Gwangju":
               si = "광주광역시";
           case "Daejeon":
               si = "대전광역시";
           case "Busan":
               si = "부산광역시";
           case "Ulsan" :
               si = "울산광역시";
       }
        return si;
    }

    public class asyncTask extends AsyncTask<String,Integer,Integer>
    {
        @Override
        protected Integer doInBackground(String... strings) {
            if (strings[0].equals("1"))
            {
                markers.getValue().forEach(m->{m.setMap(null);});
                refresh(Double.parseDouble(strings[1]),Double.parseDouble(strings[2]));
            }
            else if (strings[0].equals("2"))
            {
                search(strings[1],strings[2]);
            }
            return null;
        }
    }
}
