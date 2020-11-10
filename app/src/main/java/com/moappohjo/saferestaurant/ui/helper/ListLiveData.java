package com.moappohjo.saferestaurant.ui.helper;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class ListLiveData<T> extends MutableLiveData<List<T>> {
    private List<T> temp = new ArrayList<T>();

    public ListLiveData() {
        this.setValue(temp);
    }
    public void add(T item) {
        temp.add(item);
        this.setValue(temp);
    }

    public void add(int index, T item) {
        temp.add(index, item);
        this.setValue(temp);
    }

    public void addAll(List<T> items) {
        temp.addAll(items);
        this.setValue(temp);
    }

    public void remove(T item) {
        temp.remove(item);
        this.setValue(temp);
    }

    public void clear() {
        temp.clear();
        this.setValue(temp);
    }
}
