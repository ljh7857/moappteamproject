package com.moappohjo.saferestaurant.ui.views;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moappohjo.saferestaurant.R;
import com.moappohjo.saferestaurant.dm.DataManager;
import com.moappohjo.saferestaurant.pd.model.MarkerDTO;
import com.moappohjo.saferestaurant.pd.model.Restaurant;
import com.moappohjo.saferestaurant.ui.helper.Utils;
import com.moappohjo.saferestaurant.ui.viewmodels.MainViewModel;
import com.moappohjo.saferestaurant.ui.helper.RecyclerViewAdapter;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private LinearLayout searchViewContainer;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private NaverMap naverMap;
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private MainViewModel viewModel = new MainViewModel();
    private RecyclerViewAdapter recyclerViewAdapter;
    private Button showListButton;
    public static Context mContext;
    private static Location prevLocation = null;
    private Spinner searchOptionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNaverMap();
        setContentView(R.layout.activity_main);
        mContext = this;
        showListButton = findViewById(R.id.showListButton);
        setShowListObserver();
        setMarkersObserver();
        setSearchView();
        setRecyclerView();
        //사용자의 현재 위치를 얻어서 넣어주세요.
        Address address=null;
        DataManager dm = new DataManager(getApplicationContext(), address);
        if(!dm.loadData()){

        }
    }

    private void setMarkersObserver() {
        viewModel.markers.observe(this, new Observer<List<Marker>>() {
            @Override
            public void onChanged(List<Marker> markers) {
                markers.forEach(m -> {
                    m.setOnClickListener(new Overlay.OnClickListener() {
                        @Override
                        public boolean onClick(@NonNull Overlay overlay) {
                            List<Restaurant> items = viewModel.items.getValue().stream().filter((i)->(i.id == overlay.getZIndex())).collect(Collectors.toList());
                            if (items.size() == 0) return false;
                            Restaurant restaurant = items.get(0);
                            viewModel.items.remove(restaurant);
                            viewModel.items.add(0, restaurant);
                            viewModel.setIsListShowing(true);
                            recyclerView.smoothScrollToPosition(0);
                            return true;
                        }
                    });
                    m.setMap(naverMap);});
            }
        });
    }

    private void setShowListObserver() {
        viewModel.getIsListShowing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean showList) {
                if (showList) {recyclerView.setVisibility(View.VISIBLE); showListButton.setText(getResources().getString(R.string.hide_list));}
                else {recyclerView.setVisibility(View.GONE); showListButton.setText(getResources().getString(R.string.show_list));}

            }
        });
    }

    public void setRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), viewModel.items.getValue(), R.layout.activity_main);
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                viewModel.setIsListShowing(false);
                Restaurant restaurant = recyclerViewAdapter.items.get(pos);
                List<Marker> markers = viewModel.markers.getValue().stream().filter((m)->(m.getZIndex() == restaurant.id)).collect(Collectors.toList());
                if (markers.size() == 0) return;
                Marker marker = markers.get(0);
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(marker.getPosition());
                naverMap.moveCamera(cameraUpdate);

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
        searchViewContainer = findViewById(R.id.search_container);
        searchOptionSpinner = findViewById(R.id.search_option_spinner);
        //TextView t = (TextView)(searchOptionSpinner.getSelectedItem());
        //searchOption = t.getText().toString();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.search(query, searchOptionSpinner.getSelectedItem().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchViewContainer.setBackgroundColor(Color.WHITE);
                }
                else  {
                    searchViewContainer.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

    }

    private void setNaverMap() {
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
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
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                if (prevLocation != null && isLocationSame(location, prevLocation)) return;
                prevLocation = new Location(location);
                if (location.hasAccuracy()) {
                    //Toast.makeText(this, "haha", Toast.LENGTH_SHORT).show();
                    viewModel.markers.getValue().forEach(m->{m.setMap(null);});
                    viewModel.markers.clear();
                    viewModel.items.clear();
                    viewModel.refresh(location.getLatitude(), location.getLongitude());
                    naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
                }
            }
        });
        //LatLng p = naverMap.getLocationOverlay().getPosition();
        //this.viewModel.refresh(p.latitude, p.longitude);
        //setMarkers();
    }

    private boolean isLocationSame(Location location, Location prevLocation) {
        return Math.abs(location.getLongitude() - prevLocation.getLongitude()) < 0.1
                && Math.abs(location.getLatitude() - prevLocation.getLatitude()) < 0.1;
    }

    public void onClickShowList(View v) {
        viewModel.setIsListShowing(!viewModel.getIsListShowing().getValue());
    }

}