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
import com.stevenfu.warehouse.models.Products;
import com.stevenfu.warehouse.network.WhRequest;
import com.stevenfu.warehouse.settings.Url;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProductEditActivity extends BaseActivity {

    private Products mProduct;
    private EditText edtName;
    private EditText edtSpecification;
    private EditText edtUnit;
    private EditText edtLength;
    private EditText edtWidth;
    private EditText edtHeight;
    private EditText edtPrice;
    private EditText edtBrand;
    private EditText edtBarcode;
    private Button btnSave;
    private int product_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        InitData();
    }
    private void InitData()
    {
        edtName= (EditText)findViewById(R.id.edtName);
        edtSpecification= (EditText)findViewById(R.id.edtSpecification);
        edtUnit = (EditText)findViewById(R.id.edtUnit);
        edtLength = (EditText)findViewById(R.id.edtLength);
        edtWidth = (EditText)findViewById(R.id.edtWidth);
        edtHeight = (EditText)findViewById(R.id.edtHeight);
        edtPrice = (EditText)findViewById(R.id.edtPrice);
        edtBrand = (EditText)findViewById(R.id.edtBrand);
        edtBarcode = (EditText)findViewById(R.id.edtBarcode);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoSave();
            }
        });

        Intent intent = getIntent();
        product_id = intent.getIntExtra("product_id",0);
        if (product_id>0){
            btnSave.setText("更新");
        }
        LoadData(String.format(Url.SERVER_URL+Url.PRODUCT_FIND,product_id),null);
    }
    protected void LoadData(String url,Map parameters){
        showProgress(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        WhRequest request = new WhRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
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
        mProduct = new Products();
        if (response.optBoolean("status")){
            mProduct.InitDataWithJson(response.optJSONObject("result"));
        }

        edtName.setText(mProduct.Name);
        edtSpecification.setText(mProduct.Specification);
        edtUnit.setText(mProduct.Unit);
        edtLength.setText(String.format("%1.2f", mProduct.Length));
        edtWidth.setText(String.format("%1.2f", mProduct.Width));
        edtHeight.setText(String.format("%1.2f", mProduct.Height));
        edtPrice.setText(String.format("%1.2f", mProduct.Price));
        edtBrand.setText(mProduct.Brand);
        edtBarcode.setText(mProduct.Barcode);
    }
    private  void DoSave()
    {
        Map map = new HashMap();
        map.put("Id",String.format("%d",product_id));
        map.put("Name",edtName.getText().toString());
        map.put("Specification",edtSpecification.getText().toString());
        map.put("Unit", edtUnit.getText().toString());
        map.put("Length",edtLength.getText().toString());
        map.put("Width",edtWidth.getText().toString());
        map.put("Height", edtHeight.getText().toString());
        map.put("Brand", edtBrand.getText().toString());
        map.put("Price", edtPrice.getText().toString());
        map.put("Barcode", edtBarcode.getText().toString());
        String url = Url.SERVER_URL;
        if (product_id>0){
            url = url + Url.PRODUCT_EDIT;
        }else{
            url = url + Url.PRODUCT_ADD;
        }
        showProgress(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        WhRequest request = new WhRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
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
            if (product_id>0){
                returnCode = 2;
            }
            setResult(returnCode);
            finish();
        }else{
            Toast.makeText(this,JsonResultMessage,Toast.LENGTH_LONG).show();
        }
    }
}
