package com.stevenfu.warehouse.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.stevenfu.warehouse.models.WItems;

/**
 * Created by Steven Fu on 12/01/2016.
 */

public class ItemsAdapter extends BaseAdapter {
    private WItems<?> mItems;
    private IItemsAdapter mViewHandler;
    private LayoutInflater inflater;
    public ItemsAdapter(Context context,WItems<?> witems, IItemsAdapter viewHandler)
    {
        this.inflater = LayoutInflater.from(context);
        mItems = witems;
        mViewHandler = viewHandler;
    }
    @Override
    public int getCount() {
        return mItems.Items.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.Items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return mViewHandler.getView(inflater,i,view,viewGroup);
    }
}
