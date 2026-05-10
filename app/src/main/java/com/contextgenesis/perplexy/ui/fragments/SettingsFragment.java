package com.contextgenesis.perplexy.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.contextgenesis.perplexy.ui.ContributeActivity;
import com.contextgenesis.perplexy.ui.HelpActivity;
import com.kyleduo.switchbutton.SwitchButton;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.elements.GenericAnswerDetails;
import com.contextgenesis.perplexy.ui.AboutActivity;
import com.contextgenesis.perplexy.utils.Constants;
import com.contextgenesis.perplexy.utils.SoundManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rose on 10/3/16.
 */

public class SettingsFragment extends Fragment {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Bind(R.id.switchButton)
    SwitchButton volume;

    @Bind(R.id.settings_reset)
    TextView reset;

    @Bind(R.id.settings_info)
    TextView info;

    @Bind(R.id.settings_tutorial)
    TextView tutorial;

    @Bind(R.id.settings_contribute)
    TextView contribute;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, rootView);
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "tagus.ttf");
        TextView heading = (TextView) rootView.findViewById(R.id.settings_tv_heading);
        heading.setTypeface(typeFace);
        setupSoundSwitch();
        return rootView;
    }

    public void setupSoundSwitch() {
        SoundManager.playSwipeSound(getActivity());
        pref = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        volume.setChecked(pref.getString(Constants.VOLUME, "Y").equals("Y"));
        final SharedPreferences.Editor editor = pref.edit();
        volume.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString(Constants.VOLUME, "Y").apply();
                    SoundManager.setShouldPlaySound("Y");
                } else {
                    editor.putString(Constants.VOLUME, "N").apply();
                    SoundManager.setShouldPlaySound("N");
                }
            }
        });
    }

    @OnClick(R.id.settings_contribute)
    public void onClick_contribute() {
        SoundManager.playButtonClickSound(getActivity());
        startActivity(new Intent(getActivity(), ContributeActivity.class));
    }

    @OnClick(R.id.settings_reset)
    public void onClick_reset() {
        SoundManager.playButtonClickSound(getActivity());
        pref = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        final DialogPlus dialog = DialogPlus.newDialog(getContext())
                .setGravity(Gravity.BOTTOM)
                .setOverlayBackgroundResource(Color.TRANSPARENT)
                .setContentHolder(new ViewHolder(R.layout.reset_confirmation))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setPadding(16, 16, 16, 16)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .create();
        dialog.show();

        View dialogView = dialog.getHolderView();

        TextView yes = (TextView) dialogView.findViewById(R.id.yes_reset);
        TextView no = (TextView) dialogView.findViewById(R.id.no_reset);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenericAnswerDetails.initializeDatabase(getActivity());
                SharedPreferences.Editor editor = pref.edit();

                editor.putLong(Constants.PREF_COINS, Constants.INITIAL_COINS).apply();
                editor.putLong(Constants.PREF_COINS_SPENT, 0).apply();
                editor.putLong(Constants.PREF_COINS_EARNED, Constants.INITIAL_COINS).apply();

                editor.putInt(Constants.CORRECT_COUNT, 0).apply();
                editor.putInt(Constants.INCORRECT_COUNT, 0).apply();
                editor.putFloat(Constants.ACCURACY, 0f).apply();
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.settings_rate_us)
    public void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "Unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.settings_info)
    public void onClick_info() {
        SoundManager.playButtonClickSound(getActivity());
        startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    @OnClick(R.id.settings_tutorial)
    public void onClick_tutorial() {
        SoundManager.playButtonClickSound(getActivity());
        startActivity(new Intent(getActivity(), HelpActivity.class));
    }
}
