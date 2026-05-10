package com.contextgenesis.perplexy.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.utils.Constants;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bhutanidhruv16 on 06-Mar-16.
 */

public class StatisticsFragment extends Fragment {

    @Bind(R.id.stats_totalcoins)
    TextView totalcoins;

    @Bind(R.id.stats_earnedcoins)
    TextView earnedcoins;

    @Bind(R.id.stats_spentcoins)
    TextView spentcoins;

    @Bind(R.id.stats_answered)
    TextView answeredcount;

    @Bind(R.id.stats_correctques_count)
    TextView correctcount;

    @Bind(R.id.stats_incorrectques_count)
    TextView incorrectcount;

    @Bind(R.id.stats_accuracy)
    TextView accuracy;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_statisticspage, container, false);
        ButterKnife.bind(this, rootView);
        TextView heading = (TextView) rootView.findViewById(R.id.statistics_tv_heading);
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "tagus.ttf");
        heading.setTypeface(typeFace);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        pref = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        totalcoins.setText("Coins  :  " + pref.getLong(Constants.PREF_COINS, 0));
        spentcoins.setText("SPENT\n" + pref.getLong(Constants.PREF_COINS_SPENT, 0));
        earnedcoins.setText("EARNED\n" + pref.getLong(Constants.PREF_COINS_EARNED, 0));

        answeredcount.setText("Answered  :  " + (pref.getInt(Constants.CORRECT_COUNT, 0) + pref.getInt(Constants.INCORRECT_COUNT, 0)));
        correctcount.setText("CORRECT\n" + pref.getInt(Constants.CORRECT_COUNT, 0));
        incorrectcount.setText("INCORRECT\n" + pref.getInt(Constants.INCORRECT_COUNT, 0));

        DecimalFormat df = new DecimalFormat("#.##");
        accuracy.setText("Accuracy  :  " + df.format(pref.getFloat(Constants.ACCURACY, 0f)*100f) + "%");
    }
}