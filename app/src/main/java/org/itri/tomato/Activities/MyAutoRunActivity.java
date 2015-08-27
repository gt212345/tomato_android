package org.itri.tomato.activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import org.itri.tomato.DataRetrieveListener;
import org.itri.tomato.R;
import org.itri.tomato.Utilities;
import org.itri.tomato.WhenDoIconView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MyAutoRunActivity extends AppCompatActivity implements ObservableScrollViewCallbacks, DataRetrieveListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "MyAutoRunActivity";
    Toolbar toolbar;
    private WhenDoIconView mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private int mActionBarSize;
    private int mFlexibleSpaceImageHeight;
    ArrayList<Bitmap> icons;
    DataRetrieveListener dataRetrieveListener;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    Toast toast;
    JSONObject autoRunItem;
    LinearLayout layout;
    String jsonPara, description;
    int whenIconId, doIconId, id;

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_auto_run);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        id = getIntent().getExtras().getInt("autoRunId");
        dataRetrieveListener = MyAutoRunActivity.this;
        progressDialog = ProgressDialog.show(this, "載入中", "請稍等......", false);
        createIconList();
        mImageView = (WhenDoIconView) findViewById(R.id.image);
        new Thread(getAutoRun).start();
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = 125;/**/
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
    public void onFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams params;
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                layout = (LinearLayout) findViewById(R.id.viewGroup);
                LinearLayout horLayout = new LinearLayout(getApplicationContext());
                horLayout.setOrientation(LinearLayout.HORIZONTAL);
                layout.addView(horLayout);
                TextView desTv = new TextView(getApplicationContext());
                desTv.setText("Enable/Disable: ");
                desTv.setPadding(10, 10, 10, 10);
                desTv.setGravity(Gravity.CENTER_VERTICAL);
                desTv.setTextColor(Color.WHITE);
                desTv.setTextSize(25);
                params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        3.0f
                );
                desTv.setLayoutParams(params);
                Switch aSwitch = new Switch(getApplicationContext());
                try {
                    if (autoRunItem.getString("enable").equals("on")) {
                        aSwitch.setChecked(true);
                    } else {
                        aSwitch.setChecked(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.5f
                );
                aSwitch.setLayoutParams(params);
                aSwitch.setOnCheckedChangeListener(MyAutoRunActivity.this);
                horLayout.setBackgroundColor(Color.parseColor("#00BCD4"));
                horLayout.addView(desTv);
                horLayout.addView(aSwitch);
                TextView overlay = new TextView(getApplicationContext());
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        400
                );
                overlay.setLayoutParams(params);
                layout.addView(overlay);
                Button runNow = new Button(getApplicationContext());
                runNow.setTextSize(20);
                runNow.setText("Run Now");
                runNow.setId(R.id.run_btn);
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                runNow.setLayoutParams(params);
                runNow.setOnClickListener(MyAutoRunActivity.this);
                layout.addView(runNow);
                Button edit = new Button(getApplicationContext());
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                edit.setLayoutParams(params);
                edit.setTextSize(20);
                edit.setText("Edit");
                edit.setId(0);
                edit.setOnClickListener(MyAutoRunActivity.this);
                layout.addView(edit);
                Button delete = new Button(getApplicationContext());
                delete.setTextColor(Color.RED);
                delete.setTextSize(20);
                delete.setText("Delete");
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                delete.setLayoutParams(params);
                delete.setId(-1);
                delete.setOnClickListener(MyAutoRunActivity.this);
                layout.addView(delete);
                mImageView.setIcon(icons.get(whenIconId - 1), icons.get(doIconId - 1), size.x, mActionBarSize);
                mImageView.invalidate();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
                onScrollChanged(0, false, false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case 0:
                intent = new Intent();
                intent.setClass(MyAutoRunActivity.this, EditAutoRunActivity.class);
                intent.putExtra("autoRunId", id);
                startActivity(intent);
                break;
            case -1:
                new AlertDialog.Builder(MyAutoRunActivity.this)
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog = ProgressDialog.show(MyAutoRunActivity.this, "載入中", "請稍等......", false);
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
                                            Utilities.API_CONNECT(Action, Params, MyAutoRunActivity.this, true);
                                            if (Utilities.getResponseCode().equals("true")) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        toast.setText("AutoRun Deleted");
                                                        toast.show();
                                                    }
                                                });
                                                progressDialog.dismiss();
                                                Intent intent = new Intent();
                                                intent.putExtra("from", TAG);
                                                intent.setClass(MyAutoRunActivity.this, AutoRunActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }).start();
                                } catch (JSONException e) {
                                    progressDialog.cancel();
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                break;
            case R.id.run_btn:
                progressDialog = ProgressDialog.show(this, "載入中", "請稍等......", false);
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
                            Utilities.API_CONNECT(Action, Params, MyAutoRunActivity.this, true);
                            if (Utilities.getResponseCode().equals("true")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast.setText("AutoRun Run Complete");
                                        toast.show();
                                    }
                                });
                                progressDialog.dismiss();
                            }
                        }
                    }).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, final boolean able) {
        progressDialog = ProgressDialog.show(this, "載入中", "請稍等......", false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String Action = Utilities.ACTION + "SwitchUserAutoRunById";
                JSONObject para = new JSONObject();
                try {
                    para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                    para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                    para.put("userautorunId", id);
                    para.put("enable", able ? "on" : "off");
                } catch (JSONException e) {
                    Log.w(TAG, e.toString());
                }
                String Para = Utilities.PARAMS + para.toString();
                Utilities.API_CONNECT(Action, Para, MyAutoRunActivity.this, true);
                if (Utilities.getResponseCode().equals("true")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();
                        }
                    });
                }
            }
        }).start();
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

    Runnable getAutoRun = new Runnable() {
        @Override
        public void run() {
            String Action = Utilities.ACTION + "GetUserAutoRunById";
            JSONObject para = new JSONObject();
            try {
                para.put("uid", sharedPreferences.getString(Utilities.USER_ID, null));
                para.put("token", sharedPreferences.getString(Utilities.USER_TOKEN, null));
                para.put("userautorunId", getIntent().getExtras().getInt("autoRunId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String Params = Utilities.PARAMS + para.toString();
            JSONObject jsonTmp = Utilities.API_CONNECT(Action, Params, MyAutoRunActivity.this, true);
            if (Utilities.getResponseCode().equals("true")) {
                try {
                    autoRunItem = new JSONObject(jsonTmp.getString("autorun"));
                    whenIconId = Integer.parseInt(autoRunItem.getString("whenIconId"));
                    doIconId = Integer.parseInt(autoRunItem.getString("doIconId"));
                    description = autoRunItem.getString("autorunDesc");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTitleView.setText(description);
                            setTitle("My AutoRun");
                        }
                    });
                    dataRetrieveListener.onFinish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    void createIconList() {
        icons = new ArrayList<>();
        icons.add(BitmapFactory.decodeResource(getResources(),
                R.drawable.raining));
        icons.add(BitmapFactory.decodeResource(getResources(),
                R.drawable.noti));
        icons.add(BitmapFactory.decodeResource(getResources(),
                R.drawable.home));
        icons.add(BitmapFactory.decodeResource(getResources(),
                R.drawable.email));
        icons.add(BitmapFactory.decodeResource(getResources(),
                R.drawable.person));
        icons.add(BitmapFactory.decodeResource(getResources(),
                R.drawable.email));
        icons.add(BitmapFactory.decodeResource(getResources(),
                R.drawable.email));
        icons.add(BitmapFactory.decodeResource(getResources(),
                R.drawable.ring));
        icons.add(BitmapFactory.decodeResource(getResources(),
                R.drawable.fb));
        icons.add(BitmapFactory.decodeResource(getResources(),
                R.drawable.dropbox));
        icons.add(BitmapFactory.decodeResource(getResources(),
                R.drawable.noti));
        icons.add(BitmapFactory.decodeResource(getResources(),
                R.drawable.bulb));
    }
}
