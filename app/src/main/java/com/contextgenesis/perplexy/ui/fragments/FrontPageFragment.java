package com.contextgenesis.perplexy.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.contextgenesis.perplexy.PerplexyApplication;
import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.databinding.FragmentFrontpageBinding;
import com.contextgenesis.perplexy.elements.GenericAnswerDetails;
import com.contextgenesis.perplexy.ui.HelpActivity;
import com.contextgenesis.perplexy.ui.MainActivity;
import com.contextgenesis.perplexy.ui.NumberLineActivity;
import com.contextgenesis.perplexy.utils.Analytics;
import com.contextgenesis.perplexy.utils.Constants;
import com.contextgenesis.perplexy.utils.SoundManager;

import java.util.ArrayList;

public class FrontPageFragment extends Fragment {

    private FragmentFrontpageBinding binding;

    private int selectedGameType = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFrontpageBinding.inflate(inflater, container, false);

        binding.game1.setOnClickListener(v -> onClickGame1());
        binding.game2.setOnClickListener(v -> onClickGame2());
        binding.game3.setOnClickListener(v -> onClickGame3());
        binding.homePlay.setOnClickListener(v -> playGame());
        binding.homeSettingsButton.setOnClickListener(v -> openSettings());
        binding.homeStatisticsButton.setOnClickListener(v -> openStats());

        binding.homeSeekbar.setProgress(1);
        binding.game2.performClick();
        binding.homeTvLvlText.setText(getGameTypeText(1));
        resetLevelSizes(1);

//        setUpSeekBar();

        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "tagus.ttf");
        binding.homeTvHeading.setTypeface(typeFace);

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
                binding.game1.setProgress(correctCount / answerDetails.size() * 100);
            }
            if (category == Constants.GAME_TYPE_RIDDLE) {
                binding.game2.setProgress(correctCount / answerDetails.size() * 100);
            }
            if (category == Constants.GAME_TYPE_SEQUENCES) {
                binding.game3.setProgress(correctCount / answerDetails.size() * 100);
            }
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setUpSeekBar() {
        binding.homeSeekbar.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, final int progress, boolean fromUser) {
                final Animation slideOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);
                final Animation slideIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);

                slideOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        binding.homeTvLvlText.setText(getGameTypeText(progress));
                        binding.homeTvLvlText.startAnimation(slideIn);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                selectedGameType = progress;
                resetLevelSizes(progress);
                binding.homeTvLvlText.startAnimation(slideOut);
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {

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
                binding.homeTvLvlText.setText(getGameTypeText(progress));
                binding.homeTvLvlText.startAnimation(slideIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        selectedGameType = progress;
        resetLevelSizes(progress);
        binding.homeTvLvlText.startAnimation(slideOut);
    }

    public void onClickGame1() {
        binding.homeSeekbar.setProgress(0);
        SoundManager.playSwipeSound(getActivity());
        statusChanges(0);
    }

    public void onClickGame2() {
        binding.homeSeekbar.setProgress(1);
        SoundManager.playSwipeSound(getActivity());
        statusChanges(1);
    }

    public void onClickGame3() {
        binding.homeSeekbar.setProgress(2);
        SoundManager.playSwipeSound(getActivity());
        statusChanges(2);
    }

    public void playGame() {
        /*
        *Send which game type user chose with this intent
         */
        Intent questionsActivity = new Intent(getActivity(), NumberLineActivity.class);
        /*Implement switch case here once we set up code and questions*/
        questionsActivity.putExtra(Constants.BUNDLE_QUESTION_CATEGORY, selectedGameType);
        startActivity(questionsActivity);
        android.os.Bundle params = new android.os.Bundle();
        params.putInt("game_type", selectedGameType);
        ((com.contextgenesis.perplexy.PerplexyApplication) getActivity().getApplication())
                .getFirebaseAnalytics().logEvent(Analytics.ACTION_PLAY_CATEGORY, params);
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
        binding.game1.requestLayout();
        binding.game1.getLayoutParams().height = convertDip2Pixels(getActivity(), 38);
        binding.game1.getLayoutParams().width = convertDip2Pixels(getActivity(), 38);
        binding.game2.requestLayout();
        binding.game2.getLayoutParams().height = convertDip2Pixels(getActivity(), 38);
        binding.game2.getLayoutParams().width = convertDip2Pixels(getActivity(), 38);
        binding.game3.requestLayout();
        binding.game3.getLayoutParams().height = convertDip2Pixels(getActivity(), 38);
        binding.game3.getLayoutParams().width = convertDip2Pixels(getActivity(), 38);

        binding.game1.setColor(getResources().getColor(R.color.white));
        binding.game2.setColor(getResources().getColor(R.color.white));
        binding.game3.setColor(getResources().getColor(R.color.white));

        switch (lvl) {
            case 0:
                binding.game1.requestLayout();
                binding.game1.getLayoutParams().height = convertDip2Pixels(getActivity(), 55);
                binding.game1.getLayoutParams().width = convertDip2Pixels(getActivity(), 55);
                binding.game1.setColor(getResources().getColor(R.color.green_progress));
                return;
            case 1:
                binding.game2.requestLayout();
                binding.game2.getLayoutParams().height = convertDip2Pixels(getActivity(), 55);
                binding.game2.getLayoutParams().width = convertDip2Pixels(getActivity(), 55);
                binding.game2.setColor(getResources().getColor(R.color.green_progress));
                return;
            case 2:
                binding.game3.requestLayout();
                binding.game3.getLayoutParams().height = convertDip2Pixels(getActivity(), 55);
                binding.game3.getLayoutParams().width = convertDip2Pixels(getActivity(), 55);
                binding.game3.setColor(getResources().getColor(R.color.green_progress));
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

    public void openSettings() {
        ((MainActivity) getActivity()).goToSettings();
    }

    public void openStats() {
        ((MainActivity) getActivity()).goToStats();
    }

    public static int convertDip2Pixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding == null) return;
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
                binding.game1.setProgress(correctCount / answerDetails.size() * 100);
            }
            if (category == Constants.GAME_TYPE_RIDDLE) {
                binding.game2.setProgress(correctCount / answerDetails.size() * 100);
            }
            if (category == Constants.GAME_TYPE_SEQUENCES) {
                binding.game3.setProgress(correctCount / answerDetails.size() * 100);
            }
        }

    }
}
