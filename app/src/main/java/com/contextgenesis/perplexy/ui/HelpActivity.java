package com.contextgenesis.perplexy.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.databinding.ActivityHelpBinding;
import com.contextgenesis.perplexy.utils.SoundManager;

public class HelpActivity extends Activity {

    private ActivityHelpBinding binding;

    String charTextArray[];

    int COUNTER = 0;

    private Animation slideIn, slideOut;
    private Animation fadeIn, fadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.helpPrev.setOnClickListener(v -> onPrev());
        binding.helpNext.setOnClickListener(v -> onNext());
        binding.helpSkip.setOnClickListener(v -> onSkip());

        slideOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_bottom);
        slideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_top);
        fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        charTextArray = getResources().getStringArray(R.array.helpText);
        binding.helpPrev.setVisibility(View.INVISIBLE);
        animateImage(COUNTER);
        binding.helpText.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onPrev() {
        SoundManager.playSwipeSound(getApplicationContext());
        animateImage(--COUNTER);
        setButtonVisibility();
        setBackground();
    }

    public void onNext() {
        SoundManager.playSwipeSound(getApplicationContext());
        if (++COUNTER >= 11) {
            finish();
            return;
        }
        animateImage(COUNTER);
        setButtonVisibility();
        setBackground();
    }

    public void onSkip() {
        finish();
        return;
    }


    private void setButtonVisibility() {
        if (COUNTER <= 0) {
            binding.helpPrev.setVisibility(View.INVISIBLE);
        } else {
            binding.helpPrev.setVisibility(View.VISIBLE);
        }
        if (COUNTER >= 10) {
            binding.helpNext.setImageResource(R.drawable.ok);
        } else {
            binding.helpNext.setImageResource(R.drawable.right);
        }
    }

    public void setCharText(int COUNTER) {
        try {
            binding.helpText.setText("" + charTextArray[COUNTER]);
        }
        catch (ArrayIndexOutOfBoundsException e){
            Log.e("Tutorial",e.getMessage());
        }

    }

    public void setImage(int COUNTER) {
        SharedPreferences prefs = getSharedPreferences(CharacterStore.CHAR_SHARED_PREFS, Context.MODE_PRIVATE);
        String resourceString = prefs.getString(CharacterStore.STRING_EXPRESSION_HAPPY_CLOSED, "tutorial" + COUNTER);
        int resourceID = getResources().getIdentifier(resourceString, "drawable", getPackageName());
        binding.helpIm0.setImageResource(resourceID);
        animateText();
    }

    private void animateText() {
        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setCharText(COUNTER);
                binding.helpText.startAnimation(slideIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.helpText.startAnimation(slideOut);
    }

    private void setBackground() {
        switch (COUNTER) {
            case 0:
                binding.helpMain.setBackgroundColor(getResources().getColor(R.color.blue_d_grey));
                break;
            case 1:
                binding.helpMain.setBackgroundColor(getResources().getColor(R.color.black_d_material_black));
                break;
            case 2:
                binding.helpMain.setBackgroundColor(getResources().getColor(R.color.black_d_material));
                break;
            case 3:
                binding.helpMain.setBackgroundColor(getResources().getColor(R.color.purple_l_plum));
                break;
            case 4:
                binding.helpMain.setBackgroundColor(getResources().getColor(R.color.purple_l_plum));
                break;
            case 5:
                binding.helpMain.setBackgroundColor(getResources().getColor(R.color.blue_l_steel_blue));
                break;
            case 6:
                binding.helpMain.setBackgroundColor(getResources().getColor(R.color.blue_l_steel_blue));
                break;
            case 7:
                binding.helpMain.setBackgroundColor(getResources().getColor(R.color.red_l_chestnut));
                break;
            case 8:
                binding.helpMain.setBackgroundColor(getResources().getColor(R.color.red_l_chestnut));
                break;
            case 9:
                binding.helpMain.setBackgroundColor(getResources().getColor(R.color.purple_l_plum));
                break;
            case 10:
                binding.helpMain.setBackgroundColor(getResources().getColor(R.color.blue_d_grey));
                break;
        }
    }

    private void animateImage(final int COUNTER) {
//        fadeOut.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                setImage(COUNTER);
//                image0.startAnimation(fadeIn);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        image0.startAnimation(fadeOut);
        setImage(COUNTER);
    }
}
