package com.contextgenesis.perplexy.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.callbacks.QuestionsCallback;
import com.contextgenesis.perplexy.databinding.QuestionMcqCardBinding;
import com.contextgenesis.perplexy.elements.GenericAnswerDetails;
import com.contextgenesis.perplexy.elements.GenericQuestion;
import com.contextgenesis.perplexy.ui.QuestionsActivity;
import com.contextgenesis.perplexy.utils.Coins;
import com.contextgenesis.perplexy.utils.Constants;
import com.contextgenesis.perplexy.utils.DrawingView;
import com.contextgenesis.perplexy.utils.JSONUtils;
import com.contextgenesis.perplexy.utils.SoundManager;

/**
 * Created by rose on 7/3/16.
 */
public class QuestionMCQFragment extends Fragment {

    int POSITION = -1;
    int CATEGORY;
    private QuestionsCallback mCallback;
    GenericQuestion genericQuestion;
    SharedPreferences pref;
    ViewGroup container;

    private QuestionMcqCardBinding binding;

    private boolean isUIVisibleToUser = false;
    private RelativeLayout cardContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        binding = QuestionMcqCardBinding.inflate(inflater, container, false);

        binding.canvasPull.setOnClickListener(v -> canvasClick());
        binding.qcardMcqOption1.setOnClickListener(v -> onClickOption1(v));
        binding.qcardMcqOption2.setOnClickListener(v -> onClickOption2(v));
        binding.qcardMcqOption3.setOnClickListener(v -> onClickOption3(v));
        binding.qcardMcqOption4.setOnClickListener(v -> onClickOption4(v));
        binding.qcardMcqNext.setOnClickListener(v -> nextQuestion());
        binding.qcardMcqPrevious.setOnClickListener(v -> previousQuestion());

        this.cardContent = (RelativeLayout) binding.getRoot().findViewById(R.id.question_card_content);
        //setCardContent(cardContent);

        this.mCallback = (QuestionsCallback) getActivity();
        Bundle args = getArguments();
        POSITION = args.getInt(Constants.BUNDLE_QUESTION_NUMBER);
        CATEGORY = args.getInt(Constants.BUNDLE_QUESTION_CATEGORY);
        genericQuestion = JSONUtils.getQuestionAt(getActivity(), CATEGORY, POSITION - 1);
        pref = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;

