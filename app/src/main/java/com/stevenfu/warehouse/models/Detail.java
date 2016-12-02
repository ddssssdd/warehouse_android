package com.stevenfu.warehouse.models;

import com.stevenfu.warehouse.base.Entity;

/**
 * Created by Steven Fu on 12/02/2016.
 */

public class Detail extends Entity
{
    public Detail()
    {}
    public int Id;
    public String Method;
    public String Memo;
    public double BeforeUpdate;
    public double AfterUpdate;
    public double Quantity;
    public double Price;
    public String Specification;
    public int UpdateSequ;
    public String UpdateDate;
    public String Direction()
    {
        if (Method.equalsIgnoreCase("in")){
            return "入库";
        }else{
            return "出库";
        }
    }
    public String Description()
    {
        return String.format("%d. %s -%1.2f",UpdateSequ,Direction(),Price);
    }
    public String Operation()
    {
        if (Method.equalsIgnoreCase("in")){
            return String.format(" + %1.2f = ",Quantity);
        }else{
            return String.format(" - %1.2f = ",Quantity);
        }
    }

}