package com.contextgenesis.perplexy.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.callbacks.QuestionsCallback;
import com.contextgenesis.perplexy.databinding.QuestionWordCardBinding;
import com.contextgenesis.perplexy.elements.GenericAnswerDetails;
import com.contextgenesis.perplexy.elements.GenericQuestion;
import com.contextgenesis.perplexy.ui.QuestionsActivity;
import com.contextgenesis.perplexy.utils.Coins;
import com.contextgenesis.perplexy.utils.Constants;
import com.contextgenesis.perplexy.utils.DrawingView;
import com.contextgenesis.perplexy.utils.JSONUtils;
import com.contextgenesis.perplexy.utils.SoundManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by rish on 9/3/16.
 */

public class QuestionWordFragment extends Fragment {

    int POSITION = -1;
    int CATEGORY;
    private QuestionsCallback mCallback;
    GenericQuestion genericQuestion;
    SharedPreferences pref;

    private QuestionWordCardBinding binding;

    ArrayList<Character> jumbledCharacters;

    String answer, answerPadCharacters;

    private boolean isUIVisibleToUser = false;
    private RelativeLayout cardContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = QuestionWordCardBinding.inflate(inflater, container, false);

        binding.qcardWordNext.setOnClickListener(v -> nextQuestion());
        binding.qcardWordPrevious.setOnClickListener(v -> previousQuestion());
        binding.wordCanvasPull.setOnClickListener(v -> canvas_pulldown());

        this.cardContent = (RelativeLayout) binding.getRoot().findViewById(R.id.question_card_content);
        this.mCallback = (QuestionsCallback) getActivity();
        Bundle args = getArguments();
        POSITION = args.getInt(Constants.BUNDLE_QUESTION_NUMBER);
        CATEGORY = args.getInt(Constants.BUNDLE_QUESTION_CATEGORY);
        pref = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;

