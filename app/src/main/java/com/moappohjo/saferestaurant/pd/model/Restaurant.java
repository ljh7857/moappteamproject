package com.moappohjo.saferestaurant.pd.model;

import androidx.annotation.Nullable;

import com.moappohjo.saferestaurant.R;


public class Restaurant implements Comparable<Restaurant> {
    public final int id;
    public final String name;
    public final String address;
    public final String type;
    public final String tell;
    public int image;
    public double distance;


    public Restaurant(int id, String name, String address, String type, String tell) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.tell = tell;
        this.image = imageOf(type);
        this.id = id;

    }

    public static int imageOf(String type) {
        int ret = R.drawable.ic_food;
        switch (type) {
            case "한식":
                ret = R.drawable.rice;
                break;
            case "일식":
                ret = R.drawable.sushi;
                break;
            case "서양식":
                ret = R.drawable.pizza;
                break;
            case "중식":
                ret = R.drawable.chinese;
                break;
            case "기타 음식점업":
                ret = R.drawable.shop;
                break;
            default:
                break;
        }
        return ret;
    }

    @Override
    public int compareTo(Restaurant o) {
       return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }


    //인자 1,2는 현재 사용자 위경도, 3,4는 식당 위경도, 5는 단위
    public double getDistance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(degTorad(lat1)) * Math.sin(degTorad(lat2)) + Math.cos(degTorad(lat1)) * Math.cos(degTorad(lat2)) * Math.cos(degTorad(theta));

        dist = Math.acos(dist);
        dist = radTodeg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double degTorad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double radTodeg(double rad) {
        return (rad * 180 / Math.PI);
    }

}
