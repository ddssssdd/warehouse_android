package com.stevenfu.warehouse;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.stevenfu.warehouse.base.BaseActivity;
import com.stevenfu.warehouse.fragments.StocksInFragment;
import com.stevenfu.warehouse.fragments.StocksOutFragment;
import com.stevenfu.warehouse.models.Clients;
import com.stevenfu.warehouse.models.Detail;
import com.stevenfu.warehouse.models.Products;
import com.stevenfu.warehouse.models.Stores;
import com.stevenfu.warehouse.models.WItems;
import com.stevenfu.warehouse.network.WhRequest;
import com.stevenfu.warehouse.settings.Url;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StocksOutActivity extends BaseActivity implements StocksOutFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks_out);
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
    private Spinner spClient;
    private EditText edtEnteredDate;

    private EditText edtTotalPrice;
    private EditText edtTotalNo;
    private ListView listDetails;
    private Button btnAddDetail;
    private ArrayList<Detail> details;
    private StocksOutActivity.DetailAdapter detailAdapter;


    WItems<Clients> clients;
    WItems<Stores> stores;
    private Clients currentClient;


    private void InitData()
    {
        details = new ArrayList<Detail>();
        btnAddDetail = (Button)findViewById(R.id.btnAddDetail);
        listDetails =(ListView)findViewById(R.id.listDetails);
        edtMemo = (EditText)findViewById(R.id.edtMemo);
        edtInvoiceNo = (EditText)findViewById(R.id.edtInvoiceNo);
        spClient =(Spinner)findViewById(R.id.spClient);
        edtEnteredDate = (EditText)findViewById(R.id.edtEnteredDate);

        edtTotalPrice =(EditText)findViewById(R.id.edtTotalPrice);
        edtTotalNo = (EditText)findViewById(R.id.edtTotalNo);

        btnAddDetail.setEnabled(false); // first ,set disable wait for loading products and stores;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        edtEnteredDate.setText(format.format(new Date()));
        //bind adapter to details;
        detailAdapter = new StocksOutActivity.DetailAdapter(this);
        listDetails.setAdapter(detailAdapter);
        //Init Vendor
        showProgress(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Url.SERVER_URL + Url.CLIENTS;
        WhRequest request = new WhRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                HandleClientResponseData(response);
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
        WhRequest request_store = new WhRequest(Request.Method.POST, url_store, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                stores = new WItems<>(response,Stores.class);
                enableUserActions();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandleError(error);
            }
        });
        queue.add(request_store);

        btnAddDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewDetail();
            }
        });

    }

    private void HandleClientResponseData(JSONObject res)
    {

        clients = new WItems<>(res,Clients.class);
        if (clients.status){
            ArrayList<String> arrayItems = clients.DescriptionList();
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,arrayItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spClient.setAdapter(adapter);
            spClient.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectClient(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    selectClient(-1);
                }
            });


        }
        enableUserActions();
    }

    private void selectClient(int index){
        if (index>-1){
            currentClient = clients.Items.get(index);

        }
    }

    private void enableUserActions()
    {
        showProgress(false);
        boolean canWork = clients !=null && clients.status;
        btnAddDetail.setEnabled(canWork);
    }

    private void AddNewDetail()
    {
        StocksOutFragment fragment = StocksOutFragment.newInstance(0);
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
    public void AddDetail(Products product,  double price, double quantity, Stores store, String memo)
    {
        if (product!=null && store!=null && price!=0.0f && quantity!=0.0f){
            Detail detail = new Detail();
            detail.Price = price;
            detail.Product = product;
            detail.ProductId = product.Id;
            detail.Store = store;
            detail.StoreId = store.Id;
            detail.Quantity = quantity;

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
    public class DetailAdapter extends BaseAdapter {
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
                view = inflater.inflate(R.layout.stocks_out_item,parent,false);
            }else{
                view = convertView;
            }

            TextView textName = (TextView)view.findViewById(R.id.txtName);
            final Detail detail = details.get(i);
            detail.UpdateSequ = i;
            textName.setText(detail.Product.Name);
            ((TextView)view.findViewById(R.id.txtMemo)).setText(detail.Memo);
            ((TextView)view.findViewById(R.id.txtStore)).setText(detail.Store.Name);
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
        map.put("ClientId",String.format("%d", currentClient.Id));
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


        }

        String url = Url.SERVER_URL+ Url.STOCKS_OUT;

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
            setResult(1);
            finish();
        }else{
            Toast.makeText(this,JsonResultMessage,Toast.LENGTH_LONG).show();
        }
    }
}
