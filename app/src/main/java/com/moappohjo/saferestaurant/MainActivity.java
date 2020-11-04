package com.moappohjo.saferestaurant;

import android.os.Bundle;
import android.util.SparseArray;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naver.maps.map.NaverMapSdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private SparseArray<Fragment.SavedState> savedStateSparseArray = new SparseArray<>();
    private int currentSelectedItemId = R.id.navigation_map;
    private static final String SAVED_STATE_CONTAINER_KEY = "ContainerKey";
    private static final String SAVED_STATE_CURRENT_TAB_KEY = "CurrentTabKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            savedStateSparseArray = savedInstanceState.getSparseParcelableArray(SAVED_STATE_CONTAINER_KEY);
            currentSelectedItemId = savedInstanceState.getInt(SAVED_STATE_CURRENT_TAB_KEY);
        }
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("sc9032srv9")
        );
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

}