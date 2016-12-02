package com.stevenfu.warehouse;

import com.stevenfu.warehouse.models.Clients;
import com.stevenfu.warehouse.models.Vendors;

import org.json.JSONObject;

/**
 * Created by Steven Fu on 12/02/2016.
 */

public class VendorEditActivity extends ClientEditActivity {
    private Vendors mVendor;
    @Override
    protected void HandleResponseData(JSONObject response)
    {
        showProgress(false);
        mVendor = new Vendors();
        if (response.optBoolean("status")){
            mVendor.InitDataWithJson(response.optJSONObject("result"));
        }

        edtName.setText(mVendor.Name);
        edtAddress.setText(mVendor.Address);
        edtPhone.setText(mVendor.Phone);
        edtFax.setText(mVendor.Fax);
        edtEmail.setText(mVendor.Email);
        edtContactCellphone.setText(mVendor.ContactCellphone);
        edtConttactName.setText(mVendor.ContactName);
        this.setTitle(String.format("供应商 -%s",mVendor.Name ));

    }
}
