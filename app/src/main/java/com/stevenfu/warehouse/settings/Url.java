package com.stevenfu.warehouse.settings;

/**
 * Created by Steven Fu on 11/30/2016.
 */

public class Url {
   // public static String SERVER_URL = "http://g.thoughts-go.top/";
    public static String SERVER_URL = "http://10.4.30.60:8000/warehouse/";
    public static String USER_LOGIN = "home/login_json";

    public static String STORES = "store/Items";
    public static String STOCKS_IN = "stocks/save_in";
    public static String STOCKS_OUT = "stocks/save_out";
    public static String STOCKS_PRODUCTS ="stocks/products?store_id=%d";
    public static String STOCKS_DETAIL_WITH_STOREID_PRODUCTID_INVENTORYID ="stocks/details?store_id=%d&product_id=%d&inventory_id=%d";

    public static String PRODUCTS = "product/items";
    public static String PRODUCT_FIND = "product/Find?id=%d";
    public static String PRODUCT_ADD = "product/Add";
    public static String PRODUCT_EDIT = "product/Edit";

    public static String CLIENTS = "client/items";
    public static String CLIENT_FIND = "client/Find?id=%d";
    public static String CLIENT_ADD = "client/Add";
    public static String CLIENT_EDIT = "client/Edit";

    public static String VENDORS = "vendor/items";
    public static String VENDOR_FIND = "vendor/Find?id=%d";
    public static String VENDOR_ADD = "vendor/Add";
    public static String VENDOR_EDIT = "vendor/Edit";

    public static String UPLOAD_FILE ="Uploadfile/post";





}
