package org.itri.tomato.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;

import org.itri.tomato.Activities.EditAutoRunActivity;
import org.itri.tomato.Activities.MarketListAdapter;
import org.itri.tomato.DataRetrieveListener;
import org.itri.tomato.ListItem;
import org.itri.tomato.R;
import org.itri.tomato.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by heiruwu on 7/16/15.
 */
public class MyAutoRunListFragment extends Fragment implements DataRetrieveListener, AdapterView.OnItemClickListener, MarketListAdapter.toggleListener {
    SharedPreferences sharedPreferences;
    ListView autoRunList;
    ArrayList<Bitmap> bitmaps;
    ArrayList<ListItem> listItems;
    ArrayList<Integer> autoRunIDs;
    ArrayList<Boolean> able;
    DataRetrieveListener listener;
    PullRefreshLayout pullRefreshLayout;
    MarketListAdapter adapter;
//    private View rootView;

    private static final String TAG = "MyAutoRunListFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myautorunlist, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle("My AutoRun List");
//        this.rootView = rootView;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        createDummyList();
        autoRunList = (ListView) rootView.findViewById(R.id.autoRunList);
        listener = MyAutoRunListFragment.this;
        pullRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.pullRefreshLayout);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = new MarketListAdapter(getActivity(), getAutoRunList(), MyAutoRunListFragment.this);
            }
        });
        adapter = new MarketListAdapter(getActivity(), getAutoRunList(), MyAutoRunListFragment.this);
        return rootView;
    }

    @Override
    public void onFinish() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                autoRunList.setAdapter(adapter);
                autoRunList.setOnItemClickListener(MyAutoRunListFragment.this);
                adapter.notifyDataSetChanged();
                pullRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent();
        intent.putExtra("autoRunId", autoRunIDs.get(position));
        intent.putExtra("content", listItems.get(position).getContent());
        intent.setClass(getActivity(), EditAutoRunActivity.class);
        startActivity(intent);
    }

    private ArrayList<ListItem> getAutoRunList() {
        listItems = new ArrayList<>();
        able = new ArrayList<>();
        autoRunIDs = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.setRefreshing(true);
                    }
                });
                String Action = Utilities.ACTION + "GetUserAutoRunList";
                JSONObject para = new JSONObject();
                try {
                    para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                    para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                } catch (JSONException e) {
                    Log.w("JSON", e.toString());
                }
                String Params = Utilities.PARAMS + para.toString();
                JSONObject jsonObject = Utilities.API_CONNECT(Action, Params, true);
                if (Utilities.getResponseCode().equals("true")) {
                    try {
                        JSONObject jsonObjectTmp = new JSONObject(jsonObject.getString("response"));
                        JSONArray jsonArray = new JSONArray(jsonObjectTmp.getString("autoruns"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (jsonArray.getJSONObject(i).getString("enable").equals("on")) {
                                able.add(true);
                            } else {
                                able.add(false);
                            }
                            listItems.add(new ListItem(bitmaps.get(jsonArray.getJSONObject(i).getInt("whenIconId") - 1), bitmaps.get(jsonArray.getJSONObject(i).getInt("doIconId") - 1)
                                    , jsonArray.getJSONObject(i).getString("autorunDesc"), true, true, able.get(i)));
                            autoRunIDs.add(jsonArray.getJSONObject(i).getInt("userautorunId"));
                        }
                        listener.onFinish();
                    } catch (JSONException e) {
                        Log.w("JSON", e.toString());
                    }
                }
            }
        }).start();
        return listItems;
    }

    void createDummyList() {
        bitmaps = new ArrayList<>();
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.raining));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.noti));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.home));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.email));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.person));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.email));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.email));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.ring));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.fb));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.dropbox));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.noti));
    }

    @Override
    public void onToggle(final int position, final boolean able) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int id = autoRunIDs.get(position);
                String Action = Utilities.ACTION + "SwitchUserAutoRunById";
                JSONObject para = new JSONObject();
                try {
                    para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                    para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                    para.put("userautorunId", id);
                    para.put("enable", able ? "on" : "off");
                } catch (JSONException e) {
                    Log.w(TAG, e.toString());
                }
                String Para = Utilities.PARAMS + para.toString();
                Utilities.API_CONNECT(Action, Para, true);
            }
        }).start();
    }
}
