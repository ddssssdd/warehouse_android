package com.stevenfu.warehouse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.stevenfu.warehouse.base.Entity;
import com.stevenfu.warehouse.models.Detail;
import com.stevenfu.warehouse.models.Products;
import com.stevenfu.warehouse.models.WItems;
import com.stevenfu.warehouse.network.WhRequest;
import com.stevenfu.warehouse.settings.Url;

import org.json.JSONObject;

import java.util.Map;

public class StocksDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks_detail);
        InitData();
        this.setTitle("库存变化明细");
    }
    private void InitData()
    {
        Intent intent = getIntent();
        String url = String.format(Url.SERVER_URL+Url.STOCKS_DETAIL_WITH_STOREID_PRODUCTID_INVENTORYID,intent.getIntExtra("store_id",0),intent.getIntExtra("product_id",0),intent.getIntExtra("inventory_id",0));
        LoadData(url,null);
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
        final WItems<Detail> items = new WItems<Detail>(response,Detail.class);
        if (items.status){

            ItemsAdapter adapter = new ItemsAdapter(this,items,new IItemsAdapter(){
                @Override
                public View getView(LayoutInflater inflater, int i, View convertView, ViewGroup parent)
                {
                    View view;
                    if (convertView == null){
                        view = inflater.inflate(R.layout.stocks_detail_item,parent,false);
                    }else{
                        view = convertView;
                    }
                    final Detail detail = items.Items.get(i);
                    TextView textName = (TextView)view.findViewById(R.id.txtMethod);
                    textName.setText(detail.Description());
                    TextView txtSpecification = (TextView)view.findViewById(R.id.txtSpecification);
                    txtSpecification.setText(detail.Specification);

                    ((TextView)((TextView) view.findViewById(R.id.txtUpdateDate))).setText(detail.UpdateDate);
                    ((TextView)((TextView) view.findViewById(R.id.txtBeforeUpdate))).setText(String.format("%1.2f",detail.BeforeUpdate));
                    ((TextView)((TextView) view.findViewById(R.id.txtQuantity))).setText(detail.Operation());
                    ((TextView)((TextView) view.findViewById(R.id.txtAfterUpdate))).setText(String.format("%1.2f",detail.AfterUpdate));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //nothing for now.
                        }
                    });

                    return view;
                }
            });
            ListView listStore = (ListView)findViewById(R.id.listMain);
            listStore.setAdapter(adapter);
        }
    }

}
