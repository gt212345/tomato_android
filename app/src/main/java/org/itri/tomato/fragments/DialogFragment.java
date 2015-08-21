package org.itri.tomato.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.itri.tomato.DataRetrieveListener;
import org.itri.tomato.activities.AddAutoRunActivity;
import org.itri.tomato.activities.DropboxAuthActivity;
import org.itri.tomato.activities.EditAutoRunActivity;
import org.itri.tomato.R;
import org.itri.tomato.Utilities;

import java.net.URL;
import java.util.ArrayList;


/**
 * Created by heiruwu on 7/31/15.
 */
public class DialogFragment extends android.app.DialogFragment implements CompoundButton.OnCheckedChangeListener {

    ArrayList<String> checks;
    int num;
    String string;
    int isMap;
    RadioGroup radioGroup;
    ProgressBar progressBar;
    static Fragment fragment;
    private static final int CHECK_BOX = 1;
    private static final int RADIO_BUTTON = 2;
    private static final int SEARCH_DIALOG = 3;
    private static final int CHECK_BOX_EDIT = 4;
    private static final int RADIO_BUTTON_EDIT = 5;
    private static final int DROP_BOX = 6;
    private final static String AUTH_URL = "https://www.dropbox.com/1/oauth/authorize?";
    String oauth = "oauth_token=";
    String oauthCallback = "&oauth_callback=";


    public interface CheckBoxListener {
        void onCheckFinished(ArrayList<String> Strings, int num);
    }

    public interface RadioButtonListener {
        void onRadioFinished(String String, int num, int isMap);
    }

    public static DialogFragment newInstance(String[] parts, int type, Fragment f, int num, int isMap) {
        DialogFragment dialogFragment = new DialogFragment();
        Bundle args = new Bundle();
        args.putStringArray("parts", parts);
        args.putInt("type", type);
        args.putInt("num", num);
        args.putInt("isMap", isMap);
        fragment = f;
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int counts = getArguments().getStringArray("parts").length;
        int type = getArguments().getInt("type");
        String[] parts = getArguments().getStringArray("parts");
        isMap = getArguments().getInt("isMap");
        num = getArguments().getInt("num");
        ArrayList<String> names = new ArrayList<>();
        checks = new ArrayList<>();
        for (String tmp : getArguments().getStringArray("parts")) {
            names.add(tmp);
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog, null);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
        switch (type) {
            case CHECK_BOX:
                for (int i = 0; i < counts; i++) {
                    CheckBox checkBox = new CheckBox(getActivity());
                    checkBox.setText(names.get(i));
                    checkBox.setOnCheckedChangeListener(this);
                    layout.addView(checkBox);
                }

                builder.setView(view).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        CheckBoxListener listener = (AddAutoRunActivity) getActivity();
                        listener.onCheckFinished(checks, num);
                    }
                }).setNegativeButton("Cancel", null);
                break;
            case RADIO_BUTTON:
                radioGroup = new RadioGroup(getActivity());
                radioGroup.setOrientation(LinearLayout.VERTICAL);
                layout.addView(radioGroup);
                for (int i = 0; i < counts; i++) {
                    RadioButton radioButton = new RadioButton(getActivity());
                    radioButton.setText(names.get(i));
                    radioButton.setOnCheckedChangeListener(this);
                    radioGroup.addView(radioButton);
                }
                builder.setView(view).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RadioButtonListener listener = (AddAutoRunActivity) getActivity();
                        listener.onRadioFinished(string, num, isMap);
                    }
                }).setNegativeButton("Cancel", null);
                break;
            case SEARCH_DIALOG:
                radioGroup = new RadioGroup(getActivity());
                radioGroup.setOrientation(LinearLayout.VERTICAL);
                layout.addView(radioGroup);
                for (int i = 0; i < counts; i++) {
                    RadioButton radioButton = new RadioButton(getActivity());
                    radioButton.setText(names.get(i));
                    radioButton.setOnCheckedChangeListener(this);
                    radioGroup.addView(radioButton);
                }
                builder.setView(view).setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RadioButtonListener listener = (AutoRunListFragment) fragment;
                        listener.onRadioFinished(string, num, isMap);
                    }
                }).setNegativeButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RadioButtonListener listener = (AutoRunListFragment) fragment;
                        listener.onRadioFinished("all", num, isMap);
                    }
                });
                break;
            case CHECK_BOX_EDIT:
                for (int i = 0; i < counts; i++) {
                    CheckBox checkBox = new CheckBox(getActivity());
                    checkBox.setText(names.get(i));
                    checkBox.setOnCheckedChangeListener(this);
                    layout.addView(checkBox);
                }

                builder.setView(view).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        CheckBoxListener listener = (EditAutoRunActivity) getActivity();
                        listener.onCheckFinished(checks, num);
                    }
                }).setNegativeButton("Cancel", null);
                break;
            case RADIO_BUTTON_EDIT:
                radioGroup = new RadioGroup(getActivity());
                radioGroup.setOrientation(LinearLayout.VERTICAL);
                layout.addView(radioGroup);
                for (int i = 0; i < counts; i++) {
                    RadioButton radioButton = new RadioButton(getActivity());
                    radioButton.setText(names.get(i));
                    radioButton.setOnCheckedChangeListener(this);
                    radioGroup.addView(radioButton);
                }
                builder.setView(view).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RadioButtonListener listener = (EditAutoRunActivity) getActivity();
                        listener.onRadioFinished(string, num, isMap);
                    }
                }).setNegativeButton("Cancel", null);
                break;
            case DROP_BOX:
                String url = AUTH_URL + "oauth_token=" + parts[0] + "&oauth_callback=";
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(100);
                WebView webView = new MyWebView(getActivity());
                webView.requestFocus(View.FOCUS_DOWN);
                webView.setBackgroundColor(Color.TRANSPARENT);
                webView.getSettings().setDomStorageEnabled(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(mWebViewClient);
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        progressBar.setProgress(newProgress);
                        if (newProgress == 100) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
                webView.loadUrl(url);
                layout.addView(webView);
                builder.setView(view).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DataRetrieveListener listener = (DropboxAuthActivity) getActivity();
                        listener.onFinish();
                    }
                });
                break;
        }
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int type = getArguments().getInt("type");
        if (type == Utilities.SEARCH_DIALOG) {
            Window window = getDialog().getWindow();
            window.setGravity(Gravity.TOP | Gravity.RIGHT);
            WindowManager.LayoutParams params = window.getAttributes();
            params.x = -100;
            params.y = 100;
            window.setAttributes(params);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            checks.add(compoundButton.getText().toString());
            string = compoundButton.getText().toString();
        } else {
            checks.remove(compoundButton.getText().toString());
        }
    }

    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.w("onReceive", errorCode + failingUrl + description);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // this method will proceed your url however if certification issues are there or not
            handler.proceed();
        }
    };

    private static class MyWebView extends WebView
    {
        public MyWebView(Context context)
        {
            super(context);
        }

        // Note this!
        @Override
        public boolean onCheckIsTextEditor()
        {
            return true;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev)
        {
            switch (ev.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                    if (!hasFocus())
                        requestFocus();
                    break;
            }

            return super.onTouchEvent(ev);
        }
    }

}
