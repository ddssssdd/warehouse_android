package com.stevenfu.warehouse;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.stevenfu.warehouse.adpaters.IItemsAdapter;
import com.stevenfu.warehouse.adpaters.ItemsAdapter;
import com.stevenfu.warehouse.models.Clients;
import com.stevenfu.warehouse.models.Vendors;
import com.stevenfu.warehouse.models.WItems;
import com.stevenfu.warehouse.settings.Url;

import org.json.JSONObject;

/**
 * Created by Steven Fu on 12/02/2016.
 */

public class VendorActivity extends ClientActivity {
    @Override
    protected void Init_activity() {
        editActivityClass = VendorEditActivity.class;
        mEditUrl = Url.VENDOR_EDIT;
        mAddurl = Url.VENDOR_ADD;
        mGetUrl = Url.VENDORS;
        mFindUrl = Url.VENDOR_FIND;
        this.setTitle(String.format("供应商"));
    }
    @Override
    protected void HandleResponseData(JSONObject response)    {

        final WItems<Vendors> items = new WItems<>(response,Vendors.class);
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
                    final Vendors vendor = items.Items.get(i);
                    textName.setText(vendor.Name);
                    TextView textPhone = (TextView)view.findViewById(R.id.txtPhone);
                    textPhone.setText(vendor.Phone);
                    TextView txt1 = (TextView)view.findViewById(R.id.txtAddress);
                    txt1.setText(vendor.Address);

                    TextView txtSpecification = (TextView)view.findViewById(R.id.txtContact);
                    txtSpecification.setText(vendor.ContactName);


                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SelectItem(vendor.Id);
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
