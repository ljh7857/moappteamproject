package com.moappohjo.saferestaurant.pd.model;

import com.naver.maps.geometry.LatLng;

public class MarkerDTO {
    public final int id;
    public final double lat;
    public final double lng;
    public final String caption;

    public MarkerDTO(int id, double lat, double lng, String caption) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.caption = caption;
    }
}
