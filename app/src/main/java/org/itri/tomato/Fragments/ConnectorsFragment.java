package org.itri.tomato.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import org.itri.tomato.activities.DropboxAuthActivity;
import org.itri.tomato.activities.FacebookAuthActivity;
import org.itri.tomato.R;

/**
 * Created by heiruwu on 8/4/15.
 */
public class ConnectorsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connectors, container, false);
        getActivity().setTitle("Connector List");
        ImageView fb = (ImageView) rootView.findViewById(R.id.fbIcon);
        ImageView db = (ImageView) rootView.findViewById(R.id.dbIcon);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FacebookAuthActivity.class);
                startActivity(intent);
            }
        });
        db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), DropboxAuthActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
