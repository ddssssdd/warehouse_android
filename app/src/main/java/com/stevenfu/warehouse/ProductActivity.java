package com.stevenfu.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.stevenfu.warehouse.adpaters.IItemsAdapter;
import com.stevenfu.warehouse.adpaters.ItemsAdapter;
import com.stevenfu.warehouse.base.BaseActivity;
import com.stevenfu.warehouse.models.Inventory;
import com.stevenfu.warehouse.models.Products;
import com.stevenfu.warehouse.models.WItems;
import com.stevenfu.warehouse.network.WhRequest;
import com.stevenfu.warehouse.settings.Url;

import org.json.JSONObject;

import java.util.Map;

public class ProductActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doEditProduct(0);
            }
        });
        InitData();
    }
    private void doEditProduct(int product_id){
        Intent intent = new Intent(ProductActivity.this,ProductEditActivity.class);
        intent.putExtra("product_id",product_id);
        startActivityForResult(intent,CODE_PRODUCT);
    }
    private static int CODE_PRODUCT = 1;
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode==CODE_PRODUCT) { //for login
            if (resultCode>0){
                String message = "新建产品成功";
                if (resultCode==2){
                    message = "修改产品成功";
                }
                Snackbar.make(this.getCurrentFocus(), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                InitData();
            }

        }
    }
    private void InitData()
    {
        LoadData(Url.SERVER_URL+Url.PRODUCTS,null);
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
    protected void HandleResponseData(JSONObject response)    {
        showProgress(false);
        final WItems<Products> items = new WItems<>(response,Products.class);
        if (items.status){

            ItemsAdapter adapter = new ItemsAdapter(this,items,new IItemsAdapter(){
                @Override
                public View getView(LayoutInflater inflater, int i, View convertView, ViewGroup parent)
                {
                    View view;
                    if (convertView == null){
                        view = inflater.inflate(R.layout.product_item,parent,false);
                    }else{
                        view = convertView;
                    }

                    TextView textName = (TextView)view.findViewById(R.id.txtName);
                    final Products product = items.Items.get(i);
                    textName.setText(product.Name);
                    TextView textPhone = (TextView)view.findViewById(R.id.txtPrice);
                    textPhone.setText(String.format("%1.2f",product.Price));
                    TextView txt1 = (TextView)view.findViewById(R.id.txtUnit);
                    txt1.setText(product.Unit);
                    if (product.Specification!=null){
                        TextView txtSpecification = (TextView)view.findViewById(R.id.txtSpecification);
                        txtSpecification.setText(product.Specification);
                    }

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SelectItem(product);
                        }
                    });

                    return view;
                }
            });
            ListView listStore = (ListView)findViewById(R.id.listMain);
            listStore.setAdapter(adapter);
        }
    }
    protected void SelectItem(Products product)
    {
        doEditProduct(product.Id);
    }

}
