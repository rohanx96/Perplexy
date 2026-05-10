package com.contextgenesis.perplexy.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.contextgenesis.perplexy.databinding.ActivityQuestionsBinding;
import com.contextgenesis.perplexy.ui.dialogs.BankDialog;
import com.contextgenesis.perplexy.utils.FallingDrawables;
import com.contextgenesis.perplexy.utils.ShareQuestion;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.callbacks.QuestionsCallback;
import com.contextgenesis.perplexy.elements.GenericAnswerDetails;
import com.contextgenesis.perplexy.ui.fragments.QuestionMCQFragment;
import com.contextgenesis.perplexy.ui.fragments.QuestionTextBoxFragment;
import com.contextgenesis.perplexy.ui.fragments.QuestionWordFragment;
import com.contextgenesis.perplexy.utils.CharacterUtils;
import com.contextgenesis.perplexy.utils.Coins;
import com.contextgenesis.perplexy.utils.Constants;
import com.contextgenesis.perplexy.utils.JSONUtils;
import com.contextgenesis.perplexy.utils.SoundManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

/**
 * Created by rose on 6/3/16.
 */

public class QuestionsActivity extends AppCompatActivity implements QuestionsCallback, RewardedVideoAdListener {

    private ScreenSlidePagerAdapter pagerAdapter;
    private final int NO_OF_COLORS = 7;
    private ImageView character;
    private int mCurrentPage;
    private boolean isCharacterDialogOpen = false;
    private boolean isLocked = false;
    //InterstitialAd mVideoAd;

    RewardedVideoAd mVideoAd;
    private boolean mIsRewardedVideoLoading;
    private final Object mLock = new Object();

    InterstitialAd mInterstitialAd;
    SharedPreferences pref;

    int CATEGORY = -1;

    private ActivityQuestionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setAllowEnterTransitionOverlap(false);
//            getWindow().setAllowReturnTransitionOverlap(false);
        }

        /* The page position is one less than question number. Note question number is passed to activity instead of position */
        mCurrentPage = getIntent().getIntExtra(Constants.BUNDLE_QUESTION_NUMBER, 0) - 1;
        CATEGORY = getIntent().getIntExtra(Constants.BUNDLE_QUESTION_CATEGORY, -1);

        binding.questionsActivityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    getWindow().setAllowEnterTransitionOverlap(false);
