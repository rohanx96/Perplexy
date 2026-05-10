package com.contextgenesis.perplexy.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contextgenesis.perplexy.databinding.FragmentStatisticspageBinding;
import com.contextgenesis.perplexy.utils.Constants;

import java.text.DecimalFormat;

/**
 * Created by bhutanidhruv16 on 06-Mar-16.
 */

public class StatisticsFragment extends Fragment {

    private FragmentStatisticspageBinding binding;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticspageBinding.inflate(inflater, container, false);
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "tagus.ttf");
        binding.statisticsTvHeading.setTypeface(typeFace);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding == null) return;

        pref = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        binding.statsTotalcoins.setText("Coins  :  " + pref.getLong(Constants.PREF_COINS, 0));
        binding.statsSpentcoins.setText("SPENT\n" + pref.getLong(Constants.PREF_COINS_SPENT, 0));
        binding.statsEarnedcoins.setText("EARNED\n" + pref.getLong(Constants.PREF_COINS_EARNED, 0));

        binding.statsAnswered.setText("Answered  :  " + (pref.getInt(Constants.CORRECT_COUNT, 0) + pref.getInt(Constants.INCORRECT_COUNT, 0)));
        binding.statsCorrectquesCount.setText("CORRECT\n" + pref.getInt(Constants.CORRECT_COUNT, 0));
        binding.statsIncorrectquesCount.setText("INCORRECT\n" + pref.getInt(Constants.INCORRECT_COUNT, 0));

        DecimalFormat df = new DecimalFormat("#.##");
        binding.statsAccuracy.setText("Accuracy  :  " + df.format(pref.getFloat(Constants.ACCURACY, 0f)*100f) + "%");
    }
}
