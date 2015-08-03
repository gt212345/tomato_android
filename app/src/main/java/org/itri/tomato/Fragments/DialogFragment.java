package org.itri.tomato.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import org.itri.tomato.Activities.AddAutoRunActivity;
import org.itri.tomato.R;

import java.util.ArrayList;


/**
 * Created by heiruwu on 7/31/15.
 */
public class DialogFragment extends android.app.DialogFragment implements CompoundButton.OnCheckedChangeListener {

    ArrayList<String> checks;

    public interface CheckBoxListener {
        void onFinished(ArrayList<String> Strings);
    }


    public static DialogFragment newInstance(String[] parts) {
        DialogFragment dialogFragment = new DialogFragment();
        Bundle args = new Bundle();
        args.putStringArray("parts", parts);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int counts = getArguments().getStringArray("parts").length;
        ArrayList<String> names = new ArrayList<>();
        checks = new ArrayList<>();
        for (String tmp : getArguments().getStringArray("parts")) {
            names.add(tmp);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog, null);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
        for (int i = 0; i < counts; i++) {
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setText(names.get(i));
            checkBox.setOnCheckedChangeListener(this);
            layout.addView(checkBox);
        }
        builder.setView(view)
                .setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                CheckBoxListener listener = (AddAutoRunActivity) getActivity();
                                listener.onFinished(checks);
                            }
                        }).setNegativeButton("Cancel", null);
        return builder.create();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            checks.add(compoundButton.getText().toString());
        } else {
            checks.remove(compoundButton.getText().toString());
        }
    }
}