        binding.textAreaScroller.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height / 4));

        genericQuestion = JSONUtils.getQuestionAt(getActivity(), CATEGORY, POSITION - 1);
        binding.qcardWordQuestion.setText(genericQuestion.question);

        answer = genericQuestion.answer;
        answerPadCharacters = genericQuestion.pad_characters;

        if (genericQuestion.question_number == 1) {
            binding.qcardWordPrevious.setVisibility(View.GONE);
        }

        if (genericQuestion.question_number == Constants.RIDDLE_COUNT && genericQuestion.category == Constants.GAME_TYPE_RIDDLE) {
            binding.qcardWordNext.setVisibility(View.GONE);
        }
        if (genericQuestion.question_number == Constants.SEQUENCE_COUNT && genericQuestion.category == Constants.GAME_TYPE_SEQUENCES) {
            binding.qcardWordNext.setVisibility(View.GONE);
        }
        if (genericQuestion.question_number == Constants.LOGIC_QUESTION && genericQuestion.category == Constants.GAME_TYPE_LOGIC) {
            binding.qcardWordNext.setVisibility(View.GONE);
        }

        setUpJumbledCharacters();
        setUpBlanksAndRows();

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
        // Check every time the fragment is refreshed
        lockQuestionIfRequired();
    }

    private void setUpJumbledCharacters() {
        jumbledCharacters = new ArrayList<>();

        long seed = System.nanoTime();

        for (int i = 0; i < answerPadCharacters.length(); i++) {
            jumbledCharacters.add(answerPadCharacters.charAt(i));
        }
        Collections.shuffle(jumbledCharacters, new Random(seed));

        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == ' ')
                jumbledCharacters.add(i, ' ');
            else
                jumbledCharacters.add(i, '-');
        }
    }

    public void nextQuestion() {
        if (isUIVisibleToUser) {
            ViewPager pager = (ViewPager) getActivity().findViewById(R.id.questions_activity_pager);
            pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            SoundManager.playSwipeSound(getActivity());
        }
    }

    public void previousQuestion() {
        if (isUIVisibleToUser) {
            ViewPager pager = (ViewPager) getActivity().findViewById(R.id.questions_activity_pager);
            pager.setCurrentItem(pager.getCurrentItem() - 1, true);
            SoundManager.playSwipeSound(getActivity());
        }
    }

    public void canvas_pulldown() {
        if (isUIVisibleToUser) {
            DrawingView.setUpCanvas(getContext(), QuestionsActivity.convertDip2Pixels(getContext(), binding.qcardWordQuestion.getHeight() + 80));
            SoundManager.playButtonClickSound(getActivity());
        }
    }

    public static QuestionWordFragment newInstance(Bundle args) {
        QuestionWordFragment fragment = new QuestionWordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void setUpBlanksAndRows() {
        if (binding == null) return;

        binding.qCardWordLlRow1Q.removeAllViews();
        binding.qCardWordLlRow2Q.removeAllViews();
        binding.qCardWordLlAnswerrowQ.removeAllViews();

        for (int i = 0; i < answer.length(); i++) {
            if (jumbledCharacters.get(i) == ' ') {
                final TextView emptyTextView = generateEmptyTextView();
                binding.qCardWordLlAnswerrowQ.addView(emptyTextView);
            } else {
                final TextView answerTV = generateBlanksTextView(i);
                final int m = i;
                answerTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isUIVisibleToUser) {
                            putThisCharacterBackToOptionsRow(m);
                            setUpBlanksAndRows();
                            SoundManager.playPadCharacterSound(getActivity());
                        }
                    }
                });
                binding.qCardWordLlAnswerrowQ.addView(answerTV);
            }
        }

        for (int i = 0; i < answerPadCharacters.length() / 2; i++) {
            final int m = i;
            final TextView answerTV = generateFilledTextView(i);

            answerTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isUIVisibleToUser) {
                        addThisCharacterToAnswerRow(answer.length() + m);
                        setUpBlanksAndRows();
                        SoundManager.playPadCharacterSound(getActivity());
                        isAnsweredCorrectly();
                    }
                }
            });

            binding.qCardWordLlRow1Q.addView(answerTV);
        }

        for (int i = answerPadCharacters.length() / 2; i < answerPadCharacters.length(); i++) {
            final TextView answerTV = generateFilledTextView(i);
            final int m = i;

            answerTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isUIVisibleToUser) {
                        addThisCharacterToAnswerRow(answer.length() + m);
                        setUpBlanksAndRows();
                        SoundManager.playPadCharacterSound(getActivity());
                        isAnsweredCorrectly();
                    }
                }
            });

            binding.qCardWordLlRow2Q.addView(answerTV);
        }
    }

    private void addThisCharacterToAnswerRow(int indexToExchange) {
        for (int i = 0; i < answer.length(); i++) {
            if (jumbledCharacters.get(i) == '-') {
                Collections.swap(jumbledCharacters, indexToExchange, i);
            }
        }
    }

    private void putThisCharacterBackToOptionsRow(int indexToExchange) {
        for (int i = answer.length(); i < answer.length() + answerPadCharacters.length(); i++) {
            if (jumbledCharacters.get(i) == '-') {
                Collections.swap(jumbledCharacters, indexToExchange, i);
            }
        }
    }

    public boolean isAnsweredCorrectly() {
        GenericAnswerDetails details = GenericAnswerDetails.getAnswerDetail(genericQuestion.question_number, CATEGORY);
        /*Check if entire row is completed. If it is, display animation if wrong*/
        if (isAnswerRowCompletelyFilled()) {
            if(pref.getInt(Constants.PREF_SHOW_AD,0)>=Constants.AD_DISPLAY_LIMIT)
                mCallback.showAd(false);
            else
                pref.edit().putInt(Constants.PREF_SHOW_AD,pref.getInt(Constants.PREF_SHOW_AD,0)+1).apply();
            boolean isAnsweredCorrectly = true;
            for (int i = 0; i < answer.length(); i++) {
                if (jumbledCharacters.get(i) != answer.charAt(i)) {
                    isAnsweredCorrectly = false;
                }
            }
            if (!isAnsweredCorrectly) {
                /*Display animation and return false*/
                final Animation animOvershoot = AnimationUtils.loadAnimation(getActivity(), R.anim.wobble);

                for (int i = 0; i < binding.qCardWordLlAnswerrowQ.getChildCount(); i++) {
                    View v = binding.qCardWordLlAnswerrowQ.getChildAt(i);
                    v.startAnimation(animOvershoot);
                }

                return false;
            }
        } else
            return false;

        // Coins and question should be unlocked when status is available. For correct status relevant coins and question have already
        // been unlocked. For incorrect and unavailable user should not be able to answer.
        if (details.status == Constants.AVAILABLE) {
            GenericAnswerDetails.updateStatus(POSITION, CATEGORY, Constants.CORRECT);
            Coins.correct_answer(getContext());

            ImageView correctIndicator = (ImageView) getActivity().findViewById(R.id.questions_activity_correct_indicator);
            correctIndicator.setImageResource(R.drawable.tick_green);

            TextView display_coins = (TextView) getActivity().findViewById(R.id.questions_activity_coin_text);
            display_coins.setText(pref.getLong(Constants.PREF_COINS, 0) + "");
            SoundManager.playCoinSound(getActivity());
            int next = mCallback.unlockNextQuestion(CATEGORY);
            mCallback.showCorrectAnswerFeedback(next);
            mCallback.refreshAdapter();
        } else {
            mCallback.showCorrectAnswerFeedback(-1);
        }
        //Toast.makeText(getActivity(), "Answered Correctly!", Toast.LENGTH_LONG).show();
        return true;
    }

    /*
    *@return true if answer row is completely filled, else false
     */
    private boolean isAnswerRowCompletelyFilled() {
        for (int i = 0; i < answer.length(); i++) {
            if (jumbledCharacters.get(i) == '-') {
                return false;
            }
        }
        return true;
    }

    private TextView generateBlanksTextView(int i) {

        int screen = getScreenWidth();
        int tvMargin = 2;
        int tvWidth = (screen / answer.length()) - (2 * tvMargin);
        int tvMaxWidth = (screen / (answerPadCharacters.length() / 2)) - (2 * tvMargin);

        tvWidth = tvWidth > tvMaxWidth ? tvMaxWidth : tvWidth;


        final TextView answerTV = new TextView(getActivity());

        answerTV.setText("" + jumbledCharacters.get(i));
        answerTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvWidth / 2);
        answerTV.setTextColor(Color.BLACK);
        answerTV.setBackgroundResource(R.drawable.circle_border);
        answerTV.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tvWidth, tvWidth);
        layoutParams.setMargins(tvMargin, tvMargin, tvMargin, tvMargin);
        answerTV.setLayoutParams(layoutParams);
        answerTV.setGravity(Gravity.CENTER);

        return answerTV;
    }

    private TextView generateFilledTextView(int i) {
        int screen = getScreenWidth();
        int tvMargin = 2;
        int tvWidth = (screen / (answerPadCharacters.length() / 2)) - (2 * tvMargin);

        final TextView answerTV = new TextView(getActivity());

        answerTV.setText("" + jumbledCharacters.get(answer.length() + i));
        answerTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvWidth / 2);
        answerTV.setTextColor(Color.WHITE);
        answerTV.setBackgroundResource(R.drawable.pad_character_background);
        answerTV.setLayoutParams(new ViewGroup.LayoutParams(tvWidth, tvWidth));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tvWidth, tvWidth);
        layoutParams.setMargins(tvMargin, tvMargin, tvMargin, tvMargin);
        answerTV.setLayoutParams(layoutParams);
        answerTV.setGravity(Gravity.CENTER);

        return answerTV;
    }

    private TextView generateEmptyTextView() {

        int screen = getScreenWidth();
        int tvMargin = 2;
        int tvWidth = (screen / answer.length()) - (2 * tvMargin);
        int tvMaxWidth = (screen / (answerPadCharacters.length() / 2)) - (2 * tvMargin);

        tvWidth = tvWidth > tvMaxWidth ? tvMaxWidth : tvWidth;

        TextView blankTV = new TextView(getActivity());
        blankTV.setText(" ");
        blankTV.setBackgroundResource(0);
        blankTV.setTextSize(tvWidth / 2);
        blankTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvWidth / 2);
        blankTV.setBackgroundResource(0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tvWidth, tvWidth);
        layoutParams.setMargins(tvMargin, tvMargin, tvMargin, tvMargin);
        blankTV.setLayoutParams(layoutParams);
        blankTV.setGravity(Gravity.CENTER);

        return blankTV;
    }

    private int getScreenWidth() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, r.getDisplayMetrics());

        return size.x - 4 * px;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isUIVisibleToUser = isVisibleToUser;
    }

    public void lockQuestionIfRequired() {
        if (binding == null) return;
        //Log.i("question ", answer);
        Log.i("text card ", "position " + POSITION + " category " + CATEGORY + " status " + GenericAnswerDetails.getStatus(POSITION, CATEGORY));
        switch (GenericAnswerDetails.getStatus(POSITION, CATEGORY)) {
            case Constants.UNAVAILABLE:
                Log.i("textcard", "unavailable");
                //mCallback.setIsQuestionLocked(true);
                //ImageView lock = (ImageView) findViewById(R.id.lock_full_image);
                //lock.setVisibility(View.VISIBLE);
                ImageView lock = new ImageView(getActivity());
                FrameLayout.LayoutParams layoutParams = new android.widget.FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT);
                lock.setLayoutParams(layoutParams);
                lock.setId(R.id.lockImageId);
                lock.setImageResource(R.drawable.lock_flat);
                lock.setBackgroundColor(getResources().getColor(R.color.white));
                lock.setScaleType(ImageView.ScaleType.CENTER);
                lock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isUIVisibleToUser) {
                            View characterDialog = getActivity().findViewById(R.id.questions_activity_character_dialog_unlock);
                            //expand the character dialog only if it is not previously visible
                            if (characterDialog.getVisibility() == View.GONE) {
                                mCallback.showCharacterUnlockDialog();
                                mCallback.setupCharacterUnlockDialog();
                            }
                            SoundManager.playButtonClickSound(getActivity());
                        }
                    }
                });
                cardContent.addView(lock, cardContent.getChildCount() - 2);
                binding.wordCanvasPull.setVisibility(View.GONE);
                break;
            case Constants.INCORRECT:
                //mCallback.setIsQuestionLocked(true);
                ImageView options_lock = new ImageView(getActivity());
                RelativeLayout.LayoutParams layoutParams1 = new android.widget.RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams1.addRule(RelativeLayout.BELOW, R.id.textAreaScroller);
                options_lock.setLayoutParams(layoutParams1);
                options_lock.setId(R.id.lockImageId);
                options_lock.setImageResource(R.drawable.lock_flat);
                options_lock.setBackgroundColor(getResources().getColor(R.color.white));
                options_lock.setScaleType(ImageView.ScaleType.CENTER);
                options_lock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isUIVisibleToUser) {
                            View characterDialog = getActivity().findViewById(R.id.questions_activity_character_dialog_unlock);
                            //expand the character dialog only if it is not previously visible
                            if (characterDialog.getVisibility() == View.GONE) {
                                mCallback.showCharacterUnlockDialog();
                                mCallback.setupCharacterUnlockDialog();
                            }
                            SoundManager.playButtonClickSound(getActivity());
                        }
                    }
                });
                cardContent.addView(options_lock, cardContent.getChildCount() - 2);
                binding.wordCanvasPull.setVisibility(View.GONE);
                break;
        }
    }
}
