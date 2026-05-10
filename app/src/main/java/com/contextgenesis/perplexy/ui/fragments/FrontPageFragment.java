package com.contextgenesis.perplexy.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.contextgenesis.perplexy.PerplexyApplication;
import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.elements.GenericAnswerDetails;
import com.contextgenesis.perplexy.ui.HelpActivity;
import com.contextgenesis.perplexy.ui.MainActivity;
import com.contextgenesis.perplexy.ui.NumberLineActivity;
import com.contextgenesis.perplexy.utils.Analytics;
import com.contextgenesis.perplexy.utils.Constants;
import com.contextgenesis.perplexy.utils.SoundManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FrontPageFragment extends Fragment {

    @Bind(R.id.home_tv_heading)
    TextView heading;

    @Bind(R.id.home_tv_lvl_text)
    TextView gameTypeText;

    @Bind(R.id.home_seekbar)
    SeekBar gameSeekBar;

    @Bind(R.id.home_play)
    ImageView playButton;

    @Bind(R.id.game_1)
    CircularProgressBar gameType1;
    @Bind(R.id.game_2)
    CircularProgressBar gameType2;
    @Bind(R.id.game_3)
    CircularProgressBar gameType3;

    private int selectedGameType = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_frontpage, container, false);

        ButterKnife.bind(this, rootView);

        gameSeekBar.setProgress(1);
        gameType2.performClick();
        gameTypeText.setText(getGameTypeText(1));
        resetLevelSizes(1);

