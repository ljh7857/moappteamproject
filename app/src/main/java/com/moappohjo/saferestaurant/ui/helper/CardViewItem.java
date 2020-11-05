package com.moappohjo.saferestaurant.ui.helper;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.internal.HashAccumulator;

import java.util.concurrent.atomic.AtomicInteger;


public class CardViewItem implements Comparable<CardViewItem>{
    static final AtomicInteger count = new AtomicInteger(0);
    final int id;
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
        id = count.incrementAndGet();
    }

    @Override
    public int compareTo(CardViewItem o) {
       return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
