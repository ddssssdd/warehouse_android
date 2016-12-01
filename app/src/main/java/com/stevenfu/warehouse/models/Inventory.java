package com.stevenfu.warehouse.models;
import com.stevenfu.warehouse.base.Entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;


/**
 * Created by Steven Fu on 12/01/2016.
 */

public class Inventory extends Entity{
    public Inventory(){ }
    public int Id;
    public int StoreId;
    public int ProductId;
    public double Quantity;
    public double MaxPrice;
    public double MinPrice;
    public double MinOutPrice;
    public double MaxOutPrice;
    public int Index;
    public Date LastUpdate;
    public int UserId;
    public String Name;
    public String Specification;
}