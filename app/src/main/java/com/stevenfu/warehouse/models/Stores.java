package com.stevenfu.warehouse.models;

import com.stevenfu.warehouse.base.Entity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Steven Fu on 12/01/2016.
 */
public class Stores extends Entity {
    public Stores() {
    }
    public int Id;
    public String Name;
    public String Address;
    public String Phone;
    public String Fax;
    public int Manager;
}


