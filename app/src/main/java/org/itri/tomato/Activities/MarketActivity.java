package org.itri.tomato.Activities;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.itri.tomato.Fragments.MyAutoRunListFragment;
import org.itri.tomato.ListItem;
import org.itri.tomato.Utilities;
import org.itri.tomato.R;
import org.itri.tomato.Fragments.MarketListFragment;
import org.itri.tomato.Services.RegistrationIntentService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;

public class MarketActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    FragmentManager fragmentManager;
    Fragment fragment;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;

    /**
     * Google Cloud Messaging
     */
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                drawerLayout.closeDrawers();
                fragment = new MarketListFragment();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 1:
                drawerLayout.closeDrawers();
                fragment = new MyAutoRunListFragment();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 2:
                intent = new Intent();
                intent.setClass(MarketActivity.this, ChannelsActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
                break;
            case 3:
                drawerLayout.closeDrawers();
                break;
            case 4:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                sharedPreferences.edit().remove(Utilities.USER_ACCOUNT).apply();
                sharedPreferences.edit().remove(Utilities.USER_PASSWORD).apply();
                sharedPreferences.edit().remove(Utilities.HAS_ACCOUNT).apply();
                sharedPreferences.edit().putBoolean(Utilities.HAS_ACCOUNT, false).apply();
                drawerLayout.closeDrawers();
                intent = new Intent();
                intent.setClass(MarketActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getInt(Utilities.SDK_VERSION, -100) >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusBar));
        }
        APITest();
        ListView sideView = (ListView) findViewById(R.id.drawer_view);
        sideView.setOnItemClickListener(this);
        MarketListAdapter adapter = new MarketListAdapter(this,createDummyList());
        sideView.setAdapter(adapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_market);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Market List");
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        toggle.syncState();
        drawerLayout.setDrawerListener(toggle);
        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragment = new MarketListFragment();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(Utilities.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.i("Server","registered");
                } else {
                    Log.i("Server", "registration failed");
                }
            }
        };
    }


    ArrayList<ListItem> createDummyList() {
        ArrayList<ListItem> items = new ArrayList<>();
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.marketlist), null, "Market List", false, false));
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.my), null, "My AutoRuns",false, false));
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.channels), null, "Channels", false, false));
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.settings), null, "Settings", false, false));
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.logout), null, "Log out", false, false));
        return items;
    }

    private void APITest () {
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(code)) {
            GooglePlayServicesUtil.getErrorDialog(code,this,200);
        }
        if (code == ConnectionResult.SUCCESS) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        } else {
            Toast.makeText(this,"請更新 Google Play Service!!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Utilities.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

}
