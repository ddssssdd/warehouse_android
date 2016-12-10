package com.stevenfu.warehouse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.stevenfu.warehouse.base.BaseActivity;
import com.stevenfu.warehouse.models.Clients;
import com.stevenfu.warehouse.models.Products;
import com.stevenfu.warehouse.network.WhRequest;
import com.stevenfu.warehouse.settings.Url;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ClientEditActivity extends BaseActivity {

    private Clients mClient;
    protected EditText edtName;
    protected EditText edtAddress;
    protected EditText edtPhone;
    protected EditText edtFax;
    protected EditText edtEmail;
    protected EditText edtConttactName;
    protected EditText edtContactCellphone;
    protected Button btnSave;
    protected int item_id;
    protected String mEditUrl;
    protected String mAddurl;
    protected String mFindUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_client_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        InitData();
    }
    private void InitData()
    {
        edtName= (EditText)findViewById(R.id.edtName);
        edtAddress= (EditText)findViewById(R.id.edtAddress);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        edtFax = (EditText)findViewById(R.id.edtFax);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtContactCellphone = (EditText)findViewById(R.id.edtContactCellphone);
        edtConttactName = (EditText)findViewById(R.id.edtContactName);

        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoSave();
            }
        });

        Intent intent = getIntent();
        item_id = intent.getIntExtra("id",0);
        mAddurl = intent.getStringExtra("AddUrl");
        mEditUrl = intent.getStringExtra("EditUrl");
        mFindUrl = intent.getStringExtra("FindUrl");
        if (item_id>0){
            btnSave.setText("更新");
        }
        LoadData(String.format(Url.SERVER_URL+mFindUrl,item_id),null);
    }
    protected void LoadData(String url,Map parameters){
        showProgress(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        WhRequest request = new WhRequest(this, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                HandleResponseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandleError(error);
            }
        });
        queue.add(request);

    }

    protected void HandleResponseData(JSONObject response)
    {
        showProgress(false);
        mClient = new Clients();
        if (response.optBoolean("status")){
            mClient.InitDataWithJson(response.optJSONObject("result"));
        }

        edtName.setText(mClient.Name);
        edtAddress.setText(mClient.Address);
        edtPhone.setText(mClient.Phone);
        edtFax.setText(mClient.Fax);
        edtEmail.setText(mClient.Email);
        edtContactCellphone.setText(mClient.ContactCellphone);
        edtConttactName.setText(mClient.ContactName);
        this.setTitle(String.format("客户 -%s",mClient.Name ));

    }
    private  void DoSave()
    {
        Map map = new HashMap();
        map.put("Id",String.format("%d",item_id));
        map.put("Name",edtName.getText().toString());
        map.put("Address",edtAddress.getText().toString());
        map.put("Phone",edtPhone.getText().toString());
        map.put("Fax",edtFax.getText().toString());
        map.put("Email",edtEmail.getText().toString());
        map.put("ContactName", edtConttactName.getText().toString());
        map.put("ContactCellphone", edtContactCellphone.getText().toString());

        String url = Url.SERVER_URL;
        if (item_id>0){
            url = url + mEditUrl;
        }else{
            url = url + mAddurl;
        }
        showProgress(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        WhRequest request = new WhRequest(this, url, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                HandleUpdateResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandleError(error);
            }
        });
        queue.add(request);
    }
    private void HandleUpdateResult(JSONObject response)
    {
        showProgress(false);
        HandleJsonResult(response);
        if (JsonResultStatus){
            int returnCode=1;
            if (item_id>0){
                returnCode = 2;
            }
            setResult(returnCode);
            finish();
        }else{
            Toast.makeText(this,JsonResultMessage,Toast.LENGTH_LONG).show();
        }
    }
}
