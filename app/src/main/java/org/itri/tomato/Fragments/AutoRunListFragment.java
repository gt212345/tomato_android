package org.itri.tomato.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;

import org.itri.tomato.Activities.AddAutoRunActivity;
import org.itri.tomato.DataRetrieveListener;
import org.itri.tomato.ListItem;
import org.itri.tomato.R;

import org.itri.tomato.Activities.MarketListAdapter;
import org.itri.tomato.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hrw on 15/7/9.
 */
public class AutoRunListFragment extends Fragment implements AdapterView.OnItemClickListener, DataRetrieveListener {
    private View rootView;
    ListView marketList;
    MarketListAdapter adapter;
    ArrayList<ListItem> items;
    ArrayList<Bitmap> bitmaps;
    PullRefreshLayout pullRefreshLayout;
    Handler handler;
    DataRetrieveListener listener;
    ArrayList<Integer> autoRunIDs;
    boolean isMore = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_marketlist, container, false);
        autoRunIDs = new ArrayList<Integer>();
        createDummyList();
        listener = AutoRunListFragment.this;
        getActivity().setTitle("AutoRun List");
        this.rootView = rootView;
        handler = new Handler(Looper.getMainLooper());
//        items = createDummyList();
        marketList = (ListView) rootView.findViewById(R.id.marketList);
        marketList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount && adapter != null && isMore) {
//                    dummyLoadMore(items);
//                    adapter.notifyDataSetChanged();
                }
            }
        });
        pullRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.pullRefreshLayout);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MarketListAdapter(getActivity(), getAutoRunList());
                    }
                });
            }
        });
        adapter = new MarketListAdapter(getActivity(), getAutoRunList());
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                R.drawable.ring));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.person));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.email));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.person));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.noti));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.title));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.noti));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.title));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.ring));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.dropbox));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.email));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.dropbox));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.ring));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.email));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.ring));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.email));
        bitmaps.add(BitmapFactory.decodeResource(getActivity().getResources(),
                R.drawable.ring));
    }

    void dummyLoadMore(ArrayList<ListItem> items) {
        for (int i = 0; i < 5; i++) {
            if (items.size() > 70) {
                isMore = false;
            } else {
                isMore = true;
            }
            items.add(new ListItem(BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.fb), BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.email), "This AutoRun script will send you an email whenever you are invited to an event!", true, false));
            items.add(new ListItem(BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.github), BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.twitter), "This AutoRun script tweet out every detail of your commit of a certain project.", true, false));
            items.add(new ListItem(BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.in), BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.youtube), "This AutoRun script will do nothing!", true, false));
            items.add(new ListItem(BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.instagram), BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.gplus), "This AutoRun script will do nothing!", true, false));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("autoRunId", autoRunIDs.get(position));
        intent.putExtra("content", items.get(position).getContent());
        intent.setClass(getActivity(), AddAutoRunActivity.class);
        startActivity(intent);
    }

    private ArrayList<ListItem> getAutoRunList() {
        items = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String Action = Utilities.ACTION + "GetAutoRunList";
                String Params = Utilities.PARAMS + "{\"uid\":\"4\",\"token\":\"123\",\"filterName\":\"\",\"page\":\"1\",\"count\":\"10\"}";
                JSONObject jsonObject = Utilities.API_CONNECT(Action, Params, true);
                if (Utilities.getResponseCode() == 200) {
                    try {
                        JSONObject jsonObjectTmp = new JSONObject(jsonObject.getString("response"));
                        JSONArray jsonArray = new JSONArray(jsonObjectTmp.getString("autoruns"));
                        int a = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            int t = a + 1;
                            items.add(new ListItem(bitmaps.get(a), bitmaps.get(t), jsonArray.getJSONObject(i).getString("autorunDesc"), true, false));
                            autoRunIDs.add(jsonArray.getJSONObject(i).getInt("autorunId"));
                            a += 2;
                        }
                        listener.onFinish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return items;
    }

    @Override
    public void onFinish() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                marketList.setAdapter(adapter);
                marketList.setOnItemClickListener(AutoRunListFragment.this);
                adapter.notifyDataSetChanged();
                pullRefreshLayout.setRefreshing(false);
            }
        });
    }
}