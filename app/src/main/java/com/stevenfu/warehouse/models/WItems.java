package com.stevenfu.warehouse.models;

import android.os.Parcelable;
import android.util.Log;

import com.stevenfu.warehouse.base.Entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Steven Fu on 12/01/2016.
 */

public class WItems<T extends Entity> {
    public String Message;
    public boolean status;
    public ArrayList<T> Items;
    private Class EntityClass;
    public WItems(JSONObject response, Class entityClass){
        EntityClass = entityClass;
        InitData(response);
    }
    private void InitData(JSONObject response)
    {
        status = response.optBoolean("status");
        if (!status){
            Message = response.optString("message");
        }
        JSONArray array = response.optJSONArray("result");
        if (array==null){
            return;
        }
        Items = new ArrayList<T>();
        for(int i=0;i<array.length();i++){
            JSONObject obj = array.optJSONObject(i);
            try
            {
                T t = (T)EntityClass.newInstance();
                t.InitDataWithJson(obj);
                Items.add(t);
            }catch (Exception ex)
            {
                Log.d("Exception",ex.toString());
            }


        }
    }
}
