package com.moappohjo.saferestaurant.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moappohjo.saferestaurant.R;
import com.moappohjo.saferestaurant.ui.helper.CardViewItem;
import com.moappohjo.saferestaurant.ui.helper.RecyclerViewAdapter;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private NaverMap naverMap;
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private MainViewModel viewModel = new MainViewModel();
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNaverMap();
        setContentView(R.layout.activity_main);
        setSearchView();
        setRecyclerView();
        if(haveNetworkConnection(getApplicationContext())){
            //비동기 클래스를 사용해 사용자 관련 정보들을 가져온다.
            FetchItemTask ft = new FetchItemTask();
            ft.execute();
        }
        else{

        }
    }

    protected boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                haveConnectedWifi = ni.isConnected();
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                haveConnectedMobile = ni.isConnected();
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    //네트워크와 통신하는 과정이므로 비동기 방식을 사용해야 함.
    private  class FetchItemTask extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;

        protected void onPreExecute(){
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("안심식당 정보 로딩중...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
            progressDialog.show();
        }

        //백그라운드에서 실행하는 내용
        @Override
        protected String doInBackground(Void... voids) {
            String APIKEY = "0f8513fb24b87da71f5eb1594e0ac11b35b2be4afe6c06a1c543dcd9169a376f";
            String APIURL = "http://211.237.50.150:7080/openapi/"+ APIKEY +
                    "/xml/Grid_20200713000000000605_1/1/1?&RELAX_USE_YN=Y";
            //현재는 실행마다 사이트에 접속해서 업데이트를 하는 구조인데, 이후 주기적으로 업데이트하도록 수정 예정임.
            myDBHelper dbHelper = new myDBHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                //우성 길이를 하나만 가져와서 총 몇 개의 안심식당이 있는지 확인합니다.
                Document result = Jsoup.connect(APIURL).method(Connection.Method.GET).execute().parse();
                int totalCnt = Integer.parseInt(result.select("totalCnt").text());

                //한 번의 요청 당 최대 1000개의 식당을 검색할 수 있습니다.
                for(int startIndex=1; startIndex<=totalCnt; startIndex+=1000){
                    int endIndex = (startIndex+999<totalCnt)? startIndex+999: totalCnt;
                    String partURL = "http://211.237.50.150:7080/openapi/"+ APIKEY +
                            "/xml/Grid_20200713000000000605_1/"+startIndex+"/"+endIndex+"?&RELAX_USE_YN=Y";
                    //System.out.println(partURL);
                    result = Jsoup.connect(partURL).method(Connection.Method.GET).execute().parse();
                    Elements restaurantsInfo = result.select("row");
                    //System.out.println(restaurantsInfo.size());
                    for(int idx=0; idx<endIndex-startIndex; idx++){
                        Element element = restaurantsInfo.get(idx);
                        Log.i(startIndex+idx+"",element.select("RELAX_RSTRNT_NM").text());
//                        Log.i(idx+"",element.select("ROW_NUM").text());
                        String restaurantName = element.select("RELAX_RSTRNT_NM").text().replaceAll("'", "-");
//                        db.execSQL("INSERT OR REPLACE INTO Restaurant VALUES("+element.select("RELAX_SEQ").text()+", "+element.select("RELAX_ZIPCODE").text()+", " +
//                                "'"+element.select("RELAX_SI_NM").text()+"', '"+element.select("RELAX_SIDO_NM").text()+"', '"+restaurantName+"', " +
//                                "'"+element.select("RELAX_RSTRNT_REPRESENT").text()+"', '"+element.select("RELAX_ADD1").text().replaceAll("'", "")+"', '"+element.select("RELAX_ADD2").text()+"', " +
//                                "'"+element.select("RELAX_GUBUN").text()+"', '"+element.select("RELAX_GUBUN_DETAIL").text()+"', '"+element.select("RELAX_RSTRNT_TEL").text()+"', " +
//                                "'"+element.select("RELAX_RSTRNT_ETC").text()+"');");
                    }
                }
            } catch (IOException e) {
                Log.i("FAIL", "Exception occured");
                e.printStackTrace();
            }
            return "Fail";
        }

        //포그라운드에서 실행하는 내용
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        int i = 3;
        while(i-->0)
            viewModel.items.add(new CardViewItem("황금알보쌈정식", "대구광역시 북구 산격동 1307-24", "족발, 보쌈", "032-123-1234", R.drawable.ic_home_black_24dp));

        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), viewModel.items.getValue(), R.layout.activity_main);
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Toast.makeText(getApplicationContext(), "clcik" + pos, Toast.LENGTH_SHORT).show();
                viewModel.items.add(new CardViewItem("황금알보쌈정식", "대구광역시 북구 산격동 1307-24", "족발, 보쌈", "032-123-1234", R.drawable.ic_home_black_24dp));
            }
        });
        recyclerView.setAdapter(recyclerViewAdapter);
        viewModel.items.observe(this, new Observer<List<CardViewItem>>() {
            @Override
            public void onChanged(List<CardViewItem> cardViewItems) {
                recyclerViewAdapter.updateCardViewItemList(cardViewItems);
            }
        });


    }

    private void setSearchView() {
        searchView = findViewById(R.id.search_view);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) searchView.setBackgroundColor(Color.WHITE);
                else searchView.setBackgroundColor(Color.TRANSPARENT);
            }
        });

    }

    private void setNaverMap() {
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("sc9032srv9")
        );

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.naver_map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.naver_map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) {
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        naverMap.setLocationSource(locationSource);
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
    }

    public void onClickShowList(View v) {
        Button b = (Button)v;
        if (b.getText().equals(getResources().getString(R.string.show_list))) {
            recyclerView.setVisibility(View.VISIBLE);
            b.setText(getResources().getString(R.string.hide_list));
        } else {
            recyclerView.setVisibility(View.GONE);
            b.setText(getResources().getString(R.string.show_list));
        }
    }

}