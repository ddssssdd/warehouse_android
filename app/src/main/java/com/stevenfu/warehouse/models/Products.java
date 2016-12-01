package com.stevenfu.warehouse.models;
        import com.stevenfu.warehouse.base.Entity;
/**
 * Created by Steven Fu on 12/01/2016.
 */

public class Products extends Entity{
        public Products(){ }
        public int Id;
        public String Name;
        public String Specification;
        public String Unit;
        public double Width;
        public double Height;
        public double Length;
        public String Brand;
        public String Barcode;
        public double Price;
}
