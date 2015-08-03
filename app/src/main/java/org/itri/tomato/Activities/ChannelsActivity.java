package org.itri.tomato.Activities;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;

import org.itri.tomato.ListItem;
import org.itri.tomato.R;
import org.itri.tomato.ToolbarControlBaseActivity;
import org.itri.tomato.Utilities;

import java.util.ArrayList;

public class ChannelsActivity extends ToolbarControlBaseActivity<ObservableGridView> {
    SharedPreferences sharedPreferences;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected int getLayoutResId() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getInt(Utilities.SDK_VERSION, -100) >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusBar));
        }
        return R.layout.activity_channels;
    }

    @Override
    protected ObservableGridView createScrollable() {
        ObservableGridView gridView = (ObservableGridView) findViewById(R.id.scrollable);
        MarketListAdapter marketListAdapter = new MarketListAdapter(this, createDummyList());
        gridView.setAdapter(marketListAdapter);
        gridView.setGravity(Gravity.CENTER_HORIZONTAL);
        gridView.setLeft(20);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent();
                        intent.setClass(ChannelsActivity.this, FacebookAuthActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent();
                        intent.setClass(ChannelsActivity.this, DropboxAuthActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        return gridView;
    }

    ArrayList<ListItem> createDummyList() {
        ArrayList<ListItem> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.fb), null, null, false, true));
            items.add(new ListItem(BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.dropbox), null, null, false, true));
        }
        return items;
    }

}
