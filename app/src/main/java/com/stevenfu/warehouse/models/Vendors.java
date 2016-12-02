package com.stevenfu.warehouse.models;

import com.stevenfu.warehouse.base.Entity;

/**
 * Created by Steven Fu on 12/02/2016.
 */

public class Vendors extends Entity{
    public Vendors() {

    }

    public int Id;
    public String Name;
    public String Phone;
    public String Fax;
    public String Email;
    public String Address;
    public int ContactId;
    public String ContactName;
    public String ContactCellphone;
}
