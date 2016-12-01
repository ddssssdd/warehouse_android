package com.stevenfu.warehouse;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.stevenfu.warehouse.adpaters.IItemsAdapter;
import com.stevenfu.warehouse.adpaters.ItemsAdapter;
import com.stevenfu.warehouse.models.Stores;
import com.stevenfu.warehouse.models.WItems;
import com.stevenfu.warehouse.network.CustomRequest;
import com.stevenfu.warehouse.network.WhRequest;
import com.stevenfu.warehouse.settings.App;
import com.stevenfu.warehouse.settings.Url;
import com.stevenfu.warehouse.users.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static int CODE_LOGIN = 0;
    private  NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        HandleLogin();


    }
    private void HandleLogin()
    {
        if (App.IsLogin(this))//load data then if logged
        {
            initData();
        }
        else//popup login if not login.
        {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent,CODE_LOGIN);
        }



    }
    private WhRequest<Stores> request;
    private void initData(){
        TextView txtMain = (TextView)findViewById(R.id.txtMain);
        txtMain.setText(String.format("Welcome %s",App.Username(this)));
        /*
        RequestQueue queue = Volley.newRequestQueue(this);

        request = new WhRequest<Stores>(this,Url.STORES,null,new IItemsAdapter(){

            @Override
            public View getView(LayoutInflater inflater, int i, View convertView, ViewGroup parent) {
                View view;
                if (convertView == null){
                    view = inflater.inflate(R.layout.store_item,parent,false);
                }else{
                    view = convertView;
                }

                TextView textName = (TextView)view.findViewById(R.id.txtName);
                final Stores store = storeList.get(i);
                textName.setText(store.Name);
                TextView textPhone = (TextView)view.findViewById(R.id.txtPhone);
                textPhone.setText(store.Phone);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SelectStore(store);
                    }
                });

                return view;
            }
        },Stores.class);
        request.BindListView = (ListView)findViewById(R.id.listStore);
        queue.add(request);
        */

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Url.SERVER_URL + Url.STORES;
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
    private ArrayList<Stores> storeList;
    private void HandleStoreData(JSONObject response)    {

        WItems<Stores> items = new WItems<>(response,Stores.class);
        if (items.status){
            storeList = items.Items;
            ItemsAdapter adapter = new ItemsAdapter(this,items,new IItemsAdapter(){
                @Override
                public  View getView(LayoutInflater inflater,int i, View convertView, ViewGroup parent)
                {
                    View view;
                    if (convertView == null){
                        view = inflater.inflate(R.layout.store_item,parent,false);
                    }else{
                        view = convertView;
                    }

                    TextView textName = (TextView)view.findViewById(R.id.txtName);
                    final Stores store = storeList.get(i);
                    textName.setText(store.Name);
                    TextView textPhone = (TextView)view.findViewById(R.id.txtPhone);
                    textPhone.setText(store.Phone);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SelectStore(store);
                        }
                    });

                    return view;
                }
            });
            ListView listStore = (ListView)findViewById(R.id.listStore);
            listStore.setAdapter(adapter);
        }
    }
    private void SelectStore(Stores store)
    {
        Intent intent = new Intent(MainActivity.this, StoreProductsActivity.class);
        intent.putExtra("store_id",store.Id);
        startActivity(intent);

    }


    private void HandleError(VolleyError err)
    {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode==CODE_LOGIN) { //for login
            Snackbar.make(this.getCurrentFocus(), "You have logged in.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
        */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            App.Logout(this);
            HandleLogin();
        } else if (id == R.id.nav_in) {

        } else if (id == R.id.nav_out) {

        } else if (id == R.id.nav_product) {

        } else if (id == R.id.nav_client) {

        } else if (id == R.id.nav_vendor) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
