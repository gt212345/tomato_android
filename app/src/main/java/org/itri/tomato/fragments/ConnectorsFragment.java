package org.itri.tomato.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.itri.tomato.DataRetrieveListener;
import org.itri.tomato.Utilities;
import org.itri.tomato.activities.DropboxAuthActivity;
import org.itri.tomato.activities.FacebookAuthActivity;
import org.itri.tomato.R;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by heiruwu on 8/4/15.
 */
public class ConnectorsFragment extends Fragment implements DataRetrieveListener{
    SharedPreferences sharedPreferences;
    DataRetrieveListener dataRetrieveListener;
    ProgressDialog progressDialog;
    ImageView fb;
    ImageView db;
    boolean isFB = false;
    boolean isDB = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connectors, container, false);
        progressDialog = ProgressDialog.show(getActivity(), "載入中", "請稍等......", false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getActivity().setTitle("Connector List");
        dataRetrieveListener = ConnectorsFragment.this;
        fb = (ImageView) rootView.findViewById(R.id.fbIcon);
        db = (ImageView) rootView.findViewById(R.id.dbIcon);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("enable", isFB);
                intent.setClass(getActivity(), FacebookAuthActivity.class);
                startActivity(intent);
            }
        });
        db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("enable", isDB);
                intent.setClass(getActivity(), DropboxAuthActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(checkConnector).start();
    }

    Runnable checkConnector = new Runnable() {
        @Override
        public void run() {
            String Action = Utilities.ACTION + "GetConnectorStatusById";
            JSONObject para = new JSONObject();
            try {
                para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                para.put("connectorId", "1");
            } catch (JSONException e) {
            }
            String Para = Utilities.PARAMS + para.toString();
            Utilities.API_CONNECT(Action, Para, getActivity(), true);
            if (Utilities.getResponseCode().equals("true")) {
                isFB = true;
            } else {
                isFB = false;
            }
            Action = Utilities.ACTION + "GetConnectorStatusById";
            para = new JSONObject();
            try {
                para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                para.put("connectorId", "2");
            } catch (JSONException e) {
            }
            Para = Utilities.PARAMS + para.toString();
            Utilities.API_CONNECT(Action, Para, getActivity(), true);
            if (Utilities.getResponseCode().equals("true")) {
                isDB = true;
            } else {
                isDB = false;
            }
            dataRetrieveListener.onFinish();
        }
    };

    @Override
    public void onFinish() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDB) {
                    db.setBackgroundColor(Color.parseColor("#00000000"));
                } else {
                    db.setBackgroundColor(Color.parseColor("#22000000"));
                }
                if (isFB) {
                    fb.setBackgroundColor(Color.parseColor("#00000000"));
                } else {
                    fb.setBackgroundColor(Color.parseColor("#22000000"));
                }
            }
        });
        progressDialog.dismiss();
    }
}
