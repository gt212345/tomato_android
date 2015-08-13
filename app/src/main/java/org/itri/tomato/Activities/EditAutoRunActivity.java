package org.itri.tomato.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.itri.tomato.AutoRunItem;
import org.itri.tomato.DataRetrieveListener;
import org.itri.tomato.fragments.DialogFragment;
import org.itri.tomato.R;
import org.itri.tomato.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditAutoRunActivity extends AppCompatActivity implements DataRetrieveListener, View.OnClickListener, DialogFragment.CheckBoxListener
        , DialogFragment.RadioButtonListener {
    private final static String TAG = "EditAutoRunActivity";
    boolean isMapCreated = false;
    String id;
    SharedPreferences sharedPreferences;
    DialogFragment dialogFragment;
    Toast toast;
    LinearLayout layout;
    String description;
    ArrayList<AutoRunItem> autoRunItemsWhen, autoRunItemsDo;
    DataRetrieveListener dataRetrieveListener;
    LinearLayout mapLayout;
    TextView weightTv, lat, lng, check, radio, region;
    Button weightBt;
    String[] parts;
    String latStr, lngStr;
    ProgressDialog progressDialog;
    Geocoder geocoder;
    List<Address> addressList;
    String dialogStr;
    ArrayList<JSONObject> checkList, radioList, phoneList, emailList, textList, numList, passList, richList, schList;
    EditText phone, text, number, pass, email, rich;
    LocationManager manager;
    Toolbar toolbar;
    int countCheck = 0, countRadio = 0, countPhone = 0, countEmail = 0, countText = 0, countNum = 0, countPass = 0, countRich = 0, countSch = 0;
    double latD = 0;
    double lngD = 0;
    JSONObject jsonMapLat, jsonMapLng;
    String jsonPara;
    int counts;
    final static int mRunBtnId = 2;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        progressDialog = ProgressDialog.show(this, "載入中", "請稍等......", false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getInt(Utilities.SDK_VERSION, -100) >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBar));
        }
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        dataRetrieveListener = EditAutoRunActivity.this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        geocoder = new Geocoder(this, Locale.TAIWAN);
        new Thread(getAutoRunSettings).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.cancel();
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    @Override
    public void onCheckFinished(ArrayList<String> dataList, int num) {
        if (dataList.isEmpty()) {
            return;
        }
        check.setText("");
        String temp = "";
        for (String tmp : dataList) {
            check.append(tmp + "\n");
            temp += tmp + "|";
        }
        try {
            checkList.get(num - 1).put("value", temp.substring(0, temp.length() - 1));
            checkList.get(num - 1).put("agent_parameter", "options");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRadioFinished(String string, int num, boolean isMap) {
        radio.setText("");
        radio.append(string);
        try {
            radioList.get(num).put("value", string);
            radioList.get(num).put("agent_parameter", "options");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createList();
                layout = (LinearLayout) findViewById(R.id.viewGroup1);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideSoftKeyboard(EditAutoRunActivity.this);
                    }
                });
                TextView title = new TextView(getApplicationContext());
                title.setText("設定");
                title.setGravity(Gravity.CENTER);
                title.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                title.setTextSize(40);
                layout.addView(title);
                if (autoRunItemsWhen.size() != 0) {
                    TextView whenTitle = new TextView(getApplicationContext());
                    whenTitle.setTextColor(getResources().getColor(android.R.color.white));
                    whenTitle.setBackgroundColor(getResources().getColor(android.R.color.black));
                    whenTitle.setText("When:");
                    whenTitle.setTextSize(20);
                    layout.addView(whenTitle);
                    for (AutoRunItem item : autoRunItemsWhen) {
                        createView(item);
                    }
                }
                if (autoRunItemsDo.size() != 0) {
                    TextView doTitle = new TextView(getApplicationContext());
                    doTitle.setTextColor(getResources().getColor(android.R.color.white));
                    doTitle.setBackgroundColor(getResources().getColor(android.R.color.black));
                    doTitle.setText("Do:");
                    doTitle.setTextSize(20);
                    layout.addView(doTitle);
                    for (AutoRunItem item : autoRunItemsDo) {
                        createView(item);
                    }
                }
                LinearLayout.LayoutParams params;

                Button runNow = new Button(getApplicationContext());
                runNow.setTextSize(20);
                runNow.setText("RunNow");
                runNow.setId(mRunBtnId);
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                runNow.setLayoutParams(params);
                runNow.setOnClickListener(EditAutoRunActivity.this);
                layout.addView(runNow);

                Button apply = new Button(getApplicationContext());
                apply.setTextSize(20);
                apply.setText("Apply");
                apply.setId(0);
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                apply.setLayoutParams(params);
                apply.setOnClickListener(EditAutoRunActivity.this);
                layout.addView(apply);
                apply = new Button(getApplicationContext());
                apply.setTextSize(20);
                apply.setText("Delete");
                apply.setId(-1);
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                apply.setLayoutParams(params);
                apply.setOnClickListener(EditAutoRunActivity.this);
                layout.addView(apply);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 200) {
            latD = roundDown5(data.getDoubleExtra("lat", 0));
            lngD = roundDown5(data.getDoubleExtra("lng", 0));
            lat.setText(latStr + ": ");
            lng.setText(lngStr + ": ");
            lat.append(String.valueOf(latD));
            lng.append(String.valueOf(lngD));
            try {
                addressList = geocoder.getFromLocation(latD, lngD, 1);
                if (!addressList.isEmpty()) {
                    region.setText(addressList.get(0).getAddressLine(0));
                }
                jsonMapLat.put("value", String.valueOf(latD));
                jsonMapLat.put("agent_parameter", "options");
                jsonMapLng.put("value", String.valueOf(lngD));
                jsonMapLng.put("agent_parameter", "options");
            } catch (IOException e) {
                Log.w("Region", e.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                JSONArray jsonArray = new JSONArray();
                ArrayList<JSONObject> tmp = new ArrayList<>();
                tmp.add(jsonMapLat);
                tmp.add(jsonMapLng);
                iterateList(tmp, checkList);
                iterateList(tmp, radioList);
                iterateList(tmp, phoneList);
                iterateList(tmp, emailList);
                iterateList(tmp, passList);
                iterateList(tmp, textList);
                iterateList(tmp, richList);
                iterateList(tmp, numList);
                for (JSONObject object : tmp) {
                    if (object != null && object.length() == 4) {
                        jsonArray.put(object);
                    } else {
                    }
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("uid", sharedPreferences.getString(Utilities.USER_ID, ""));
                    jsonObject.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, ""));
                    jsonObject.put("userautorunId", id);
                    jsonObject.put("autorunPara", jsonArray);
                    jsonPara = jsonObject.toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String Action = Utilities.ACTION + "UpdateUserAutoRunById";
                            String Params = Utilities.PARAMS + jsonPara;
                            Utilities.API_CONNECT(Action, Params, true);
                            if (Utilities.getResponseCode().equals("true")) {
                                toast.setText("Edit Succeed");
                                toast.show();
                            }
                        }
                    }).start();
                    Intent intent = new Intent();
                    intent.putExtra("from", TAG);
                    intent.setClass(EditAutoRunActivity.this, AutoRunActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case -1:
                JSONObject jsonObjectDelete = new JSONObject();
                try {
                    jsonObjectDelete.put("uid", sharedPreferences.getString(Utilities.USER_ID, ""));
                    jsonObjectDelete.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, ""));
                    jsonObjectDelete.put("userautorunId", id);
                    jsonPara = jsonObjectDelete.toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String Action = Utilities.ACTION + "DelUserAutoRunById";
                            String Params = Utilities.PARAMS + jsonPara;
                            Utilities.API_CONNECT(Action, Params, true);
                            if (Utilities.getResponseCode().equals("true")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast.setText("AutoRun Deleted");
                                        toast.show();
                                    }
                                });
                            }
                        }
                    }).start();
                    Intent intent = new Intent();
                    intent.putExtra("from", TAG);
                    intent.setClass(EditAutoRunActivity.this, AutoRunActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case mRunBtnId:
                JSONObject jsonObjectRunNow = new JSONObject();
                try {
                    jsonObjectRunNow.put("uid", sharedPreferences.getString(Utilities.USER_ID, ""));
                    jsonObjectRunNow.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, ""));
                    jsonObjectRunNow.put("userautorunId", id);
                    jsonPara = jsonObjectRunNow.toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String Action = Utilities.ACTION + "RunNow";
                            String Params = Utilities.PARAMS + jsonPara;
                            Utilities.API_CONNECT(Action, Params, true);
                            if (Utilities.getResponseCode().equals("true")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast.setText("AutoRun Run Complete");
                                        toast.show();
                                    }
                                });
                            }
                        }
                    }).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void startActivity() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            toast.setText("Enable Location first");
            toast.show();
        } else {
            Intent intent = new Intent();
            if (latD != 0 && lngD != 0) {
                intent.putExtra("Lat", latD);
                intent.putExtra("Lng", lngD);
            }
            intent.setClass(EditAutoRunActivity.this, MapActivity.class);
            startActivityForResult(intent, 200);
        }
    }

    Runnable getAutoRunSettings = new Runnable() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            autoRunItemsWhen = new ArrayList<>();
            autoRunItemsDo = new ArrayList<>();
            String Action = Utilities.ACTION + "GetUserAutoRunDetailById";
            JSONObject para = new JSONObject();
            try {
                para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                para.put("userautorunId", getIntent().getExtras().getInt("autoRunId"));
            } catch (JSONException e) {
                Log.w(TAG, e.toString() + "Para");
            }
            String Params = Utilities.PARAMS + para.toString();
            JSONObject jsonObject = Utilities.API_CONNECT(Action, Params, true);
            try {
                JSONObject jsonRes = new JSONObject(jsonObject.getString("response"));
                description = jsonRes.getString("autorunDesc");
                id = jsonRes.getString("autorunId");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle("Edit AutoRun");
                    }
                });
                JSONObject jsonPara = new JSONObject(jsonRes.getString("autorunPara"));
                JSONArray jsonWhen = new JSONArray(jsonPara.getString("when"));
                for (int i = 0; i < jsonWhen.length(); i++) {
                    autoRunItemsWhen.add(new AutoRunItem(
                            jsonWhen.getJSONObject(i).getString("agentId"),
                            jsonWhen.getJSONObject(i).getString("display"),
                            jsonWhen.getJSONObject(i).getString("option"),
                            jsonWhen.getJSONObject(i).getString("conditionType"),
                            jsonWhen.getJSONObject(i).getString("condition"),
                            jsonWhen.getJSONObject(i).getString("agent_parameter"),
                            jsonWhen.getJSONObject(i).getString("value")
                    ));
                }
                JSONArray jsonDo = new JSONArray(jsonPara.getString("do"));
                for (int i = 0; i < jsonDo.length(); i++) {
                    autoRunItemsDo.add(new AutoRunItem(
                            jsonDo.getJSONObject(i).getString("agentId"),
                            jsonDo.getJSONObject(i).getString("display"),
                            jsonDo.getJSONObject(i).getString("option"),
                            jsonDo.getJSONObject(i).getString("conditionType"),
                            jsonDo.getJSONObject(i).getString("condition"),
                            jsonDo.getJSONObject(i).getString("agent_parameter"),
                            jsonDo.getJSONObject(i).getString("value")
                    ));
                }
                counts = jsonWhen.length() + jsonDo.length();
                dataRetrieveListener.onFinish();
            } catch (JSONException e) {
                progressDialog.cancel();
                Log.w("Json", e.toString());
            }
        }
    };

    private void createView(AutoRunItem item) {
        String condition;
        LinearLayout.LayoutParams params;
        switch (item.getConditionType()) {
            case "map":
                if (!isMapCreated) {
                    lat = new TextView(getApplicationContext());
                    lng = new TextView(getApplicationContext());
                    if (item.getOption().equals("sp_lat") || item.getOption().equals("latitude")) {
                        jsonMapLat = new JSONObject();
                        putJson(jsonMapLat, item);
                        lat.setText(item.getDisplay() + ": " + item.getValue());
                        lat.setTextSize(20);
                        lat.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                        latStr = item.getDisplay();
                        try {
                            jsonMapLat.put("value", item.getValue());
                            jsonMapLat.put("agent_parameter", "options");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        jsonMapLng = new JSONObject();
                        putJson(jsonMapLng, item);
                        lng.setText(item.getDisplay() + ": " + item.getValue());
                        lngStr = item.getDisplay();
                        lng.setTextSize(20);
                        lng.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                        try {
                            jsonMapLng.put("value", item.getValue());
                            jsonMapLng.put("agent_parameter", "options");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    checkGps();
                    mapLayout = new LinearLayout(getApplicationContext());
                    mapLayout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.addView(mapLayout);
                    weightTv = new TextView(getApplicationContext());
                    weightTv.setText("請選擇位置:");
                    weightTv.setGravity(Gravity.CENTER_VERTICAL);
                    weightTv.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                    weightTv.setTextSize(20);
                    params = new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            3.0f
                    );
                    weightTv.setLayoutParams(params);
                    weightBt = new Button(getApplicationContext());
                    weightBt.setText("地圖");
                    params = new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1.0f
                    );
                    weightBt.setLayoutParams(params);
                    mapLayout.addView(weightTv);
                    mapLayout.addView(weightBt);
                    weightBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity();
                        }
                    });
                    region = new TextView(getApplicationContext());
                    layout.addView(region);
                    layout.addView(lat);
                    layout.addView(lng);
                    region.setTextSize(20);
                    region.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                    isMapCreated = true;
                } else {
                    if (item.getOption().equals("sp_lat") || item.getOption().equals("latitude")) {
                        jsonMapLat = new JSONObject();
                        putJson(jsonMapLat, item);
                        lat.setText(item.getDisplay() + ": " + item.getValue());
                        latStr = item.getDisplay();
                        lat.setTextSize(20);
                        lat.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                        try {
                            jsonMapLat.put("value", item.getValue());
                            jsonMapLat.put("agent_parameter", "options");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        jsonMapLng = new JSONObject();
                        putJson(jsonMapLng, item);
                        lng.setText(item.getDisplay() + ": " + item.getValue());
                        lngStr = item.getDisplay();
                        lng.setTextSize(20);
                        lng.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                        try {
                            jsonMapLng.put("value", item.getValue());
                            jsonMapLng.put("agent_parameter", "options");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case "checkbox":
                checkList.add(putJson(new JSONObject(), item));
                condition = item.getCondition();
                parts = condition.split("\\|");
                String[] tmp = item.getValue().split("\\|");
                mapLayout = new LinearLayout(getApplicationContext());
                mapLayout.setOrientation(LinearLayout.HORIZONTAL);
                layout.addView(mapLayout);
                weightTv = new TextView(getApplicationContext());
                weightTv.setText(item.getDisplay() + ":");
                weightTv.setGravity(Gravity.CENTER_VERTICAL);
                weightTv.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                weightTv.setTextSize(20);
                params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        3.0f
                );
                weightTv.setLayoutParams(params);

                weightBt = new Button(getApplicationContext());
                weightBt.setText("展開");
                params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                weightBt.setLayoutParams(params);
                check = new TextView(getApplicationContext());
                check.setTextSize(20);
                check.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                for (String str : tmp) {
                    check.append(str + "\n");
                }
                mapLayout.addView(weightTv);
                mapLayout.addView(weightBt);
                layout.addView(check);
                dialogStr = item.getDisplay();
                weightBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogFragment = DialogFragment.newInstance(parts, Utilities.CHECK_BOX_EDIT, null, countCheck, false);
                        dialogFragment.show(getFragmentManager(), dialogStr);
                    }
                });
                countCheck++;
                break;
            case "phone":
                phoneList.add(putJson(new JSONObject(), item));
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                phone = new EditText(getApplicationContext());
                createEdit(item, params, phone, InputType.TYPE_CLASS_PHONE, countPhone++);
                break;
            case "email":
                ++countEmail;
                emailList.add(putJson(new JSONObject(), item));
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                email = new EditText(getApplicationContext());
                createEdit(item, params, email, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, countEmail);
                break;
            case "number":
                numList.add(putJson(new JSONObject(), item));
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                number = new EditText(getApplicationContext());
                createEdit(item, params, number, InputType.TYPE_CLASS_NUMBER, countNum++);
                break;
            case "pass":
                passList.add(putJson(new JSONObject(), item));
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                pass = new EditText(getApplicationContext());
                createEdit(item, params, pass, InputType.TYPE_TEXT_VARIATION_PASSWORD, countPass++);
                break;
            case "text":
                textList.add(putJson(new JSONObject(), item));
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                text = new EditText(getApplicationContext());
                createEdit(item, params, text, InputType.TYPE_CLASS_TEXT, countText++);
                break;
            case "radio":
                radioList.add(putJson(new JSONObject(), item));
                condition = item.getCondition();
                parts = condition.split("\\|");
                mapLayout = new LinearLayout(getApplicationContext());
                mapLayout.setOrientation(LinearLayout.HORIZONTAL);
                layout.addView(mapLayout);
                weightTv = new TextView(getApplicationContext());
                weightTv.setText(item.getDisplay() + ":");
                weightTv.setGravity(Gravity.CENTER_VERTICAL);
                weightTv.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                weightTv.setTextSize(20);
                params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        3.0f
                );
                weightTv.setLayoutParams(params);
                weightBt = new Button(getApplicationContext());
                weightBt.setText("展開");
                params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                weightBt.setLayoutParams(params);
                radio = new TextView(getApplicationContext());
                radio.setTextSize(20);
                radio.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                radio.setText(item.getValue());
                mapLayout.addView(weightTv);
                mapLayout.addView(weightBt);
                layout.addView(radio);
                dialogStr = item.getDisplay();
                weightBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogFragment = DialogFragment.newInstance(parts, Utilities.RADIO_BUTTON_EDIT, null, countRadio, false);
                        dialogFragment.show(getFragmentManager(), dialogStr);
                    }
                });
                countRadio++;
                break;
            case "richtext":
                richList.add(putJson(new JSONObject(), item));
                params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        3.0f
                );
                rich = new EditText(getApplicationContext());
                createEdit(item, params, rich, InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, countRich++);
                break;
            case "schedule":
                schList.add(putJson(new JSONObject(), item));
                break;
            case "key":
                counts--;
                break;
        }
    }

    private void createEdit(final AutoRunItem item, LinearLayout.LayoutParams param, final EditText editText, final int inputType, final int num) {
        weightTv = new TextView(getApplicationContext());
        weightTv.setText(item.getDisplay() + ":");
        weightTv.setTextSize(20);
        weightTv.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
        layout.addView(weightTv);
        editText.setTextSize(20);
        editText.setText(item.getValue());
        editText.getBackground().setColorFilter(getResources().getColor(R.color.abc_primary_text_material_light), PorterDuff.Mode.SRC_ATOP);
        editText.setInputType(inputType);
        editText.setHintTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
        editText.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
        editText.setLayoutParams(param);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                switch (inputType) {
                    case InputType.TYPE_CLASS_PHONE:
                        try {
                            phoneList.get(num).put("value", editText.getText().toString());
                            phoneList.get(num).put("agent_parameter", "options");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
                        try {
                            emailList.get(num).put("value", editText.getText().toString());
                            emailList.get(num).put("agent_parameter", "options");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case InputType.TYPE_CLASS_NUMBER:
                        try {
                            numList.get(num).put("value", editText.getText().toString());
                            numList.get(num).put("agent_parameter", "options");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case InputType.TYPE_TEXT_VARIATION_PASSWORD:
                        try {
                            passList.get(num).put("value", editText.getText().toString());
                            passList.get(num).put("agent_parameter", "options");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case InputType.TYPE_CLASS_TEXT:
                        try {
                            textList.get(num).put("value", editText.getText().toString());
                            textList.get(num).put("agent_parameter", "options");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS:
                        try {
                            richList.get(num).put("value", editText.getText().toString());
                            richList.get(num).put("agent_parameter", "options");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
        if (inputType == InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS) {
            LinearLayout orientLayout = new LinearLayout(getApplicationContext());
            orientLayout.setOrientation(LinearLayout.HORIZONTAL);
            final Button richBt = new Button(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            richBt.setLayoutParams(params);
            richBt.setText("提示");
            orientLayout.addView(editText);
            orientLayout.addView(richBt);
            layout.addView(orientLayout);

            final TextView condition = new TextView(getApplicationContext());
            condition.setTextColor(getResources().getColor(android.R.color.black));
            condition.setText(item.getCondition());
            layout.addView(condition);
            condition.setVisibility(View.GONE);

            richBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (condition.getVisibility() == View.GONE) {
                        condition.setVisibility(View.VISIBLE);
                        richBt.setText("隱藏");
                    } else {
                        condition.setVisibility(View.GONE);
                        richBt.setText("提示");
                    }
                }
            });
        } else {
            editText.setHint(item.getCondition());
            layout.addView(editText);
        }
        editText.clearFocus();
    }

    private JSONObject putJson(JSONObject object, AutoRunItem item) {
        try {
            object.put("agentId", item.getAgentId());
            object.put("option", item.getOption());
            object.put("value", item.getValue());
            object.put("agent_parameter", "options");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {

        }
    }

    private void iterateList(ArrayList<JSONObject> main, ArrayList<JSONObject> sub) {
        for (JSONObject object : sub) {
            main.add(object);
        }
    }

    private void createList() {
        checkList = new ArrayList<>();
        radioList = new ArrayList<>();
        phoneList = new ArrayList<>();
        emailList = new ArrayList<>();
        textList = new ArrayList<>();
        numList = new ArrayList<>();
        passList = new ArrayList<>();
        richList = new ArrayList<>();
        schList = new ArrayList<>();
    }

    private void checkGps() {
        if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public static double roundDown5(double d) {
        return Math.floor(d * 1e5) / 1e5;
    }

}