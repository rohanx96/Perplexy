package com.contextgenesis.perplexy.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.adapters.NumberLineAdapter;
import com.contextgenesis.perplexy.databinding.ActivityNumberLineBinding;
import com.contextgenesis.perplexy.elements.GenericAnswerDetails;
import com.contextgenesis.perplexy.ui.dialogs.BankDialog;
import com.contextgenesis.perplexy.utils.Constants;
import com.contextgenesis.perplexy.utils.FallingDrawables;
import com.contextgenesis.perplexy.utils.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class NumberLineActivity extends AppCompatActivity {
    private View mContainer;
    private int mTimeCount = 0;
    private boolean isAnimationRunning = false;

    int CATEGORY;

    private ActivityNumberLineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNumberLineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.activityNumberLineBack.setOnClickListener(v -> goBack());
        binding.activityCoinText.setOnClickListener(v -> onClick_contribute());
        binding.activityCoinImage.setOnClickListener(v -> onClick_contribute2());

        CATEGORY = getIntent().getIntExtra(Constants.BUNDLE_QUESTION_CATEGORY, -1);
        binding.activityNumberLineTitle.setText("Select Level");
        mContainer = binding.getRoot().findViewById(R.id.activity_number_line_container);
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        binding.activityCoinText.setText(prefs.getLong(Constants.PREF_COINS, 0) + "");

        //bubbleLL.setVisibility(View.GONE);
        ListView listView = (ListView) binding.getRoot().findViewById(R.id.activity_number_line_listview);
        NumberLineAdapter adapter = new NumberLineAdapter(this, new ArrayList<GenericAnswerDetails>());
        listView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        /* Make the activity fullscreen */

        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        binding.activityCoinText.setText(prefs.getLong(Constants.PREF_COINS, 0) + "");

        enterImmersiveMode();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        /* Adapter list needs to be initialised here because we need to refresh list after returning to activity */
        ArrayList<GenericAnswerDetails> answerDetails = GenericAnswerDetails.listAll(CATEGORY);
        ListView listView = (ListView) binding.getRoot().findViewById(R.id.activity_number_line_listview);
        ((NumberLineAdapter) listView.getAdapter()).setAnswerDetails(answerDetails);
        ((NumberLineAdapter) listView.getAdapter()).notifyDataSetChanged();

        /** Change color of background after 7 seconds */
        if (!isAnimationRunning) {
            Thread animationThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    isAnimationRunning = true;
                    final Handler handler = new Handler(Looper.getMainLooper());
                    while (isAnimationRunning) {
                        // sleep the thread to stop the while loop for 5 seconds
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        final ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                                FallingDrawables.getBackgroundColor(mTimeCount, getApplicationContext()),
                                FallingDrawables.getBackgroundColor(mTimeCount + 1, getApplicationContext()));
                        colorAnimator.setDuration(3000);
                        mTimeCount++;
                        if (mTimeCount == FallingDrawables.NO_OF_COLORS)
                            mTimeCount = 0;
                        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(final ValueAnimator animation) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mContainer.setBackgroundColor((int) animation.getAnimatedValue());
                                    }
                                });
                            }
                        });
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                colorAnimator.start();
                            }
                        });
                    }
                }
            });
            animationThread.start();
        }
    }

    private void enterImmersiveMode() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            android.view.WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(android.view.WindowInsets.Type.systemBars());
                controller.setSystemBarsBehavior(
                        android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) enterImmersiveMode();
    }

    public void setAnimationRunning(boolean animationRunning) {
        this.isAnimationRunning = animationRunning;
    }

    public void goBack() {
        SoundManager.playButtonClickSound(getApplicationContext());
        onBackPressed();
    }

    public void onClick_contribute() {
        //  TODO: In app include later
//        new BankDialog(this).show();
    }

    public void onClick_contribute2() {
        //  TODO: In app include later
//        new BankDialog(this).show();
    }

   /* @OnClick(R.id.activity_number_line_bubble_im)
    public void characterDialogPressed() {
        this.closeCharacterDialog();
    }

    @Override
    public void toggleCharacterOpen() {
        if (bubbleLL.getVisibility() == View.VISIBLE) {
            bubbleLL.setVisibility(View.GONE);
        } else if (bubbleLL.getVisibility() == View.GONE) {
            bubbleLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void openCharacterDialog(int TYPE) {

        bubbleLL.setVisibility(View.VISIBLE);
        TextView titleTv = (TextView) findViewById(R.id.char_unlock_dialog_tv);
        switch (TYPE) {
            case Constants.UNAVAILABLE:
                titleTv.setText("This question is unavailable");
                break;
            case Constants.INCORRECT:
                titleTv.setText("This question is locked. Would you like to unlock it?");
                break;
        }
    }

    @Override
    public void closeCharacterDialog() {
        bubbleLL.setVisibility(View.GONE);
    }*/

    /*public void updateCoinsDisplay(){
        TextView coinsText = (TextView) findViewById(R.id.activity_coin_text);
        coinsText.setText(PreferenceManager.getDefaultSharedPreferences(this).getInt(Constants.PREF_COINS,0));
    }*/

}
