package org.itri.tomato.Activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.gson.JsonNull;
import com.nineoldandroids.view.ViewHelper;

import org.itri.tomato.AutoRunItem;
import org.itri.tomato.DataRetrieveListener;
import org.itri.tomato.Fragments.DialogFragment;
import org.itri.tomato.R;
import org.itri.tomato.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddAutoRunActivity extends AppCompatActivity implements ObservableScrollViewCallbacks, DataRetrieveListener
        , DialogFragment.CheckBoxListener, DialogFragment.RadioButtonListener, View.OnClickListener {
    //floating view scale
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    SharedPreferences sharedPreferences;


    //floating view
    private View mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private int mActionBarSize;
    private int mFlexibleSpaceImageHeight;

    private int ID;

    String id;

    boolean isMapCreated = false;
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
    String phoneStr, emailStr, textStr, numberStr, passStr, richStr, dialogStr;
    EditText phone, text, number, pass, email, rich;
    LocationManager manager;
    Toolbar toolbar;
    double latD = 0;
    double lngD = 0;
    JSONObject jsonMapLat, jsonMapLng, jsonCheck, jsonPhone, jsonEmail, jsonRadio, jsonText, jsonNum, jsonPass, jsonRich, jsonSch;
    String jsonPara;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addautorun);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        dataRetrieveListener = AddAutoRunActivity.this;
        progressDialog = ProgressDialog.show(this, "載入中", "請稍等......", false);
        new Thread(getAutoRunSettings).start();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ID = getIntent().getExtras().getInt("autoRunId");
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = 125;/**/
        geocoder = new Geocoder(this, Locale.TAIWAN);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        if (sharedPreferences.getInt(Utilities.SDK_VERSION, -100) >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusBar));
        }
        /**
         * init UI
         */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);
        setTitle(null);
        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
//                mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);

                // If you'd like to start from scrollY == 0, don't write like this:
                //mScrollView.scrollTo(0, 0);
                // The initial scrollY is 0, so it won't invoke onScrollChanged().
                // To do this, use the following:
                onScrollChanged(0, false, false);

                // You can also achieve it with the following codes.
                // This causes scroll change from 1 to 0.
