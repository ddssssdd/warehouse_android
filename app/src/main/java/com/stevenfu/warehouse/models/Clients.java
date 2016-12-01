package com.stevenfu.warehouse.models;

import com.stevenfu.warehouse.base.Entity;

import org.json.JSONObject;

/**
 * Created by Steven Fu on 12/01/2016.
 */
public class Clients extends Entity {
    public Clients() {

    }

    public int Id;
    public String Name;
    public String Phone;
    public String Fax;
    public String Email;
    public String Address;
    public int ContactId;
}
