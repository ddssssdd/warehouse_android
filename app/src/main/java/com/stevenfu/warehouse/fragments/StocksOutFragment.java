package com.stevenfu.warehouse.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.stevenfu.warehouse.R;
import com.stevenfu.warehouse.models.Products;
import com.stevenfu.warehouse.models.Stores;
import com.stevenfu.warehouse.models.WItems;
import com.stevenfu.warehouse.network.WhRequest;
import com.stevenfu.warehouse.settings.Url;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StocksOutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StocksOutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StocksOutFragment extends DialogFragment {


    private OnFragmentInteractionListener mListener;

    public StocksOutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     */

    public static StocksOutFragment newInstance(int index) {
        StocksOutFragment fragment = new StocksOutFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int index = getArguments().getInt("index");
        }
    }
    private View thisView;
    private WItems<Stores> stores;
    private WItems<Products> products;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisView =  inflater.inflate(R.layout.fragment_stocks_out, container, false);
        InitData();
        return  thisView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            stores = mListener.getStores();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
        WItems<Stores> getStores();
        void AddDetail(Products product,double price,double quantity,Stores store,String memo);
    }
    private EditText edtMemo;
    private EditText edtPrice;
    private Spinner spProduct;
    private EditText edtQuantity;
    private Spinner spStore;

    private Button btnSave;
    private void InitData()
    {

        edtMemo = (EditText)thisView.findViewById(R.id.edtMemo);

        edtPrice = (EditText)thisView.findViewById(R.id.edtPrice);
        spProduct =(Spinner)thisView.findViewById(R.id.spProduct);
        edtQuantity = (EditText)thisView.findViewById(R.id.edtQuantity);
        spStore = (Spinner)thisView.findViewById(R.id.spStore);
        btnSave = (Button) thisView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSave();
            }
        });
        if (stores.status){
            ArrayList<String> arrayItems = stores.DescriptionList();
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,arrayItems);
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

    }
    private void selectStore(int index){
        if (index <0){
            return;
        }
        Stores store = stores.Items.get(index);
        //init products;
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url_product = String.format(Url.SERVER_URL + Url.STOCKS_PRODUCTS,store.Id);
        WhRequest request_product = new WhRequest(this.getActivity(), url_product, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleProductsResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request_product);
    }
    private void handleProductsResponse(JSONObject res){
        products = new WItems<>(res, Products.class);
        if (products.status){
            ArrayList<String> arrayItems = products.DescriptionList();
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,arrayItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spProduct.setAdapter(adapter);
            spProduct.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectProduct(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    selectProduct(-1);
                }
            });
        }
    }

    Products current;

    private void selectProduct(int index){
        if (index>-1){
            current = products.Items.get(index);

            edtPrice.setText(String.format("%1.2f",current.Price));
            edtQuantity.setText("1");
        }
    }
    private void doSave(){

        double price = Double.parseDouble(edtPrice.getText().toString());
        double quantity = Double.parseDouble(edtQuantity.getText().toString());
        int index = spStore.getSelectedItemPosition();
        Stores store = stores.Items.get(index);
        mListener.AddDetail(current,price,quantity,store,edtMemo.getText().toString());
        dismiss();
    }
}
