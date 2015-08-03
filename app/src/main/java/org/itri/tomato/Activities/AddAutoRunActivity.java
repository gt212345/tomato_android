package org.itri.tomato.Activities;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import org.itri.tomato.Fragments.DialogFragment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.itri.tomato.AutoRunItem;
import org.itri.tomato.DataRetrieveListener;
import org.itri.tomato.R;
import org.itri.tomato.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddAutoRunActivity extends AppCompatActivity implements ObservableScrollViewCallbacks, DataRetrieveListener
        , DialogFragment.CheckBoxListener {
    //home button ID
    private static final int home = 16908332;
    //floating view scale
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    SharedPreferences sharedPreferences;


    //floating view
    private View mFab;
    private View mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private boolean mFabIsShown;

    private int ID;

    boolean isMapCreated = false;
    Toast toast;
    LinearLayout layout;
    String description;
    ArrayList<AutoRunItem> autoRunItems;
    DataRetrieveListener dataRetrieveListener;
    LinearLayout mapLayout;
    TextView mapTV, lat, lng, weather, region;
    Button mapBT;
    String[] parts;
    String latStr;
    String lngStr;
    ProgressDialog progressDialog;
    Geocoder geocoder;
    List<Address> addressList;

    double latD = 0;
    double lngD = 0;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addautorun);
        dataRetrieveListener = AddAutoRunActivity.this;
        new Thread(getAutoRunSettings).start();
        progressDialog = ProgressDialog.show(this, "載入中", "請稍等......", false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ID = getIntent().getExtras().getInt("autoRunId");
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = 125;/**/
        setTitle("Published AutoRun");
        geocoder = new Geocoder(this, Locale.TAIWAN);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        if(sharedPreferences.getInt(Utilities.SDK_VERSION, -100) >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusBar));
        }
        /**
         * init UI
         */
        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(getTitle());
        setTitle(null);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddAutoRunActivity.this, "AutoRun added", Toast.LENGTH_SHORT).show();
            }
        });
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        ViewHelper.setScaleX(mFab, 0);
        ViewHelper.setScaleY(mFab, 0);

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
    protected void onResume() {
        super.onResume();
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
        float scale = (float)0.95 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - (mTitleView.getHeight()) * scale) + 40 /*set here*/;
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
        } else {
            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
            ViewHelper.setTranslationY(mFab, fabTranslationY);
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }

    private void startActivity() {
        Intent intent = new Intent();
        if(latD != 0 && lngD != 0) {
            intent.putExtra("Lat", latD);
            intent.putExtra("Lng", lngD);
        }
        intent.setClass(AddAutoRunActivity.this, MapActivity.class);
        startActivityForResult(intent, 200);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Runnable getAutoRunSettings = new Runnable() {
        @Override
        public void run() {
            autoRunItems = new ArrayList<>();
            String Action = Utilities.ACTION + "GetAutoRunByIdSampleF";
            String Params = Utilities.PARAMS + "{}";
            JSONObject jsonObject = Utilities.API_CONNECT(Action, Params, true);
            try {
                JSONObject jsonRes = new JSONObject(jsonObject.getString("response"));
                description = jsonRes.getString("autorunDesc");
                JSONObject jsonPara = new JSONObject(jsonRes.getString("autorunPara"));
                JSONArray jsonWhen = new JSONArray(jsonPara.getString("when"));
                for (int i = 0; i < jsonWhen.length(); i ++) {
                    autoRunItems.add(new AutoRunItem(
                            jsonWhen.getJSONObject(i).getString("agentId"),
                            jsonWhen.getJSONObject(i).getString("display"),
                            jsonWhen.getJSONObject(i).getString("option"),
                            jsonWhen.getJSONObject(i).getString("conditionType"),
                            jsonWhen.getJSONObject(i).getString("condition"),
                            jsonWhen.getJSONObject(i).getString("agent_parameter")
                    ));
                }
                dataRetrieveListener.onFinish();
            } catch (JSONException e) {
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
                TextView des = new TextView(getApplicationContext());
                des.setText(description);
                des.setGravity(Gravity.CENTER);
                des.setTextSize(20);
                des.setTextColor(Color.BLACK);
                TextView when = new TextView(getApplicationContext());
                when.setText("When:");
                when.setTextSize(30);
                when.setTextColor(Color.BLACK);
                if (autoRunItems.size() != 0) {
                    layout.addView(des);
                    layout.addView(title);
                    layout.addView(when);
                    for (AutoRunItem item : autoRunItems) {
                        switch (item.getConditionType()) {
                            case "map":
                                if (!isMapCreated) {
                                    mapLayout = new LinearLayout(getApplicationContext());
                                    mapLayout.setOrientation(LinearLayout.HORIZONTAL);
                                    layout.addView(mapLayout);
                                    mapTV = new TextView(getApplicationContext());
                                    mapTV.setText("請選擇位置:");
                                    mapTV.setGravity(Gravity.CENTER_VERTICAL);
                                    mapTV.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                                    mapTV.setTextSize(20);
                                    LinearLayout.LayoutParams params;
                                    params = new LinearLayout.LayoutParams(
                                            0,
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            3.0f
                                    );
                                    mapTV.setLayoutParams(params);
                                    mapBT = new Button(getApplicationContext());
                                    mapBT.setText("地圖");
                                    params = new LinearLayout.LayoutParams(
                                            0,
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            1.0f
                                    );
                                    mapBT.setLayoutParams(params);
                                    mapLayout.addView(mapTV);
                                    mapLayout.addView(mapBT);
                                    mapBT.setOnClickListener(new View.OnClickListener() {
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
                                String condition = item.getCondition();
                                parts = condition.split("\\|");
                                mapLayout = new LinearLayout(getApplicationContext());
                                mapLayout.setOrientation(LinearLayout.HORIZONTAL);
                                layout.addView(mapLayout);
                                mapTV = new TextView(getApplicationContext());
                                mapTV.setText("請選擇天氣型態:");
                                mapTV.setGravity(Gravity.CENTER_VERTICAL);
                                mapTV.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
                                mapTV.setTextSize(20);
                                LinearLayout.LayoutParams params;
                                params = new LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        3.0f
                                );
                                mapTV.setLayoutParams(params);
                                mapBT = new Button(getApplicationContext());
                                mapBT.setText("展開");
                                params = new LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        1.0f
                                );
                                mapBT.setLayoutParams(params);
                                weather = new TextView(getApplicationContext());
                                mapLayout.addView(mapTV);
                                mapLayout.addView(mapBT);
                                layout.addView(weather);
                                mapBT.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DialogFragment dialogFragment = DialogFragment.newInstance(parts);
                                        dialogFragment.show(getFragmentManager(), "選擇天氣型態");
                                    }
                                });
                                break;
                        }
                    }
                    TextView overlay = new TextView(getApplicationContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1000
                    );
                    overlay.setLayoutParams(params);
                    layout.addView(overlay);
                    progressDialog.dismiss();
                    onScrollChanged(0, false, false);
                }

            }
        });}

    @Override
    public void onFinished(ArrayList<String> Strings) {
        weather.setText("");
        for (String tmp : Strings) {
            weather.append(tmp + "\n");
        }
        weather.setTextSize(20);
        weather.setTextColor(getResources().getColor(R.color.abc_primary_text_material_light));
    }

    public static double roundDown5(double d) {
        return Math.floor(d * 1e5) / 1e5;
    }

}
