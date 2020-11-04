package com.moappohjo.saferestaurant.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.moappohjo.saferestaurant.R;
import com.moappohjo.saferestaurant.ui.MainViewModel;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import javax.inject.Inject;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private MapViewModel mapViewModel;
    private MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        mapViewModel =
                ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            if (mainViewModel.getMapViewData().getValue() == null) {
                mapFragment = MapFragment.newInstance();
                mainViewModel.setMapViewData(mapFragment);
            } else
                mapFragment = mainViewModel.getMapViewData().getValue();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        mapViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

    }
}