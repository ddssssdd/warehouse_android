package com.stevenfu.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.stevenfu.warehouse.base.Entity;
import com.stevenfu.warehouse.models.Clients;
import com.stevenfu.warehouse.models.Products;
import com.stevenfu.warehouse.models.WItems;
import com.stevenfu.warehouse.network.WhRequest;
import com.stevenfu.warehouse.settings.Url;

import org.json.JSONObject;

import java.util.Map;

public class ClientActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 doEditItem(0);
            }
        });
        Init_activity();
        InitData();
    }

    protected void Init_activity()
    {
        editActivityClass = ClientEditActivity.class;
        mEditUrl = Url.CLIENT_EDIT;
        mAddurl = Url.CLIENT_ADD;
        mGetUrl = Url.CLIENTS;
        mFindUrl = Url.CLIENT_FIND;
        this.setTitle(String.format("客户"));
    }
    private int item_id;
    protected Class editActivityClass;
    protected String mEditUrl;
    protected String mAddurl;
    protected String mGetUrl;
    protected String mFindUrl;

    private void doEditItem(int id){
        item_id = id;
        Intent intent = new Intent(this,editActivityClass);
        intent.putExtra("id",id);
        intent.putExtra("EditUrl",mEditUrl);
        intent.putExtra("AddUrl",mAddurl);
        intent.putExtra("FindUrl",mFindUrl);
        startActivityForResult(intent, CODE_CURRENT_ITEM);
    }
    private static int CODE_CURRENT_ITEM = 1;
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode== CODE_CURRENT_ITEM) {
            if (resultCode>0){
                String message = "新建成功";
                if (resultCode==2){
                    message = "修改成功";
                }
                Snackbar.make(this.getCurrentFocus(), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                InitData();
            }

        }
    }
    private void InitData()
    {
        LoadData(Url.SERVER_URL+mGetUrl,null);
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
        final WItems<Clients> items = new WItems<>(response,Clients.class);
        if (items.status){

            ItemsAdapter adapter = new ItemsAdapter(this,items,new IItemsAdapter(){
                @Override
                public View getView(LayoutInflater inflater, int i, View convertView, ViewGroup parent)
                {
                    View view;
                    if (convertView == null){
                        view = inflater.inflate(R.layout.client_item,parent,false);
                    }else{
                        view = convertView;
                    }

                    TextView textName = (TextView)view.findViewById(R.id.txtName);
                    final Clients client = items.Items.get(i);
                    textName.setText(client.Name);
                    TextView textPhone = (TextView)view.findViewById(R.id.txtPhone);
                    textPhone.setText(client.Phone);
                    TextView txt1 = (TextView)view.findViewById(R.id.txtAddress);
                    txt1.setText(client.Address);

                    TextView txtSpecification = (TextView)view.findViewById(R.id.txtContact);
                    txtSpecification.setText(client.ContactName);


                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SelectItem(client.Id);
                        }
                    });

                    return view;
                }
            });
            ListView listStore = (ListView)findViewById(R.id.listMain);
            listStore.setAdapter(adapter);
        }
    }
    protected void SelectItem(int clientId)
    {
        doEditItem(clientId);
    }

}
