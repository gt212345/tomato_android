package org.itri.tomato.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
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
import android.view.Display;
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

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import org.itri.tomato.AutoRunItem;
import org.itri.tomato.DataRetrieveListener;
import org.itri.tomato.Fragments.DialogFragment;
import org.itri.tomato.R;
import org.itri.tomato.Utilities;
import org.itri.tomato.WhenDoIconView;
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
    private static final String TAG = "AddAutoRunActivity";
    SharedPreferences sharedPreferences;


    //floating view
    private WhenDoIconView mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private int mActionBarSize;
    private int mFlexibleSpaceImageHeight;


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


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addautorun);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        dataRetrieveListener = AddAutoRunActivity.this;
        progressDialog = ProgressDialog.show(this, "載入中", "請稍等......", false);
        mImageView = (WhenDoIconView) findViewById(R.id.image);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        new Thread(getAutoRunSettings).start();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = 125;/**/
        mImageView.setIcon(R.drawable.fb, R.drawable.dropbox, size.x, mActionBarSize);
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
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
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
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            autoRunItemsWhen = new ArrayList<>();
            autoRunItemsDo = new ArrayList<>();
            String Action = Utilities.ACTION + "GetAutoRunById";
            JSONObject para = new JSONObject();
            try {
                para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                para.put("autorunId", getIntent().getExtras().getInt("autoRunId"));
            } catch (JSONException e) {
                e.printStackTrace();
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
                        mTitleView.setText(description);
                        setTitle("Add AutoRun");
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
                counts = jsonWhen.length() + jsonDo.length();
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
                createList();
                layout = (LinearLayout) findViewById(R.id.viewGroup);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideSoftKeyboard(AddAutoRunActivity.this);
                    }
                });
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
    public void onCheckFinished(ArrayList<String> Strings, int num) {
        check.setText("");
        String temp = "";
        for (String tmp : Strings) {
            check.append(tmp + "\n");
            temp += tmp + "|";
        }
        try {
            checkList.get(num).put("value", temp.substring(0, temp.length() - 1));
            checkList.get(num).put("agent_parameter", "options");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        check.setTextSize(20);
        check.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
    }

    @Override
    public void onRadioFinished(String string, int num) {
        radio.setText("");
        radio.append(string);
        radio.setTextSize(20);
        radio.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
        try {
            radioList.get(num).put("value", string);
            radioList.get(num).put("agent_parameter", "options");
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
                checkList.add(putJson(new JSONObject(), item));
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
                        dialogFragment = DialogFragment.newInstance(parts, Utilities.CHECK_BOX, null, countCheck++);
                        dialogFragment.show(getFragmentManager(), dialogStr);
                    }
                });
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
                mapLayout.addView(weightTv);
                mapLayout.addView(weightBt);
                layout.addView(radio);
                dialogStr = item.getDisplay();
                weightBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogFragment = DialogFragment.newInstance(parts, Utilities.RADIO_BUTTON, null, countRadio++);
                        dialogFragment.show(getFragmentManager(), dialogStr);
                    }
                });
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

    private void createEdit(final AutoRunItem item, LinearLayout.LayoutParams param, final EditText editText, final int inputType, final int num) {
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
            }
        }
        if (jsonArray.length() != counts) {
            toast.setText("Settings not complete!!");
            toast.show();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", sharedPreferences.getString(Utilities.USER_ID, ""));
            jsonObject.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, ""));
            jsonObject.put("autorunId", id);
            jsonObject.put("autorunPara", jsonArray);
            jsonPara = jsonObject.toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String Action = Utilities.ACTION + "AddUserAutoRun";
                    String Params = Utilities.PARAMS + jsonPara;
                    Utilities.API_CONNECT(Action, Params, true);
                    if (Utilities.getResponseCode().equals("true")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toast.setText("AutoRun Added");
                                toast.show();
                            }
                        });
                    }
                }
            }).start();
            Intent intent = new Intent();
            intent.putExtra("from", TAG);
            intent.setClass(AddAutoRunActivity.this, AutoRunActivity.class);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject putJson(JSONObject object, AutoRunItem item) {
        try {
            object.put("agentId", item.getAgentId());
            object.put("option", item.getOption());
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

}
