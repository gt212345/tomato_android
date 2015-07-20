package org.itri.tomato.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import org.itri.tomato.ListItem;
import org.itri.tomato.QuickStartPreferences;
import org.itri.tomato.R;
import org.itri.tomato.Fragments.MarketListFragment;
import org.itri.tomato.Services.RegistrationIntentService;

import java.util.ArrayList;

public class MarketActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    FragmentManager fragmentManager;
    Fragment fragment;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;

    /**
     * Google Cloud Messaging
     */
    private static final String SENDER_ID = "948528150442";
    private static final String SERVER_API_KEY = "AIzaSyBerIFX1Y_5mp0c1chZQnA5WL-oaDXrNnA";
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
            case 0:
//                fragment = new
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
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
        fragment = new MarketListFragment();
        fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickStartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.i("GCM","registered");
                } else {
                    Log.i("GCM", "registration failed");
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_market, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //TODO create own icons
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_search) {
            //TODO override search behave
        }

        return super.onOptionsItemSelected(item);
    }

    ArrayList<ListItem> createDummyList() {
        ArrayList<ListItem> items = new ArrayList<>();
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.fb), null, "My AutoRuns",false));
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.github), null, "Channels",false));
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.email), null, "Settings",false));
        items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.youtube), null, "Log out", false));
        return items;
    }

    private void APITest () {
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(code)) {
            GooglePlayServicesUtil.getErrorDialog(code,this,200);
        }
        if (code == ConnectionResult.SUCCESS) {
            Toast.makeText(this,"Google API Available",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(QuickStartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

}
