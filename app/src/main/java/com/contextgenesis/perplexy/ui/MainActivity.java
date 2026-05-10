package com.contextgenesis.perplexy.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import android.view.LayoutInflater;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AlertDialog;
import com.contextgenesis.perplexy.databinding.ActivityMainBinding;
import com.contextgenesis.perplexy.elements.GenericAnswerDetails;
import com.contextgenesis.perplexy.ui.fragments.FrontPageFragment;
import com.contextgenesis.perplexy.ui.fragments.SettingsFragment;
import com.contextgenesis.perplexy.utils.FallingDrawables;
import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.ui.fragments.StatisticsFragment;
import com.contextgenesis.perplexy.utils.Constants;

public class MainActivity extends FragmentActivity {

    private static final int NUM_PAGES = 3;

    private ViewPager mPager;
    private ActivityMainBinding binding;

    private PagerAdapter mPagerAdapter;

    FallingDrawables fallingDrawables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onFirstRun();
        mPager = binding.questionsActivityPager;
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        binding.indicator.setViewPager(mPager);
        mPager.setCurrentItem(1, false);
        fallingDrawables = new FallingDrawables(this, binding.mainActivityContainer);
        showRateDialog();

    }

    @Override
    protected void onResume() {
        super.onResume();
        /* Make the activity fullscreen */
        binding.mainActivityContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        // This is not done in OnCreate because the animation is stopped whenever the activity is left.
        // So we need to restart the animation when activity resumes
        if (!fallingDrawables.getIsRunning()) {
            fallingDrawables.createAnimation();
            fallingDrawables.setmDrawablesInRow();
        }
    }

    @Override
    public void onBackPressed() {
        switch (mPager.getCurrentItem()) {
            case 0:
                mPager.setCurrentItem(1, true);
                return;
            case 1:
                super.onBackPressed();
                return;
            case 2:
                mPager.setCurrentItem(1, true);
                return;
            default:
                return;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SettingsFragment();
                case 1:
                    return new FrontPageFragment();
                case 2:
                    return new StatisticsFragment();
                default:
                    return new FrontPageFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private void onFirstRun() {
        /*
        * Check if app is run for the first time.
        * Initialize SequenceAnswersDetails database with default values
         */
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        if (prefs.getBoolean(Constants.FIRST_RUN, true)) {
//            Log.d("MainActivity","ENTERED HERE");
            GenericAnswerDetails.initializeDatabase(MainActivity.this);
            prefs.edit().putBoolean(Constants.FIRST_RUN, false).apply();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(Constants.PREF_COINS, Constants.INITIAL_COINS).apply();
            editor.putString(Constants.VOLUME, "Y").apply();
            editor.putLong(Constants.PREF_COINS_EARNED,Constants.INITIAL_COINS).apply();
        }
    }

    private void showRateDialog() {
        final SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        int prefCount = prefs.getInt(Constants.PREF_SHOW_RATE_US, -1);
        Log.i("show rate", " " + prefCount);
        if (prefCount != -2) {
            if (prefCount > 3) {
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rate_us, null);
                final AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                        .setView(dialogView)
                        .create();
                dialogView.findViewById(R.id.rate_us_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(myAppLinkToMarket);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(MainActivity.this, "Unable to find market app", Toast.LENGTH_LONG).show();
                        }
                        prefs.edit().putInt(Constants.PREF_SHOW_RATE_US, -2).apply();
                        dialog.dismiss();
                    }
                });
                dialogView.findViewById(R.id.rate_us_remind_later).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prefs.edit().putInt(Constants.PREF_SHOW_RATE_US, 0).apply();
                        dialog.dismiss();
                    }
                });
                dialogView.findViewById(R.id.rate_us_never).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prefs.edit().putInt(Constants.PREF_SHOW_RATE_US, -2).apply();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            } else prefs.edit().putInt(Constants.PREF_SHOW_RATE_US, prefCount + 1).apply();
        }
    }

    public FallingDrawables getFallingDrawables() {
        return fallingDrawables;
    }

    public void goToSettings() {
        mPager.setCurrentItem(0, true);
    }

    public void goToStats() {
        mPager.setCurrentItem(2, true);
    }
}
