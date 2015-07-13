package org.itri.tomato.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.itri.tomato.MarketListItem;
import org.itri.tomato.R;

import org.itri.tomato.Fragments.MarketListFragment;

import java.util.ArrayList;

public class MarketActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    FragmentManager fragmentManager;
    Fragment fragment;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        ListView sideView = (ListView) findViewById(R.id.drawer_view);
        sideView.setOnItemClickListener(this);
        MarketListAdapter adapter = new MarketListAdapter(this,createDummyList());
        sideView.setAdapter(adapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_market);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    ArrayList<MarketListItem> createDummyList() {
        ArrayList<MarketListItem> items = new ArrayList<>();
        items.add(new MarketListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.fb), null, "My Account",false));
        items.add(new MarketListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.github), null, "Channels",false));
        items.add(new MarketListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.email), null, "Settings",false));
        items.add(new MarketListItem(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.youtube), null, "Log out",false));
        return items;
    }
}