//        setUpSeekBar();

        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "tagus.ttf");
        heading.setTypeface(typeFace);

        for (int category = 0; category < 3; category++) {
            ArrayList<GenericAnswerDetails> answerDetails = GenericAnswerDetails.listAll(category);
            float correctCount = 0;
            for (GenericAnswerDetails answerDetail : answerDetails) {
                if (answerDetail.status == Constants.CORRECT) {
                    correctCount++;
                }
            }

            if ((int)correctCount == 0) {
                correctCount = 1;
            }
            if (category == Constants.GAME_TYPE_LOGIC) {
                gameType1.setProgress(correctCount / answerDetails.size() * 100);
            }
            if (category == Constants.GAME_TYPE_RIDDLE) {
                gameType2.setProgress(correctCount / answerDetails.size() * 100);
            }
            if (category == Constants.GAME_TYPE_SEQUENCES) {
                gameType3.setProgress(correctCount / answerDetails.size() * 100);
            }
        }

        return rootView;
    }

    private void setUpSeekBar() {
        gameSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                final Animation slideOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);
                final Animation slideIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);

                slideOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        gameTypeText.setText(getGameTypeText(progress));
                        gameTypeText.startAnimation(slideIn);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                selectedGameType = progress;
                resetLevelSizes(progress);
                gameTypeText.startAnimation(slideOut);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void statusChanges(final int progress) {
        if (selectedGameType == progress) {
            return;
        }

        final Animation slideOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);
        final Animation slideIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);

        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gameTypeText.setText(getGameTypeText(progress));
                gameTypeText.startAnimation(slideIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        selectedGameType = progress;
        resetLevelSizes(progress);
        gameTypeText.startAnimation(slideOut);
    }

    @OnClick(R.id.game_1)
    public void onClickGame1() {
        gameSeekBar.setProgress(0);
        SoundManager.playSwipeSound(getActivity());
        statusChanges(0);
    }

    @OnClick(R.id.game_2)
    public void onClickGame2() {
        gameSeekBar.setProgress(1);
        SoundManager.playSwipeSound(getActivity());
        statusChanges(1);
    }

    @OnClick(R.id.game_3)
    public void onClickGame3() {
        gameSeekBar.setProgress(2);
        SoundManager.playSwipeSound(getActivity());
        statusChanges(2);
    }

    @OnClick(R.id.home_play)
    public void playGame() {
        /*
        *Send which game type user chose with this intent
         */
        Intent questionsActivity = new Intent(getActivity(), NumberLineActivity.class);
        /*Implement switch case here once we set up code and questions*/
        questionsActivity.putExtra(Constants.BUNDLE_QUESTION_CATEGORY, selectedGameType);
        startActivity(questionsActivity);
        PerplexyApplication application = (PerplexyApplication) getActivity().getApplication();
        application.getDefaultTracker().send(new HitBuilders.EventBuilder()
                .setCategory(Analytics.CATEGORY_UI).setAction(Analytics.ACTION_PLAY_CATEGORY).setValue(selectedGameType).build());
        /* This will stop the falling drawables animation when the activity has been left. Improves performance */
        ((MainActivity) getActivity()).getFallingDrawables().stopAnimation();
        SoundManager.playButtonClickSound(getActivity());
    }


    private String getGameTypeText(int lvl) {
        switch (lvl) {
            case 0:
                return "Do you have the LOGIC in you?";
            case 1:
                return "RIDDLE me this";
            case 2:
                return "S,E,Q,U,E,N,C,E,S";
            case 3:
                return "4 Pictures 1 Word";
            default:
                return "Error in selecting level";
        }
    }

    private void resetLevelSizes(int lvl) {
        gameType1.requestLayout();
        gameType1.getLayoutParams().height = convertDip2Pixels(getActivity(), 38);
        gameType1.getLayoutParams().width = convertDip2Pixels(getActivity(), 38);
        gameType2.requestLayout();
        gameType2.getLayoutParams().height = convertDip2Pixels(getActivity(), 38);
        gameType2.getLayoutParams().width = convertDip2Pixels(getActivity(), 38);
        gameType3.requestLayout();
        gameType3.getLayoutParams().height = convertDip2Pixels(getActivity(), 38);
        gameType3.getLayoutParams().width = convertDip2Pixels(getActivity(), 38);

        gameType1.setColor(getResources().getColor(R.color.white));
        gameType2.setColor(getResources().getColor(R.color.white));
        gameType3.setColor(getResources().getColor(R.color.white));

        switch (lvl) {
            case 0:
                gameType1.requestLayout();
                gameType1.getLayoutParams().height = convertDip2Pixels(getActivity(), 55);
                gameType1.getLayoutParams().width = convertDip2Pixels(getActivity(), 55);
                gameType1.setColor(getResources().getColor(R.color.green_progress));
                return;
            case 1:
                gameType2.requestLayout();
                gameType2.getLayoutParams().height = convertDip2Pixels(getActivity(), 55);
                gameType2.getLayoutParams().width = convertDip2Pixels(getActivity(), 55);
                gameType2.setColor(getResources().getColor(R.color.green_progress));
                return;
            case 2:
                gameType3.requestLayout();
                gameType3.getLayoutParams().height = convertDip2Pixels(getActivity(), 55);
                gameType3.getLayoutParams().width = convertDip2Pixels(getActivity(), 55);
                gameType3.setColor(getResources().getColor(R.color.green_progress));
                return;
            /*case 3:
                gameType4.requestLayout();
                gameType4.getLayoutParams().height = 55;
                gameType4.getLayoutParams().width = 55;
                return;*/
            default:
                return;
        }
    }

    @OnClick(R.id.home_settings_button)
    public void openSettings() {
        ((MainActivity) getActivity()).goToSettings();
    }

    @OnClick(R.id.home_statistics_button)
    public void openStats() {
        ((MainActivity) getActivity()).goToStats();
    }

    public static int convertDip2Pixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Make the activity fullscreen */
        for (int category = 0; category < 3; category++) {
            ArrayList<GenericAnswerDetails> answerDetails = GenericAnswerDetails.listAll(category);
            float correctCount = 0;
            for (GenericAnswerDetails answerDetail : answerDetails) {
                if (answerDetail.status == Constants.CORRECT) {
                    correctCount++;
                }
            }
            if ((int)correctCount == 0) {
                correctCount = 1;
            }
            if (category == Constants.GAME_TYPE_LOGIC) {
                gameType1.setProgress(correctCount / answerDetails.size() * 100);
            }
            if (category == Constants.GAME_TYPE_RIDDLE) {
                gameType2.setProgress(correctCount / answerDetails.size() * 100);
            }
            if (category == Constants.GAME_TYPE_SEQUENCES) {
                gameType3.setProgress(correctCount / answerDetails.size() * 100);
            }
        }

    }
}