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
 * {@link StocksInFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StocksInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StocksInFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;
    private View thisView;
    public StocksInFragment() {
        // Required empty public constructor
    }

    public static StocksInFragment newInstance(int index) {
        StocksInFragment fragment = new StocksInFragment();
        Bundle args = new Bundle();
        args.putInt("store_index_id", index);
        fragment.setArguments(args);
        return fragment;
    }
    private int store_index_id;
    private WItems<Stores> stores;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            store_index_id = getArguments().getInt("store_index_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        thisView = inflater.inflate(R.layout.fragment_stocks_in, container, false);
        InitData();
        return thisView;
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
            products =mListener.getProducts();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        WItems<Stores> getStores();
        WItems<Products> getProducts();
        void AddDetail(Products product,String specification,double price,double quantity,Stores store,String memo);
    }
    private EditText edtMemo;
    private EditText edtPrice;
    private EditText edtSpecification;
    private Spinner spProduct;
    private EditText edtQuantity;
    private Spinner spStore;
    private Button btnSave;
    private void InitData()
    {

        edtMemo = (EditText)thisView.findViewById(R.id.edtMemo);
        edtSpecification = (EditText)thisView.findViewById(R.id.edtSpecification);
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
            spStore.setSelection(store_index_id);

        }
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
    WItems<Products> products;
    Products current;

    private void selectProduct(int index){
        if (index>-1){
            current = products.Items.get(index);
            edtSpecification.setText(current.Specification);
            edtPrice.setText(String.format("%1.2f",current.Price));
            edtQuantity.setText("1");
        }
    }
    private void doSave(){

        double price = Double.parseDouble(edtPrice.getText().toString());
        double quantity = Double.parseDouble(edtQuantity.getText().toString());
        int index = spStore.getSelectedItemPosition();
        Stores store = stores.Items.get(index);
        mListener.AddDetail(current,edtSpecification.getText().toString(),price,quantity,store,edtMemo.getText().toString());
        dismiss();
    }
}