//                mScrollView.scrollTo(0, 1);
//                mScrollView.scrollTo(0, 0);
            }
        });
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
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = (float) 0.95 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - (mTitleView.getHeight()) * scale) + 40 /*set here*/;
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }


    private void startActivity() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            toast.setText("Enable Location first");
            toast.show();
        } else {
            Intent intent = new Intent();
            if (latD != 0 && lngD != 0) {
                intent.putExtra("Lat", latD);
                intent.putExtra("Lng", lngD);
            }
            intent.setClass(AddAutoRunActivity.this, MapActivity.class);
            startActivityForResult(intent, 200);
        }
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
                region.setText(addressList.get(0).getAddressLine(0));
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

    Runnable getAutoRunSettings = new Runnable() {
        @Override
        public void run() {
            autoRunItemsWhen = new ArrayList<>();
            autoRunItemsDo = new ArrayList<>();
            String Action;
            switch (ID) {
                case 2:
                    Action = Utilities.ACTION + "GetAutoRunByIdSampleS";
                    break;
                default:
                    Action = Utilities.ACTION + "GetAutoRunByIdSampleF";
                    break;
            }
            String Params = Utilities.PARAMS + "{}";
            JSONObject jsonObject = Utilities.API_CONNECT(Action, Params, true);
            try {
                JSONObject jsonRes = new JSONObject(jsonObject.getString("response"));
                description = jsonRes.getString("autorunDesc");
                id = jsonRes.getString("autorunId");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTitleView.setText(description);
                        setTitle(description);
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
                            jsonWhen.getJSONObject(i).getString("agent_parameter")
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
                            jsonDo.getJSONObject(i).getString("agent_parameter")
                    ));
                }
                dataRetrieveListener.onFinish();
            } catch (JSONException e) {
                progressDialog.cancel();
                Log.w("Json", e.toString());
            }
        }
    };

    @Override
    public void onFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout = (LinearLayout) findViewById(R.id.viewGroup);
                TextView title = new TextView(getApplicationContext());
                title.setText("設定");
                title.setGravity(Gravity.CENTER);
                title.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                title.setTextSize(40);
                layout.addView(title);
                if (autoRunItemsWhen.size() != 0) {
                    TextView when = new TextView(getApplicationContext());
                    when.setText("When:");
                    when.setTextSize(30);
                    when.setTextColor(Color.BLACK);
                    layout.addView(when);
                    for (AutoRunItem item : autoRunItemsWhen) {
                        createView(item);
                    }
                }
                if (autoRunItemsDo.size() != 0) {
                    TextView Do = new TextView(getApplicationContext());
                    Do.setText("Do:");
                    Do.setTextSize(30);
                    Do.setTextColor(Color.BLACK);
                    layout.addView(Do);
                    for (AutoRunItem item : autoRunItemsDo) {
                        createView(item);
                    }
                }
                TextView overlay = new TextView(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        800
                );
                overlay.setLayoutParams(params);
                layout.addView(overlay);
                Button apply = new Button(getApplicationContext());
                apply.setTextSize(20);
                apply.setText("Add");
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                apply.setLayoutParams(params);
                apply.setOnClickListener(AddAutoRunActivity.this);
                layout.addView(apply);
                progressDialog.dismiss();
                onScrollChanged(0, false, false);
            }
        });
    }

    @Override
    public void onCheckFinished(ArrayList<String> Strings) {
        check.setText("");
        String temp = "";
        for (String tmp : Strings) {
            check.append(tmp + "\n");
            temp += tmp + "|";
        }
        try {
            jsonCheck.put("value", temp.substring(0, temp.length() - 1));
            jsonCheck.put("agent_parameter", "options");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        check.setTextSize(20);
        check.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
    }

    @Override
    public void onRadioFinished(String string) {
        radio.setText("");
        radio.append(string);
        radio.setTextSize(20);
        radio.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
        try {
            jsonRadio.put("value", string);
            jsonRadio.put("agent_parameter", "options");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void createView(AutoRunItem item) {
        String condition;
        LinearLayout.LayoutParams params;
        switch (item.getConditionType()) {
            case "map":
                if (!isMapCreated) {
                    jsonMapLat = new JSONObject();
                    jsonMapLng = new JSONObject();
                    putJson(jsonMapLat, item);
                    putJson(jsonMapLng, item);
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
                    lat = new TextView(getApplicationContext());
                    lng = new TextView(getApplicationContext());
                    layout.addView(region);
                    layout.addView(lat);
                    layout.addView(lng);
                    lat.setText(item.getDisplay() + ": ");
                    latStr = item.getDisplay();
                    region.setTextSize(20);
                    lat.setTextSize(20);
                    lng.setTextSize(20);
                    region.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                    lat.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                    lng.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                    isMapCreated = true;
                } else {
                    lngStr = item.getDisplay();
                    lng.setText(item.getDisplay() + ": ");
                }
                break;
            case "checkbox":
                jsonCheck = new JSONObject();
                putJson(jsonCheck, item);
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
                check = new TextView(getApplicationContext());
                mapLayout.addView(weightTv);
                mapLayout.addView(weightBt);
                layout.addView(check);
                dialogStr = item.getDisplay();
                weightBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogFragment = DialogFragment.newInstance(parts, Utilities.CHECK_BOX, null);
                        dialogFragment.show(getFragmentManager(), dialogStr);
                    }
                });
                break;
            case "phone":
                jsonPhone = new JSONObject();
                putJson(jsonPhone, item);
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                phone = new EditText(getApplicationContext());
                createEdit(item, params, phone, InputType.TYPE_CLASS_PHONE);
                break;
            case "email":
                jsonEmail = new JSONObject();
                putJson(jsonEmail, item);
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                email = new EditText(getApplicationContext());
                createEdit(item, params, email, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case "number":
                jsonNum = new JSONObject();
                putJson(jsonNum, item);
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                number = new EditText(getApplicationContext());
                createEdit(item, params, number, InputType.TYPE_CLASS_NUMBER);
                break;
            case "pass":
                jsonPass = new JSONObject();
                putJson(jsonPass, item);
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                pass = new EditText(getApplicationContext());
                createEdit(item, params, pass, InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case "text":
                jsonText = new JSONObject();
                putJson(jsonText, item);
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                text = new EditText(getApplicationContext());
                createEdit(item, params, text, InputType.TYPE_CLASS_TEXT);
                break;
            case "radio":
                jsonRadio = new JSONObject();
                putJson(jsonRadio, item);
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
                mapLayout.addView(weightTv);
                mapLayout.addView(weightBt);
                layout.addView(radio);
                dialogStr = item.getDisplay();
                weightBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogFragment = DialogFragment.newInstance(parts, Utilities.RADIO_BUTTON, null);
                        dialogFragment.show(getFragmentManager(), dialogStr);
                    }
                });
                break;
            case "richtext":
                jsonRich = new JSONObject();
                putJson(jsonRich, item);
                params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        3.0f
                );
                rich = new EditText(getApplicationContext());
                createEdit(item, params, rich, InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                break;
            case "schedule":
                jsonSch = new JSONObject();
                putJson(jsonSch, item);
                break;
        }
    }

    private void checkGps() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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

    private void createEdit(final AutoRunItem item, LinearLayout.LayoutParams param, final EditText editText, final int inputType) {
        weightTv = new TextView(getApplicationContext());
        weightTv.setText(item.getDisplay() + ":");
        weightTv.setTextSize(20);
        weightTv.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
        layout.addView(weightTv);
        editText.setTextSize(20);
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
                        phoneStr = editText.getText().toString();
                        try {
                            jsonPhone.put("value", phoneStr);
                            jsonPhone.put("agent_parameter", "options");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
                        emailStr = editText.getText().toString();
                        try {
                            jsonEmail.put("value", emailStr);
                            jsonEmail.put("agent_parameter", "options");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case InputType.TYPE_CLASS_NUMBER:
                        numberStr = editText.getText().toString();
                        try {
                            jsonNum.put("value", numberStr);
                            jsonNum.put("agent_parameter", "options");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case InputType.TYPE_TEXT_VARIATION_PASSWORD:
                        passStr = editText.getText().toString();
                        try {
                            jsonPass.put("value", passStr);
                            jsonPass.put("agent_parameter", "options");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case InputType.TYPE_CLASS_TEXT:
                        textStr = editText.getText().toString();
                        try {
                            jsonText.put("value", textStr);
                            jsonText.put("agent_parameter", "options");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS:
                        richStr = editText.getText().toString();
                        try {
                            jsonRich.put("value", richStr);
                            jsonRich.put("agent_parameter", "options");
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
            Button richBt = new Button(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            );
            richBt.setLayoutParams(params);
            richBt.setText("提示");
            layout.addView(orientLayout);
            orientLayout.addView(editText);
            orientLayout.addView(richBt);
            richBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new android.support.v7.app.AlertDialog.Builder(AddAutoRunActivity.this)
                            .setMessage(item.getCondition())
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            });
        } else {
            editText.setHint(item.getCondition());
            layout.addView(editText);
        }
        editText.clearFocus();
    }

    public static double roundDown5(double d) {
        return Math.floor(d * 1e5) / 1e5;
    }

    @Override
    public void onClick(View view) {
        JSONArray jsonArray = new JSONArray();
        ArrayList<JSONObject> tmp = new ArrayList<>();
        tmp.add(jsonMapLat);
        tmp.add(jsonMapLng);
        tmp.add(jsonCheck);
        tmp.add(jsonRadio);
        tmp.add(jsonText);
        tmp.add(jsonPhone);
        tmp.add(jsonPass);
        tmp.add(jsonRich);
        tmp.add(jsonEmail);
        tmp.add(jsonNum);
        tmp.add(jsonSch);
        for (JSONObject object : tmp) {
            if (object != null) {
                jsonArray.put(object);
            }
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", sharedPreferences.getString(Utilities.USER_ID, ""));
            jsonObject.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, ""));
            jsonObject.put("autorunId", id);
            jsonObject.put("autorunPara", jsonArray);
            jsonPara = jsonObject.toString();
            Log.w("Json", jsonPara);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void putJson(JSONObject object, AutoRunItem item) {
        try {
            object.put("agentId", item.getAgentId());
            object.put("option", item.getOption());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
