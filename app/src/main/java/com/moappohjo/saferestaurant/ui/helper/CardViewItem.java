package com.moappohjo.saferestaurant.ui.helper;

public class CardViewItem {
    String name;
    String address;
    String type;
    String tell;
    int image;

    public CardViewItem(String name, String address, String type, String tell, int image) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.tell = tell;
        this.image = image;
    }
}
