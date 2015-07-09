package org.itri.tomato.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
        items = createDummyList();
        marketList = (ListView) rootView.findViewById(R.id.marketList);
        adapter = new MarketListAdapter(getActivity(),items);
        marketList.setAdapter(adapter);
    }

    ArrayList<MarketListItem> createDummyList() {
        ArrayList<MarketListItem> items = new ArrayList<>();
        for (int i = 0;i<10;i++) {
            items.add(new MarketListItem(BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.fb), BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.email), "This AutoRun script will send you an email whenever you are invited to an event!"));
            items.add(new MarketListItem(BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.github), BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.twitter), "This AutoRun script tweet out every detail of your commit of a certain project."));
            items.add(new MarketListItem(BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.in), BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.youtube), "This AutoRun script will do nothing!"));
            items.add(new MarketListItem(BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.instagram), BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.gplus), "This AutoRun script will do nothing!"));
        }
        return items;
    }
}
