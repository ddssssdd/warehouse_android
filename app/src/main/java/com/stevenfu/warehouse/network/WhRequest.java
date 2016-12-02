package com.stevenfu.warehouse.network;

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
import com.stevenfu.warehouse.settings.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Steven Fu on 12/01/2016.
 */

public class WhRequest<T extends Entity> extends Request<JSONObject>{
    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;
    private IItemsAdapter mAdapter;
    private Context mContext;
    private Class mClass;
    public ListView BindListView;
    public WhRequest(Context context, String url, Map<String, String> params, IItemsAdapter itemAdapter,Class entityClass)
    {
        super(Method.POST, Url.SERVER_URL + url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        this.listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                HandleResponseJson(response);
            }
        };
        this.params = params;
        mAdapter = itemAdapter;
        mContext = context;
        mClass = entityClass;
    }

    public WhRequest(String url, Map<String, String> params,
                         Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    public WhRequest(int method, String url, Map<String, String> params,
                         Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
        Log.d("Http",url);
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    };
    protected void HandleResponseJson(JSONObject response)
    {
        List = new WItems<T>(response,mClass);
        if (List.status){
            Adapter = new ItemsAdapter(mContext,List,mAdapter);
            if (BindListView!=null){
                BindListView.setAdapter(Adapter);
            }
        }
    }
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
