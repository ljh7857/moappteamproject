package com.moappohjo.saferestaurant.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;


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