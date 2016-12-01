package com.stevenfu.warehouse.adpaters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Steven Fu on 12/01/2016.
 */

public interface IItemsAdapter {
    View getView(LayoutInflater inflater,int i, View convertView, ViewGroup parent);
}
