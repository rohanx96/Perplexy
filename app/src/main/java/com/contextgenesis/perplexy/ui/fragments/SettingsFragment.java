package com.contextgenesis.perplexy.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.contextgenesis.perplexy.ui.ContributeActivity;
import com.contextgenesis.perplexy.ui.HelpActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AlertDialog;
import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.databinding.FragmentSettingsBinding;
import com.contextgenesis.perplexy.elements.GenericAnswerDetails;
import com.contextgenesis.perplexy.ui.AboutActivity;
import com.contextgenesis.perplexy.utils.Constants;
import com.contextgenesis.perplexy.utils.SoundManager;

/**
 * Created by rose on 10/3/16.
 */

public class SettingsFragment extends Fragment {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        binding.settingsContribute.setOnClickListener(v -> onClick_contribute());
        binding.settingsReset.setOnClickListener(v -> onClick_reset());
        binding.settingsRateUs.setOnClickListener(v -> rateUs());
        binding.settingsInfo.setOnClickListener(v -> onClick_info());
        binding.settingsTutorial.setOnClickListener(v -> onClick_tutorial());

        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "tagus.ttf");
        binding.settingsTvHeading.setTypeface(typeFace);
        setupSoundSwitch();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setupSoundSwitch() {
        SoundManager.playSwipeSound(getActivity());
        pref = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        binding.switchButton.setChecked(pref.getString(Constants.VOLUME, "Y").equals("Y"));
        final SharedPreferences.Editor editor = pref.edit();
        binding.switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

    public void onClick_contribute() {
        SoundManager.playButtonClickSound(getActivity());
        startActivity(new Intent(getActivity(), ContributeActivity.class));
    }

    public void onClick_reset() {
        SoundManager.playButtonClickSound(getActivity());
        pref = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.reset_confirmation, null);
        final AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
                .setView(dialogView)
                .create();
        dialog.show();

        android.widget.TextView yes = (android.widget.TextView) dialogView.findViewById(R.id.yes_reset);
        android.widget.TextView no = (android.widget.TextView) dialogView.findViewById(R.id.no_reset);

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

    public void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "Unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    public void onClick_info() {
        SoundManager.playButtonClickSound(getActivity());
        startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    public void onClick_tutorial() {
        SoundManager.playButtonClickSound(getActivity());
        startActivity(new Intent(getActivity(), HelpActivity.class));
    }
}
