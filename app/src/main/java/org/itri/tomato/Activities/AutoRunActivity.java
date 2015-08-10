package org.itri.tomato.Activities;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.itri.tomato.Fragments.AutoRunListFragment;
import org.itri.tomato.Fragments.ConnectorsFragment;
import org.itri.tomato.Fragments.MyAutoRunListFragment;
import org.itri.tomato.ListItem;
import org.itri.tomato.R;
import org.itri.tomato.Services.RegistrationIntentService;
import org.itri.tomato.Utilities;

import java.util.ArrayList;

public class AutoRunActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
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
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                drawerLayout.closeDrawers();
                fragment = new AutoRunListFragment();
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
                drawerLayout.closeDrawers();
                fragment = new ConnectorsFragment();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 3:
                drawerLayout.closeDrawers();
                break;
            case 4:
                new AlertDialog.Builder(AutoRunActivity.this)
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AutoRunActivity.this);
                                sharedPreferences.edit().remove(Utilities.USER_ACCOUNT).apply();
                                sharedPreferences.edit().remove(Utilities.USER_PASSWORD).apply();
                                sharedPreferences.edit().remove(Utilities.HAS_ACCOUNT).apply();
                                sharedPreferences.edit().putBoolean(Utilities.HAS_ACCOUNT, false).apply();
                                drawerLayout.closeDrawers();
                                Intent intent = new Intent();
                                intent.setClass(AutoRunActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                drawerLayout.closeDrawers();
                            }
                        })
                        .show();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autorun);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getInt(Utilities.SDK_VERSION, -100) >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusBar));
        }
        APITest();
        ListView sideView = (ListView) findViewById(R.id.drawer_view);
        sideView.setOnItemClickListener(this);
        MarketListAdapter adapter = new MarketListAdapter(this, createDrawerMenu(), null);
        sideView.setAdapter(adapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
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
        if (savedInstanceState == null && getIntent().getExtras().getString("from").equals("AddAutoRunActivity")) {
            fragment = new MyAutoRunListFragment();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if ((savedInstanceState == null && getIntent().getExtras().getString("from").equals("EditAutoRunActivity"))) {
            fragment = new MyAutoRunListFragment();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (savedInstanceState == null) {
            fragment = new AutoRunListFragment();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(Utilities.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.i("Server", "registered");
                } else {
                    Log.i("Server", "registration failed");
                }
            }
        };
    }


    ArrayList<ListItem> createDrawerMenu() {
        ArrayList<ListItem> items = new ArrayList<>();
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.marketlist), null, "AutoRun List", false, false, false));
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.my), null, "My AutoRun List", false, false, false));
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.channels), null, "Connector List", false, false, false));
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.settings), null, "Log", false, false, false));
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.logout), null, "Sign out", false, false, false));
        return items;
    }

    private void APITest() {
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(code)) {
            GooglePlayServicesUtil.getErrorDialog(code, this, 200);
        }
        if (code == ConnectionResult.SUCCESS) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        } else {
            Toast.makeText(this, "請更新 Google Play Service!!", Toast.LENGTH_SHORT).show();
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