        binding.textAreaScroller.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height/4));

        binding.qcardMcqQuestion.setText(genericQuestion.question);

        if (genericQuestion.question_number == 1) {
            binding.qcardMcqPrevious.setVisibility(View.GONE);
        }

        if (genericQuestion.question_number == Constants.RIDDLE_COUNT && genericQuestion.category == Constants.GAME_TYPE_RIDDLE) {
            binding.qcardMcqNext.setVisibility(View.GONE);
        }
        if (genericQuestion.question_number == Constants.SEQUENCE_COUNT && genericQuestion.category == Constants.GAME_TYPE_SEQUENCES) {
            binding.qcardMcqNext.setVisibility(View.GONE);
        }
        if (genericQuestion.question_number == Constants.LOGIC_QUESTION && genericQuestion.category == Constants.GAME_TYPE_LOGIC) {
            binding.qcardMcqNext.setVisibility(View.GONE);
        }

        try {
            String option[] = genericQuestion.options.split(";");

            switch (option.length) {
                case 2:
                    binding.qcardMcqOption1.setText(option[0]);
                    binding.qcardMcqOption2.setText(option[1]);
                    binding.qcardMcqOption3.setVisibility(View.GONE);
                    binding.qcardMcqOption4.setVisibility(View.GONE);
                    break;
                case 3:
                    binding.qcardMcqOption1.setText(option[0]);
                    binding.qcardMcqOption2.setText(option[1]);
                    binding.qcardMcqOption3.setText(option[2]);
                    binding.qcardMcqOption4.setVisibility(View.GONE);
                    break;
                case 4:
                    binding.qcardMcqOption1.setText(option[0]);
                    binding.qcardMcqOption2.setText(option[1]);
                    binding.qcardMcqOption3.setText(option[2]);
                    binding.qcardMcqOption4.setText(option[3]);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void canvasClick() {
        if (isUIVisibleToUser) {
            DrawingView.setUpCanvas(getContext(), QuestionsActivity.convertDip2Pixels(getContext(),70) + binding.qcardMcqQuestion.getHeight());
            SoundManager.playButtonClickSound(getActivity());
        }
    }

    public void onClickOption1(View view) {
        if (isUIVisibleToUser) {
            optionConfirmation(1);
            SoundManager.playPadCharacterSound(getActivity());
        }
    }

    public void onClickOption2(View view) {
        if (isUIVisibleToUser) {
            optionConfirmation(2);
            SoundManager.playPadCharacterSound(getActivity());
        }
    }

    public void onClickOption3(View view) {
        if (isUIVisibleToUser) {
            optionConfirmation(3);
            SoundManager.playPadCharacterSound(getActivity());
        }
    }

    public void onClickOption4(View view) {
        if (isUIVisibleToUser) {
            optionConfirmation(4);
            SoundManager.playPadCharacterSound(getActivity());
        }
    }

    int lastclick = 0;
    View view2;

    void optionConfirmation(final int n) {
        LayoutInflater layoutInflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view2 = layoutInflater.inflate(R.layout.confirm_option, container, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);
        view2.setLayoutParams(params);

        switch (lastclick) {
            case 1:
                binding.qcardMcqOptionsLl.removeViewAt(1);
                binding.qcardMcqOption1.setVisibility(View.VISIBLE);
                break;
            case 2:
                binding.qcardMcqOptionsLl.removeViewAt(2);
                binding.qcardMcqOption2.setVisibility(View.VISIBLE);
                break;
            case 3:
                binding.qcardMcqOptionsLl.removeViewAt(3);
                binding.qcardMcqOption3.setVisibility(View.VISIBLE);
                break;
            case 4:
                binding.qcardMcqOptionsLl.removeViewAt(4);
                binding.qcardMcqOption4.setVisibility(View.VISIBLE);
                break;
        }

        switch (n) {
            case 1:
                lastclick = 1;
                binding.qcardMcqOptionsLl.addView(view2, 1);
                binding.qcardMcqOption1.setVisibility(View.GONE);
                break;
            case 2:
                lastclick = 2;
                binding.qcardMcqOptionsLl.addView(view2, 2);
                binding.qcardMcqOption2.setVisibility(View.GONE);
                break;
            case 3:
                lastclick = 3;
                binding.qcardMcqOptionsLl.addView(view2, 3);
                binding.qcardMcqOption3.setVisibility(View.GONE);
                break;
            case 4:
                lastclick = 4;
                binding.qcardMcqOptionsLl.addView(view2, 4);
                binding.qcardMcqOption4.setVisibility(View.GONE);
                break;
        }

        TextView yes = (TextView) view2.findViewById(R.id.yes_answer);
        TextView no = (TextView) view2.findViewById(R.id.no_answer);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (n) {
                    case 1:
                        view2.setVisibility(View.GONE);
                        binding.qcardMcqOption1.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        view2.setVisibility(View.GONE);
                        binding.qcardMcqOption2.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        view2.setVisibility(View.GONE);
                        binding.qcardMcqOption3.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        view2.setVisibility(View.GONE);
                        binding.qcardMcqOption4.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (n) {
                    case 1:
                        view2.setVisibility(View.GONE);
                        binding.qcardMcqOption1.setVisibility(View.VISIBLE);
                        isRight(1 + "");
                        break;
                    case 2:
                        view2.setVisibility(View.GONE);
                        binding.qcardMcqOption2.setVisibility(View.VISIBLE);
                        isRight(2 + "");
                        break;
                    case 3:
                        view2.setVisibility(View.GONE);
                        binding.qcardMcqOption3.setVisibility(View.VISIBLE);
                        isRight(3 + "");
                        break;
                    case 4:
                        view2.setVisibility(View.GONE);
                        binding.qcardMcqOption4.setVisibility(View.VISIBLE);
                        isRight(4 + "");
                        break;
                }
            }
        });
    }

    void isRight(String check) {
        if (pref.getInt(Constants.PREF_SHOW_AD, 0) >= Constants.AD_DISPLAY_LIMIT)
            mCallback.showAd(false);
        else
            pref.edit().putInt(Constants.PREF_SHOW_AD, pref.getInt(Constants.PREF_SHOW_AD, 0) + 1).apply();
        if (genericQuestion.answer.equals(check)) {
            GenericAnswerDetails details = GenericAnswerDetails.getAnswerDetail(genericQuestion.question_number, CATEGORY);
            // Coins and question should be unlocked when status is available. For correct status relevant coins and question have already
            // been unlocked. For incorrect and unavailable user should not be able to answer.
            if (details.status == Constants.AVAILABLE) {
                Coins.correct_answer(getContext());
                details.status = Constants.CORRECT;
                details.answer_displayed = true;
                details.save();

                TextView display_coins = (TextView) getActivity().findViewById(R.id.questions_activity_coin_text);
                ImageView correctIndicator = (ImageView) getActivity().findViewById(R.id.questions_activity_correct_indicator);
                correctIndicator.setImageResource(R.drawable.tick_green);

                display_coins.setText(pref.getLong(Constants.PREF_COINS, 0) + "");
                SoundManager.playCoinSound(getActivity());
                int next = mCallback.unlockNextQuestion(CATEGORY);
                mCallback.showCorrectAnswerFeedback(next);
                mCallback.refreshAdapter();
            } else mCallback.showCorrectAnswerFeedback(-1);
            //Toast.makeText(getActivity(), "Clicked option " + check + " CORRECT", Toast.LENGTH_SHORT).show();

        } else {
            GenericAnswerDetails details = GenericAnswerDetails.getAnswerDetail(genericQuestion.question_number, CATEGORY);
            if (details.status == Constants.AVAILABLE) {
                Coins.wrong_answer(getContext());
                details.status = Constants.INCORRECT;
                details.save();

                ImageView correctIndicator = (ImageView) getActivity().findViewById(R.id.questions_activity_correct_indicator);
                correctIndicator.setImageResource(R.drawable.cross);

                TextView display_coins = (TextView) getActivity().findViewById(R.id.questions_activity_coin_text);
                display_coins.setText(pref.getLong(Constants.PREF_COINS, 0) + "");
                // Sets status of question locked in questionsActivity. Used to change the layout of character
                mCallback.setIsQuestionLocked(true);
                lockQuestionIfRequired();
            }
            mCallback.showIncorrectAnswerFeedback();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check every time the fragment is refreshed
        lockQuestionIfRequired();
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

    public static QuestionMCQFragment newInstance(Bundle args) {

        QuestionMCQFragment fragment = new QuestionMCQFragment();
        fragment.setArguments(args);
        return fragment;
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
                cardContent.addView(lock, cardContent.getChildCount() - 1);
                break;
            case Constants.INCORRECT:
                //mCallback.setIsQuestionLocked(true);
                ImageView options_lock = new ImageView(getActivity());
                RelativeLayout.LayoutParams layoutParams1 = new android.widget.RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams1.addRule(RelativeLayout.BELOW, R.id.textAreaScroller);
                options_lock.setLayoutParams(layoutParams1);
                options_lock.setId(R.id.lockImageId + POSITION);
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
                cardContent.addView(options_lock, cardContent.getChildCount() - 1);
                break;
        }
    }
}
