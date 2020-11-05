package com.moappohjo.saferestaurant.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.moappohjo.saferestaurant.R;
import com.moappohjo.saferestaurant.ui.helper.CardViewItem;
import com.moappohjo.saferestaurant.ui.helper.RecyclerViewAdapter;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SearchView searchView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("sc9032srv9")
        );
        setContentView(R.layout.activity_main);
        searchView = findViewById(R.id.search_view);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) searchView.setBackgroundColor(Color.WHITE);
                else searchView.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.naver_map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.naver_map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        List<CardViewItem> items = new ArrayList<>();
        CardViewItem[] item = new CardViewItem[7];
        item[0] = new CardViewItem("황금알보쌈정식", "대구광역시 북구 산격동 1307-24", "족발, 보쌈", "032-123-1234", R.drawable.ic_home_black_24dp);
        item[1] = new CardViewItem("황금알보쌈정식", "대구광역시 북구 산격동 1307-24", "족발, 보쌈", "032-123-1234", R.drawable.ic_home_black_24dp);
        item[2] = new CardViewItem("황금알보쌈정식", "대구광역시 북구 산격동 1307-24", "족발, 보쌈", "032-123-1234", R.drawable.ic_home_black_24dp);
        item[3] = new CardViewItem("황금알보쌈정식", "대구광역시 북구 산격동 1307-24", "족발, 보쌈", "032-123-1234", R.drawable.ic_home_black_24dp);
        item[4] = new CardViewItem("황금알보쌈정식", "대구광역시 북구 산격동 1307-24", "족발, 보쌈", "032-123-1234", R.drawable.ic_home_black_24dp);
        item[5] = new CardViewItem("황금알보쌈정식", "대구광역시 북구 산격동 1307-24", "족발, 보쌈", "032-123-1234", R.drawable.ic_home_black_24dp);
        item[6] = new CardViewItem("황금알보쌈정식", "대구광역시 북구 산격동 1307-24", "족발, 보쌈", "032-123-1234", R.drawable.ic_home_black_24dp);

        int i = 7;
        while (i --> 0) {
            items.add(item[i]);
        }

        recyclerView.setAdapter(new RecyclerViewAdapter(getApplicationContext(), items, R.layout.activity_main));

    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
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