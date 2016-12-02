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
import com.stevenfu.warehouse.models.Inventory;
import com.stevenfu.warehouse.models.Stores;
import com.stevenfu.warehouse.models.WItems;
import com.stevenfu.warehouse.network.WhRequest;
import com.stevenfu.warehouse.settings.Url;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StoreProductsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_products);

        InitData();
    }
    private int store_id;
    private void InitData(){
        Intent intent = getIntent();
        store_id = intent.getIntExtra("store_id",0);
        if (store_id>0){
            showProgress(true);
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = String.format(Url.SERVER_URL + Url.STOCKS_PRODUCTS,store_id);
            Map map = new HashMap();
            map.put("store_id",String.format("%d",store_id));

            WhRequest request = new WhRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    HandleStoreData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    HandleError(error);
                }
            });
            queue.add(request);
        }
        String storeName = intent.getStringExtra("store_name");
        this.setTitle(String.format("库存明细 - %s",storeName));

    }
    private void HandleStoreData(JSONObject response)    {
        showProgress(false);
        final WItems<Inventory> items = new WItems<>(response,Inventory.class);
        if (items.status){

            ItemsAdapter adapter = new ItemsAdapter(this,items,new IItemsAdapter(){
                @Override
                public View getView(LayoutInflater inflater, int i, View convertView, ViewGroup parent)
                {
                    View view;
                    if (convertView == null){
                        view = inflater.inflate(R.layout.store_products_item,parent,false);
                    }else{
                        view = convertView;
                    }

                    TextView textName = (TextView)view.findViewById(R.id.txtName);
                    final Inventory inventory = items.Items.get(i);
                    textName.setText(inventory.Name);
                    TextView textPhone = (TextView)view.findViewById(R.id.txtQuantity);
                    textPhone.setText(String.format("%1.2f",inventory.Quantity));
                    TextView txt1 = (TextView)view.findViewById(R.id.txtMinPrice);
                    txt1.setText(String.format("¥%1.2f",inventory.MinPrice));
                    TextView txt2 = (TextView)view.findViewById(R.id.txtMaxPrice);
                    txt2.setText(String.format("¥%1.2f",inventory.MaxPrice));
                    TextView txt3 = (TextView)view.findViewById(R.id.txtMinOutPrice);
                    txt3.setText(String.format("¥%1.2f",inventory.MinOutPrice));
                    TextView txt4 = (TextView)view.findViewById(R.id.txtMaxOutPrice);
                    txt4.setText(String.format("¥%1.2f",inventory.MaxOutPrice));
                    if (inventory.Specification!=null){
                        TextView txtSpecification = (TextView)view.findViewById(R.id.txtSpecification);
                        txtSpecification.setText(inventory.Specification);
                    }

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SelectStore(inventory);
                        }
                    });

                    return view;
                }
            });
            ListView listStore = (ListView)findViewById(R.id.listMain);
            listStore.setAdapter(adapter);
        }
    }
    private void SelectStore(Inventory store)
    {
        Intent intent = new Intent(this, StocksDetailActivity.class);
        intent.putExtra("store_id",store_id);
        intent.putExtra("product_id",store.ProductId);
        intent.putExtra("inventory_id",store.Id);
        startActivity(intent);
    }

}
