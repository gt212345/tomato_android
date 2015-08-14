package org.itri.tomato.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;

import org.itri.tomato.activities.AddAutoRunActivity;
import org.itri.tomato.DataRetrieveListener;
import org.itri.tomato.ListItem;
import org.itri.tomato.R;

import org.itri.tomato.activities.MarketListAdapter;
import org.itri.tomato.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hrw on 15/7/9.
 */
public class AutoRunListFragment extends Fragment implements AdapterView.OnItemClickListener, DataRetrieveListener
        , DialogFragment.RadioButtonListener, View.OnClickListener {
    private View rootView;
    ListView marketList;
    MarketListAdapter adapter;
    ArrayList<Bitmap> bitmaps;
    PullRefreshLayout pullRefreshLayout;
    DataRetrieveListener listener;
    ArrayList<ListItem> items;
    ArrayList<Integer> autoRunIDs;
    DialogFragment dialogFragment;
    SearchView searchView;
    String filterName = "all";
    SharedPreferences sharedPreferences;
    int page = 1, count = 20;

    @Override
    public void onPause() {
        super.onPause();
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancewe) {
        View rootView = inflater.inflate(R.layout.fragment_marketlist, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setHasOptionsMenu(true);
        createIconList();
        listener = AutoRunListFragment.this;
        getActivity().setTitle("AutoRun List");
        this.rootView = rootView;
//        items = createIconList();
        marketList = (ListView) rootView.findViewById(R.id.marketList);
        marketList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount && adapter != null) {
//                    count++;
//                    items = getAutoRunList();
//                    adapter.notifyDataSetChanged();
                }
            }
        });
        pullRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.pullRefreshLayout);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = new MarketListAdapter(getActivity(), getAutoRunList(), null);
            }
        });
        adapter = new MarketListAdapter(getActivity(), getAutoRunList(), null);
        return rootView;
    }

    void createIconList() {
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (!items.isEmpty()) {
            Intent intent = new Intent();
            intent.putExtra("autoRunId", autoRunIDs.get(position));
            intent.putExtra("content", items.get(position).getContent());
            intent.setClass(getActivity(), AddAutoRunActivity.class);
            startActivity(intent);
        }
    }

    private ArrayList<ListItem> getAutoRunList() {
        items = new ArrayList<>();
        autoRunIDs = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.setRefreshing(true);
                        marketList.setOnItemClickListener(null);
                    }
                });
                String Action = Utilities.ACTION + "GetAutoRunList";
                JSONObject para = new JSONObject();
                try {
                    para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                    para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                    para.put("filterName", filterName);
                    para.put("page", page);
                    para.put("count", count);
                } catch (JSONException e) {
                    Log.w("AutoRunListFragment", e.toString());
                }
                String Params = Utilities.PARAMS + para.toString();
                JSONObject jsonObject = Utilities.API_CONNECT(Action, Params, true);
                if (Utilities.getResponseCode().equals("true")) {
                    try {
                        JSONObject jsonObjectTmp = new JSONObject(jsonObject.getString("response"));
                        JSONArray jsonArray = new JSONArray(jsonObjectTmp.getString("autoruns"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            items.add(new ListItem(bitmaps.get(jsonArray.getJSONObject(i).getInt("whenIconId") - 1), bitmaps.get(jsonArray.getJSONObject(i).getInt("doIconId") - 1)
                                    , jsonArray.getJSONObject(i).getString("autorunDesc"), true, false, false));
                            autoRunIDs.add(jsonArray.getJSONObject(i).getInt("autorunId"));
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
                adapter.notifyDataSetChanged();
                pullRefreshLayout.setRefreshing(false);
                marketList.setOnItemClickListener(AutoRunListFragment.this);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_autorun, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                final String[] parts = {"all", "Weather", "Notification", "Mail", "Dropbox", "Facebook", "Location", "Phone"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(parts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        filterName = parts[i];
                        adapter = new MarketListAdapter(getActivity(), getAutoRunList(), null);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alertDialog.getWindow().getAttributes());
                lp.width = 700;
                lp.height = 900;
                lp.gravity = Gravity.RIGHT | Gravity.TOP;
                TypedValue tv = new TypedValue();
                int actionBarHeight = 70;
                if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics()) - 50;
                }
                lp.x = -100;
                lp.y = actionBarHeight;
                alertDialog.getWindow().setAttributes(lp);
//                dialogFragment = DialogFragment.newInstance(parts, Utilities.SEARCH_DIALOG, AutoRunListFragment.this);
//                dialogFragment.show(getFragmentManager(), "");
                return true;
            }
        });
        /**For future usage**/
//        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
//        searchView.setOnSearchClickListener(this);
    }

    @Override
    public void onRadioFinished(String query, int num, int isMap) {
//        searchView.clearFocus();
        filterName = query;
        adapter = new MarketListAdapter(getActivity(), getAutoRunList(), null);
    }

    @Override
    public void onClick(View view) {
//        String[] parts = {"Weather", "Notification", "Mail", "Dropbox", "Facebook", "Location", "Phone"};
//        DialogFragment dialogFragment = DialogFragment.newInstance(parts, Utilities.SEARCH_DIALOG, AutoRunListFragment.this);
//        dialogFragment.show(getFragmentManager(), "");
    }

}
