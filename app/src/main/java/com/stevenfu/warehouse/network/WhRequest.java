package com.stevenfu.warehouse.network;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.stevenfu.warehouse.adpaters.IItemsAdapter;
import com.stevenfu.warehouse.adpaters.ItemsAdapter;
import com.stevenfu.warehouse.base.Entity;
import com.stevenfu.warehouse.models.WItems;
import com.stevenfu.warehouse.settings.App;
import com.stevenfu.warehouse.settings.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steven Fu on 12/01/2016.
 */

public class WhRequest<T extends Entity> extends Request<JSONObject>{
    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;
    private IItemsAdapter mAdapter;
    private Activity mContext;

    public WhRequest(Activity context, String url, Map<String, String> params,
                         Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, url, errorListener);
        mContext = context;
        this.listener = reponseListener;
        if (params==null){
            params = new HashMap<>();
        }
        params.put("userId",String.format("%d", App.UserId(context)));
        params.put("token",App.Token(context));

        this.params = params;
        Log.d("Http",url);
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    };

    public WItems<T> List;
    public ItemsAdapter Adapter;
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            JSONObject json = new JSONObject(jsonString);
            Response<JSONObject> result =  Response.success(json,
                    HttpHeaderParser.parseCacheHeaders(response));
            return result;
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }
}
