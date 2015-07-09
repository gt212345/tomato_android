package org.itri.tomato.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.itri.tomato.MarketListItem;
import org.itri.tomato.R;

import org.itri.tomato.Activities.MarketListAdapter;

import java.util.ArrayList;

/**
 * Created by hrw on 15/7/9.
 */
public class MarketListFragment extends Fragment {
    private View rootView;

    ListView marketList;
    MarketListAdapter adapter;
    ArrayList<MarketListItem> items;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_marketlist,container,false);
        this.rootView = rootView;
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        marketList = (ListView) rootView.findViewById(R.id.marketList);
        adapter = new MarketListAdapter(getActivity(),items);
        marketList.setAdapter(adapter);
    }

    void createDummyList() {
        items = new ArrayList<>();

    }
}
