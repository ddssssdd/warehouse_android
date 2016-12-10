package com.stevenfu.warehouse.settings;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by Steven Fu on 11/30/2016.
 */

public class App {
    public static String PREFS ="COM.STEVENFU.WAREHOUSE";
    public static boolean IsLogin(Activity context)
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS,0);
        boolean result = settings.getBoolean("IsLogin",false);
        return result;
    }
    public static String Username(Activity context)
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS,0);
        return settings.getString("UserName","Unknown");
    }
    public static int UserId(Activity context)
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS,0);
        return settings.getInt("UserId",0);
    }
    public static String Token(Activity context)
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS,0);
        return settings.getString("Token","android");
    }
    public static void Login(Activity context,int userId,String username,String password)
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS,0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("IsLogin",true);
        editor.putInt("UserId",userId);
        editor.putString("UserName",username);
        editor.putString("Password",password);
        editor.putString("LoginDateTime", new Date().toString());
        editor.commit();
    }
    public static void Logout(Activity context)
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS,0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("IsLogin",false);
        editor.putInt("UserId",0);
        editor.putString("UserName","");
        editor.putString("Token","");
        editor.remove("LoginDateTime");
        editor.commit();

    }
}