//                }
                SoundManager.playButtonClickSound(getApplicationContext());
                onBackPressed();
            }
        });

        binding.questionsActivityCoinText.setOnClickListener(v -> onClick_contribute());
        binding.questionsActivityCoinImage.setOnClickListener(v -> onClick_contribute2());

        if (GenericAnswerDetails.getStatus(mCurrentPage + 1, CATEGORY) == Constants.CORRECT) {
            binding.questionsActivityCorrectIndicator.setImageResource(R.drawable.tick_green);
        } else if (GenericAnswerDetails.getStatus(mCurrentPage + 1, CATEGORY) == Constants.INCORRECT) {
            binding.questionsActivityCorrectIndicator.setImageResource(R.drawable.cross);
        } else {
            binding.questionsActivityCorrectIndicator.setImageResource(0);
        }
        setupCharacter();
        setupAd();
    }

    public void onClick_contribute() {
//     TODO: in app include later
//     new BankDialog(this).show();
    }

    public void onClick_contribute2() {
//     TODO: in app include later
//     new BankDialog(this).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        /* Make the activity fullscreen */
        binding.questionsActivityContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setUpViewPager();
        binding.questionsActivityContainer.setBackgroundColor(FallingDrawables.getLightBackgroundColor(mCurrentPage, getApplicationContext()));
        binding.linearLayout.setBackgroundColor(FallingDrawables.getLightBackgroundColor(mCurrentPage, getApplicationContext()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.questionsActivityPager.clearOnPageChangeListeners();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i("QuestionsActivity", " Handling permission request result");
        switch (requestCode) {
            case ShareQuestion.REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Preparing for Share", Toast.LENGTH_LONG).show();
                    ShareQuestion.shareImageWhatsapp(this);
                } else {
                    Snackbar.make(binding.questionsActivityContainer, "Cannot share. Please grant the write to external storage permission", Snackbar.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void setUpViewPager() {
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        binding.questionsActivityPager.setAdapter(pagerAdapter);
        binding.questionsActivityPager.setPageMargin(convertDip2Pixels(this, 16));
        binding.questionsActivityPager.setPageTransformer(true, new DepthPageTransformer());

        binding.questionsActivityPager.setCurrentItem(mCurrentPage);
        binding.questionsActivityLevel.setText("Level " + (mCurrentPage + 1));

        pref = getBaseContext().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        binding.questionsActivityCoinText.setText(pref.getLong(Constants.PREF_COINS, 0) + " ");

        binding.questionsActivityPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //See note above for why this is needed
                ValueAnimator colorAnimator;
                colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                        FallingDrawables.getLightBackgroundColor(mCurrentPage % NO_OF_COLORS, QuestionsActivity.this),
                        FallingDrawables.getLightBackgroundColor(position % NO_OF_COLORS, QuestionsActivity.this));
                colorAnimator.setDuration(500).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        binding.questionsActivityContainer.setBackgroundColor((int) animation.getAnimatedValue());
                    }
                });
                ValueAnimator colorAnimator2 = ValueAnimator.ofObject(new ArgbEvaluator(),
                        FallingDrawables.getLightBackgroundColor(mCurrentPage % NO_OF_COLORS, QuestionsActivity.this),
                        FallingDrawables.getLightBackgroundColor(position % NO_OF_COLORS, QuestionsActivity.this));
                colorAnimator.setDuration(500).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        binding.linearLayout.setBackgroundColor((int) animation.getAnimatedValue());
                    }
                });
                colorAnimator.start();
                colorAnimator2.start();
                mCurrentPage = position;
                binding.questionsActivityLevel.setText("Level " + (mCurrentPage + 1));
                hideCorrectAnswerFeedback();
                hideIncorrectAnswerFeedback();
                if (isCharacterDialogOpen) {
                    hideCharacterDialog();
                    hideCharacterUnlockDialog();
                }
                isLocked = (GenericAnswerDetails.getStatus(mCurrentPage + 1, CATEGORY) == Constants.INCORRECT)
                        || (GenericAnswerDetails.getStatus(mCurrentPage + 1, CATEGORY) == Constants.UNAVAILABLE);
                Log.i("lock status ", " position " + mCurrentPage + 1 + " " + isLocked);

                if (isLocked)
                    CharacterUtils.setCharacterDrawable(getApplicationContext(), character, CharacterUtils.EXPRESSION_SAD_CLOSED);

                if (GenericAnswerDetails.getStatus(mCurrentPage + 1, CATEGORY) == Constants.INCORRECT) {
                    binding.questionsActivityCorrectIndicator.setImageResource(R.drawable.cross);
                } else if (GenericAnswerDetails.getStatus(mCurrentPage + 1, CATEGORY) == Constants.CORRECT) {
                    binding.questionsActivityCorrectIndicator.setImageResource(R.drawable.tick_green);
                } else
                    binding.questionsActivityCorrectIndicator.setImageResource(0);

                // Remove the lock image on the fragment if question was previously locked but is now unlocked
                // finding view by id and then removing it does not work even if unique IDs are assigne to lock image view
                // Another way to do this is getCurrentFragment and call method of fragment that removes the view from container
                /*if (!isLocked) {
                    ((QuestionsFragment)pagerAdapter.getItem(mCurrentPage)).unlockQuestion();
                }*/
                // The above method also does not work because the view cardContent used in unlockQuestion method is always null.
                // So currently this issue is resolved by calling notifyDataSetChanged every time user correctly answers a question
                // or unlocks a new question
                // Another possible solution could be to display the lock image in the activity instead of fragments
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Unused
            }
        });
    }

    @Override
    public void toggleIsCharacterOpen() {
        isCharacterDialogOpen = !isCharacterDialogOpen;
    }

    @Override
    public void setIsQuestionLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    @Override
    public int unlockNextQuestion(int category) {
        return GenericAnswerDetails.unlockNextQuestion(category);
    }

    @Override
    public void refreshAdapter() {
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public float getCharacterX() {
        return character.getX();
    }

    @Override
    public float getCharacterY() {
        return character.getY();
    }

    @Override
    public void setupCharacterDialog() {
        CharacterHelper characterHelper = new CharacterHelper(this);
        characterHelper.setupCharacterDialog(CATEGORY, mCurrentPage, getApplicationContext());
    }

    @Override
    public void showCharacterDialog() {
        final View characterDialog = findViewById(R.id.questions_activity_character_dialog);
        startCharacterDialogShowAnimation(characterDialog);
        CharacterUtils.setCharacterDrawable(getApplicationContext(), character, CharacterUtils.EXPRESSION_HAPPY_OPEN);
        toggleIsCharacterOpen();
    }

    @Override
    public void hideCharacterDialog() {
        final View characterDialog = findViewById(R.id.questions_activity_character_dialog);
        // This will prevent running of animation when hiding not visible dialog.
        // This helps because we can now call this method even if the view is not visible
        if (characterDialog.getVisibility() == View.VISIBLE) {
            startCharacterDialogHideAnimation(characterDialog);
            CharacterUtils.setCharacterDrawable(getApplicationContext(), character, CharacterUtils.EXPRESSION_HAPPY_CLOSED);
            toggleIsCharacterOpen();
        }
    }

    @Override
    public void showCharacterUnlockDialog() {
        final View characterDialog = findViewById(R.id.questions_activity_character_dialog_unlock);
        startCharacterDialogShowAnimation(characterDialog);
        CharacterUtils.setCharacterDrawable(getApplicationContext(), character, CharacterUtils.EXPRESSION_BLUSH);
        toggleIsCharacterOpen();
    }

    @Override
    public void setupCharacterUnlockDialog() {
        CharacterHelper characterHelper = new CharacterHelper(this);
        characterHelper.setupCharacterUnlockDialog(CATEGORY, mCurrentPage, getApplicationContext());

    }

    @Override
    public void hideCharacterUnlockDialog() {
        final View characterDialog = findViewById(R.id.questions_activity_character_dialog_unlock);
        // This will prevent running of animation when hiding not visible dialog.
        // This helps because we can now call this method even if the view is not visible
        if (characterDialog.getVisibility() == View.VISIBLE) {
            startCharacterDialogHideAnimation(characterDialog);
            CharacterUtils.setCharacterDrawable(getApplicationContext(), character, CharacterUtils.EXPRESSION_SAD_CLOSED);
            toggleIsCharacterOpen();
        }
    }

    @Override
    public void setupCorrectAnswerFeedback(int nextQuestionUnlocked) {
        CharacterHelper helper = new CharacterHelper(this);
        helper.setupCorrectAnswerFeedback(CATEGORY, mCurrentPage, nextQuestionUnlocked, getApplicationContext());
    }

    @Override
    public void showCorrectAnswerFeedback(int nextQuestion) {
        final View characterDialog = findViewById(R.id.questions_activity_character_feedback_correct);
        startCharacterDialogShowAnimation(characterDialog);
        CharacterUtils.setCharacterDrawable(getApplicationContext(), character, CharacterUtils.EXPRESSION_HAPPY_OPEN);
        setupCorrectAnswerFeedback(nextQuestion);
        toggleIsCharacterOpen();
    }

    @Override
    public void hideCorrectAnswerFeedback() {
        final View characterDialog = findViewById(R.id.questions_activity_character_feedback_correct);
        // This will prevent running of animation when hiding not visible dialog.
        // This helps because we can now call this method even if the view is not visible
        if (characterDialog.getVisibility() == View.VISIBLE) {
            startCharacterDialogHideAnimation(characterDialog);
            toggleIsCharacterOpen();
        }
        CharacterUtils.setCharacterDrawable(getApplicationContext(), character, CharacterUtils.EXPRESSION_HAPPY_CLOSED);
    }

    @Override
    public void setupIncorrectAnswerFeedback() {
        CharacterHelper helper = new CharacterHelper(this);
        helper.setupIncorrectAnswerFeedback(CATEGORY, mCurrentPage, getApplicationContext());
    }

    @Override
    public void showIncorrectAnswerFeedback() {
        final View characterDialog = findViewById(R.id.questions_activity_character_feedback_incorrect);
        startCharacterDialogShowAnimation(characterDialog);
        CharacterUtils.setCharacterDrawable(getApplicationContext(), character, CharacterUtils.EXPRESSION_SHOCKED);
        setupIncorrectAnswerFeedback();
        toggleIsCharacterOpen();
    }

    @Override
    public void hideIncorrectAnswerFeedback() {
        final View characterDialog = findViewById(R.id.questions_activity_character_feedback_incorrect);
        // This will prevent running of animation when hiding not visible dialog.
        // This helps because we can now call this method even if the view is not visible
        if (characterDialog.getVisibility() == View.VISIBLE) {
            startCharacterDialogHideAnimation(characterDialog);
            toggleIsCharacterOpen();
            // Question is locked so we need to set the character as sad
            CharacterUtils.setCharacterDrawable(getApplicationContext(), character, CharacterUtils.EXPRESSION_SAD_CLOSED);
        }
    }

    @Override
    public void showAd(boolean isVideoAd) {
        if (isVideoAd) {
            if (mVideoAd.isLoaded()) {
                mVideoAd.show();
            } else {
                Snackbar.make(binding.questionsActivityContainer, "Unable to load ad. Please try again later", Snackbar.LENGTH_LONG).show();
                requestNewVideoAd();
            }
        } else if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else
            requestNewInterstitial(false);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BUNDLE_QUESTION_NUMBER, position + 1); // The question number is position + 1
            bundle.putInt(Constants.BUNDLE_QUESTION_CATEGORY, CATEGORY);

            if (JSONUtils.getQuestionAt(getApplicationContext(), CATEGORY, position).layout_type == 0) {
                return QuestionMCQFragment.newInstance(bundle);
            } else if (JSONUtils.getQuestionAt(getApplicationContext(), CATEGORY, position).layout_type == 1) {
                return QuestionWordFragment.newInstance(bundle);
            } else if (JSONUtils.getQuestionAt(getApplicationContext(), CATEGORY, position).layout_type == 2) {
                return QuestionTextBoxFragment.newInstance(bundle);
            }
            return QuestionWordFragment.newInstance(bundle);
        }

        @Override
        public int getCount() {
            return JSONUtils.getTotalQuestions(getApplicationContext(), CATEGORY);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private float MIN_SCALE = 0.85f;
        private float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
                character.animate().translationY(pageWidth * (-1 * position)).setDuration(0).start();

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                character.animate().translationY(pageWidth * position).setDuration(0).start();

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public void setupCharacter() {
        character = (ImageView) findViewById(R.id.questions_activity_bubble);
        if (Build.VERSION.SDK_INT >= 21)
            character.setTransitionName("character");
        character.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(1.3f).scaleY(1.3f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        character.clearAnimation();
                        character.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                        isLocked = (GenericAnswerDetails.getStatus(mCurrentPage + 1, CATEGORY) == Constants.INCORRECT)
                                || (GenericAnswerDetails.getStatus(mCurrentPage + 1, CATEGORY) == Constants.UNAVAILABLE);
                        if (!isCharacterDialogOpen) {
                            if (isLocked) {
                                showCharacterUnlockDialog();
                                setupCharacterUnlockDialog();
                            } else {
                                showCharacterDialog();
                                //Dialog is reinitialised based on question every time character is clicked
                                setupCharacterDialog();
                            }
                            SoundManager.playCharacterOpenedSound(getApplicationContext());
                        } else {
                            SoundManager.playCharacterClosedSound(getApplicationContext());
                            if (isLocked) {
                                hideCharacterUnlockDialog();
                                hideIncorrectAnswerFeedback();
                            } else {
                                hideIncorrectAnswerFeedback();
                                hideCharacterDialog();
                                hideCorrectAnswerFeedback();
                            }
                        }
                }
                return true;
            }
        });
    }

    public void gotoQuestion(int questionNumber) {
        if (questionNumber == -1)
            binding.questionsActivityPager.setCurrentItem(mCurrentPage + 1, true);
        else binding.questionsActivityPager.setCurrentItem(questionNumber - 1, true);
    }

    public static int convertDip2Pixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    public void startCharacterDialogShowAnimation(final View characterDialog) {
        /* Animation for post Lollipop devices*/
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            characterDialog.post(new Runnable() {
                @Override
                public void run() {
                    int cx = characterDialog.getWidth() / 2;
                    int cy = characterDialog.getHeight();
                    int radius = characterDialog.getHeight();

                    Animator animator = ViewAnimationUtils.createCircularReveal(characterDialog, cx, cy, 0, radius);
                    animator.start();
                    characterDialog.setVisibility(View.VISIBLE);
                }
            });
        } else {*/
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, character.getX(), character.getY());
        scaleAnimation.setDuration(500);
        scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        characterDialog.setVisibility(View.VISIBLE);
        characterDialog.startAnimation(scaleAnimation);
        //}
    }

    public void startCharacterDialogHideAnimation(final View characterDialog) {
        /* Animation for post Lollipop devices */
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            characterDialog.post(new Runnable() {
                @Override
                public void run() {
                    int cx = characterDialog.getWidth() / 2;
                    int cy = characterDialog.getHeight();
                    int radius = characterDialog.getHeight();

                    Animator animator = ViewAnimationUtils.createCircularReveal(characterDialog, cx, cy, radius, 0);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            characterDialog.setVisibility(View.GONE);
                        }
                    });
                    animator.start();
                }
            });
        } else {*/
        characterDialog.setVisibility(View.GONE);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f, character.getX(), character.getY());
        scaleAnimation.setDuration(500);
        scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        characterDialog.startAnimation(scaleAnimation);
        // }
    }

    private void setupAd() {
        /*mVideoAd = new InterstitialAd(this);
        mVideoAd.setAdUnitId(getString(R.string.video_ad_id));
        mVideoAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                afterAdWatched();
                requestNewInterstitial(true);
            }
        });*/
        mVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mVideoAd.setRewardedVideoAdListener(this);
        requestNewVideoAd();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                pref.edit().putInt(Constants.PREF_SHOW_AD, 0).apply();
                super.onAdClosed();
            }
        });
        requestNewInterstitial(false);
    }

    private void requestNewInterstitial(boolean isVideoAd) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("C40E23EA84B9F2235B07CE0531A253AB") //Rohan
                .addTestDevice("CDCEF54FDF7F3A4DEC120209B12D78C6") // Rishab
                .addTestDevice("D40CA2BD5C7E81CF7B1F9C31DFE05BE6")  // Dhruv
                .addTestDevice("8A2C1665B62A2ED7A01F6628CC2094A4")  // Dhruv N
                .addTestDevice("BE64FAA03F9F6E8E1EAD78815EFEF7C8")  // op5
                .addTestDevice("9975DC9A27F0D1B042C31A65D01EEB04") // Gaurav
                .addTestDevice("mydevice")
                .build();
        if (isVideoAd) {
            //    mVideoAd.loadAd(adRequest);
            requestNewVideoAd();
        } else
            mInterstitialAd.loadAd(adRequest);
    }

    private void requestNewVideoAd() {
        synchronized (mLock) {
            if (!mIsRewardedVideoLoading) {
                mIsRewardedVideoLoading = true;
                Bundle extras = new Bundle();
                extras.putBoolean("_noRefresh", true);
                AdRequest adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .addTestDevice("C40E23EA84B9F2235B07CE0531A253AB") //Rohan
                        .addTestDevice("CDCEF54FDF7F3A4DEC120209B12D78C6") // Rishab
                        .addTestDevice("D40CA2BD5C7E81CF7B1F9C31DFE05BE6")  // Dhruv
                        .addTestDevice("8A2C1665B62A2ED7A01F6628CC2094A4")  // Dhruv N
                        .addTestDevice("BE64FAA03F9F6E8E1EAD78815EFEF7C8")  // op5
                        .addTestDevice("9975DC9A27F0D1B042C31A65D01EEB04") // Gaurav
                        .addTestDevice("08BEF4E2E5265F493ABAEC3DDD496048") // Sarthak
                        .build();
                mVideoAd.loadAd(getString(R.string.reward_video_ad_id), adRequest);
            }
        }
    }

    public void afterAdWatched() {
        Coins.addCoinsFromAd(this);
        SoundManager.playCorrectAnswerSound(this);
        binding.questionsActivityCoinText.setText(String.format("%d", Coins.getCurrentCoins(this)));
    }

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "You earned 150 coins", Toast.LENGTH_SHORT).show();
        Coins.addCoinsFromAd(this);
        SoundManager.playCorrectAnswerSound(this);
        binding.questionsActivityCoinText.setText(String.format("%d", Coins.getCurrentCoins(this)));
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        //Toast.makeText(this, "onRewardedVideoAdLeftApplication",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        //Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
        requestNewVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        synchronized (mLock) {
            mIsRewardedVideoLoading = false;
        }
        //Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        synchronized (mLock) {
            mIsRewardedVideoLoading = false;
        }
        //Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        //Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        //Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }
}
