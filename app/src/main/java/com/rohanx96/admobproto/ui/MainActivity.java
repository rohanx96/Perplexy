package com.rohanx96.admobproto.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import com.rohanx96.admobproto.elements.WordAnswerDetails;
import com.rohanx96.admobproto.utils.FallingDrawables;
import com.rohanx96.admobproto.ui.fragments.FrontPageFragment;
import com.rohanx96.admobproto.R;
import com.rohanx96.admobproto.ui.fragments.StatisticsFragment;
import com.rohanx96.admobproto.elements.MCQAnswersDetails;
import com.rohanx96.admobproto.utils.Constants;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {

    private static final int NUM_PAGES = 2;

    private ViewPager mPager;
    private FrameLayout mContainer;

    private PagerAdapter mPagerAdapter;

    CirclePageIndicator circlePageIndicator;
    FallingDrawables fallingDrawables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        onFirstRun();
        mContainer = (FrameLayout) findViewById(R.id.main_activity_container);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.questions_activity_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mPager.setAdapter(mPagerAdapter);
        circlePageIndicator.setViewPager(mPager);
        mContainer.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        fallingDrawables = new FallingDrawables(this, mContainer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // This is not done in OnCreate because the animation is stopped whenever the activity is left.
        // So we need to restart the animation when activity resumes
        if (!fallingDrawables.getIsRunning()) {
            fallingDrawables.createAnimation();
            fallingDrawables.setmDrawablesInRow();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new FrontPageFragment();

            return new StatisticsFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private void onFirstRun() {
        /*
        * check if app is run for the first time.
        * Initialize SequenceAnswersDetails database with default values
         */
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        if (prefs.getBoolean(Constants.FIRST_RUN, true)) {
            MCQAnswersDetails.initializeDatabase(getApplicationContext());
            WordAnswerDetails.initializeDatabase(getApplicationContext());
            prefs.edit().putBoolean(Constants.FIRST_RUN, false).commit();
        }
    }

    public FallingDrawables getFallingDrawables() {
        return fallingDrawables;
    }
}