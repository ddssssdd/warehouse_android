package com.stevenfu.warehouse.base;

import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * Created by Steven Fu on 12/01/2016.
 */

public class Entity {
    public Entity()
    {

    }
    public void InitDataWithJson(JSONObject obj)
    {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field: fields){
            String name = field.getName();
            if (obj.has(name)){
                try
                {
                    Object value=null;
                    //Log.d("Type",field.getType().getName());
                    if (field.getType().getName().equalsIgnoreCase("int")){
                        value = obj.optInt(name);
                    }else if (field.getType().getName().toLowerCase().contains("string")){
                        value = obj.optString(name);
                    }else if (field.getType().getName().toLowerCase().contains("boolean")){
                        value = obj.optBoolean(name);
                    }else if (field.getType().getName().toLowerCase().contains("double")){
                        value = obj.optDouble(name);
                    }else{
                        value = obj.opt(name);
                    }
                    if (value!=null){
                        field.set(this,value);
                    }

                }catch (Exception ex)
                {
                    Log.d("Exception",ex.toString());
                }
            }


        }
    }
    public String Description()
    {
        try{
            Field field = getClass().getField("Name");
            String result = field.get(this).toString();
            return result;

        }catch (Exception ex){
            Log.d("Exception",ex.toString());
        }
        return this.toString();
    }
}
