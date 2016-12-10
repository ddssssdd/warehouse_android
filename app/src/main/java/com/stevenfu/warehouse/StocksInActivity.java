package com.stevenfu.warehouse;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.stevenfu.warehouse.base.BaseActivity;
import com.stevenfu.warehouse.fragments.StocksInFragment;
import com.stevenfu.warehouse.models.Clients;
import com.stevenfu.warehouse.models.Detail;
import com.stevenfu.warehouse.models.Products;
import com.stevenfu.warehouse.models.Stores;
import com.stevenfu.warehouse.models.Vendors;
import com.stevenfu.warehouse.models.WItems;
import com.stevenfu.warehouse.network.WhRequest;
import com.stevenfu.warehouse.settings.Url;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StocksInActivity extends BaseActivity implements StocksInFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSave();
            }
        });

        InitData();
    }

    private EditText edtMemo;
    private EditText edtInvoiceNo;
    private Spinner spVendor;
    private EditText edtEnteredDate;
    private Spinner spStore;
    private EditText edtTotalPrice;
    private EditText edtTotalNo;
    private ListView listDetails;
    private Button btnAddDetail;
    private ArrayList<Detail> details;
    private DetailAdapter detailAdapter;

    WItems<Products> products;
    WItems<Vendors> vendors;
    WItems<Stores> stores;

    private Vendors currentVendor;
    private Stores currentStore;

    private void InitData()
    {
        details = new ArrayList<Detail>();
        btnAddDetail = (Button)findViewById(R.id.btnAddDetail);
        listDetails =(ListView)findViewById(R.id.listDetails);
        edtMemo = (EditText)findViewById(R.id.edtMemo);
        edtInvoiceNo = (EditText)findViewById(R.id.edtInvoiceNo);
        spVendor =(Spinner)findViewById(R.id.spVendor);
        edtEnteredDate = (EditText)findViewById(R.id.edtEnteredDate);
        spStore = (Spinner)findViewById(R.id.spStore);
        edtTotalPrice =(EditText)findViewById(R.id.edtTotalPrice);
        edtTotalNo = (EditText)findViewById(R.id.edtTotalNo);

        btnAddDetail.setEnabled(false); // first ,set disable wait for loading products and stores;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        edtEnteredDate.setText(format.format(new Date()));
        //bind adapter to details;
        detailAdapter = new DetailAdapter(this);
        listDetails.setAdapter(detailAdapter);
        //Init Vendor
        showProgress(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Url.SERVER_URL + Url.VENDORS;
        WhRequest request = new WhRequest(this, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                HandleVendorResponseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandleError(error);
            }
        });
        queue.add(request);
        //Init_Store
        showProgress(true);

        String url_store = Url.SERVER_URL + Url.STORES;
        WhRequest request_store = new WhRequest(this, url_store, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                HandleStoreResponseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandleError(error);
            }
        });
        queue.add(request_store);

        //init products;

        String url_product = Url.SERVER_URL + Url.PRODUCTS;
        WhRequest request_product = new WhRequest(this, url_product, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                products = new WItems<>(response,Products.class);
                enableUserActions();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandleError(error);
            }
        });
        queue.add(request_product);

        btnAddDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewDetail();
            }
        });

    }

    private void HandleVendorResponseData(JSONObject res)
    {

        vendors = new WItems<>(res,Vendors.class);
        if (vendors.status){
            ArrayList<String> arrayItems = vendors.DescriptionList();
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,arrayItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spVendor.setAdapter(adapter);
            spVendor.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectVendor(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    selectVendor(-1);
                }
            });


        }
        enableUserActions();
    }
    private void selectVendor(int index){
        if (index>-1){
            currentVendor = vendors.Items.get(index);

        }
    }
    private void HandleStoreResponseData(JSONObject res){

        stores = new WItems<>(res,Stores.class);
        if (stores.status){
            ArrayList<String> arrayItems = stores.DescriptionList();
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,arrayItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spStore.setAdapter(adapter);
            spStore.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectStore(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    selectStore(-1);
                }
            });


        }
        enableUserActions();
    }
    private void enableUserActions()
    {
        showProgress(false);
        boolean canWork = products!=null && stores!=null && vendors!=null && vendors.status && products.status && stores.status;
        btnAddDetail.setEnabled(canWork);
    }
    private void selectStore(int index){
        if (index>-1){
            currentStore = stores.Items.get(index);

        }
        int i = spStore.getSelectedItemPosition();
        Log.d("Select",String.format("Index=%d",i));
    }
    private void AddNewDetail()
    {
        int store_index = spStore.getSelectedItemPosition();
        StocksInFragment fragment = StocksInFragment.newInstance(store_index);
        fragment.show(getSupportFragmentManager(),"Title");
    }
    private  void SelectDetail(Detail detail){//detail list view select item;

    }
    private void DeleteDetail(Detail detail){
        details.remove(detail.UpdateSequ);
        this.ReCalculate();
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    @Override
    public WItems<Stores> getStores()
    {
        return stores;
    }
    @Override
    public WItems<Products> getProducts()
    {
        return products;
    }

    @Override
    public void AddDetail(Products product, String specification, double price, double quantity, Stores store, String memo)
    {
        if (product!=null && store!=null && price!=0.0f && quantity!=0.0f){
            Detail detail = new Detail();
            detail.Price = price;
            detail.Product = product;
            detail.ProductId = product.Id;
            detail.Store = store;
            detail.StoreId = store.Id;
            detail.Quantity = quantity;
            detail.Specification = specification;
            detail.Memo = memo;
            details.add(detail);
        }
        this.ReCalculate();
    }
    private void ReCalculate(){
        double sum = 0;
        double count = 0;
        for(int i=0;i<details.size();i++){
            Detail d = details.get(i);
            count += d.Quantity;
            sum += d.Quantity * d.Price;
        }
        edtTotalNo.setText(String.format("%1.2f",count));
        edtTotalPrice.setText(String.format("%1.2f",sum));
        this.detailAdapter.notifyDataSetChanged();
    }
    public class DetailAdapter extends BaseAdapter{
        LayoutInflater inflater;
        public DetailAdapter(Context contex){
            inflater = LayoutInflater.from(contex);
        }
        @Override
        public int getCount() {
            return details.size();
        }

        @Override
        public Object getItem(int i) {
            return details.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null){
                view = inflater.inflate(R.layout.stocks_in_item,parent,false);
            }else{
                view = convertView;
            }

            TextView textName = (TextView)view.findViewById(R.id.txtName);
            final Detail detail = details.get(i);
            detail.UpdateSequ = i;
            textName.setText(detail.Product.Name);
            ((TextView)view.findViewById(R.id.txtMemo)).setText(detail.Memo);
            ((TextView)view.findViewById(R.id.txtSpecification)).setText(detail.Specification);
            ((TextView)view.findViewById(R.id.txtPrice)).setText(String.format("%1.2f",detail.Price));
            ((TextView)view.findViewById(R.id.txtQuantity)).setText(String.format("%1.2f",detail.Quantity));
            ((TextView)view.findViewById(R.id.txtTotal)).setText(String.format("%1.2f",detail.Price*detail.Quantity));


            ((Button)view.findViewById(R.id.btnDelete)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeleteDetail(detail);
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectDetail(detail);
                }
            });

            return view;
        }
    }
    private void doSave(){
        Map map = new HashMap();
        map.put("Id",String.format("%d",1));
        map.put("VendorId",String.format("%d",currentVendor.Id));
        map.put("StoreId",String.format("%d",currentStore.Id));
        map.put("TotalPrice", edtTotalPrice.getText().toString());
        map.put("TotalNo",edtTotalNo.getText().toString());
        map.put("InvoiceNo",edtInvoiceNo.getText().toString());
        map.put("Memo", edtMemo.getText().toString());
        map.put("EnteredDate", edtEnteredDate.getText().toString());


        for(int i=0;i<details.size();i++){
            Detail d= details.get(i);

            map.put(String.format("details[%d][ProductId]",i),String.format("%d",d.ProductId));
            map.put(String.format("details[%d][StoreId]",i),String.format("%d",d.StoreId));
            map.put(String.format("details[%d][Price]",i),String.format("%1.2f",d.Price));
            map.put(String.format("details[%d][Quantity]",i),String.format("%1.2f",d.Quantity));
            map.put(String.format("details[%d][Memo]",i), d.Memo);
            map.put(String.format("details[%d][Specification]",i), d.Specification);

        }

        String url = Url.SERVER_URL+ Url.STOCKS_IN;

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
            setResult(1);
            finish();
        }else{
            Toast.makeText(this,JsonResultMessage,Toast.LENGTH_LONG).show();
        }
    }
}
