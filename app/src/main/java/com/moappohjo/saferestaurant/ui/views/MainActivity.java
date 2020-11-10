package com.moappohjo.saferestaurant.ui.views;

import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moappohjo.saferestaurant.R;
import com.moappohjo.saferestaurant.dm.DataManager;
import com.moappohjo.saferestaurant.pd.model.Restaurant;
import com.moappohjo.saferestaurant.ui.helper.Utils;
import com.moappohjo.saferestaurant.ui.viewmodels.MainViewModel;
import com.moappohjo.saferestaurant.ui.helper.RecyclerViewAdapter;
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
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
        setMarkers();
        //사용자의 현재 위치를 얻어서 넣어주세요.
        //Address address=null;
        //DataManager dm = new DataManager(getApplicationContext(), address);
//        if(!dm.loadData()){
//
//        }
    }

    private void setMarkers() {

    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        List<Restaurant> restaurants = getRestaurantsFrom("restaurants.json");
        viewModel.items.addAll(restaurants);

        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), viewModel.items.getValue(), R.layout.activity_main);
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
               // on click
            }
        });
        recyclerView.setAdapter(recyclerViewAdapter);
        viewModel.items.observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                recyclerViewAdapter.updateCardViewItemList(restaurants);
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

    private List<Restaurant> getRestaurantsFrom(final String fileName) {
        String jsonFileString = Utils.getJsonFromAssets(getApplicationContext(), fileName);
        Gson gs = new Gson();
        Type listRestaurantType = new TypeToken<List<Restaurant>>() {}.getType();
        List<Restaurant> jsonList = gs.fromJson(jsonFileString, listRestaurantType);
        List<Restaurant> restaurants = jsonList.stream().map((r) -> {
            r.image = R.drawable.ic_food;
            return r;
        }).collect(Collectors.toList());
        return restaurants;
    }


}