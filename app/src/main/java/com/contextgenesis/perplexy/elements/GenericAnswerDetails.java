package com.contextgenesis.perplexy.elements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.contextgenesis.perplexy.ui.HelpActivity;
import com.contextgenesis.perplexy.ui.LoadingActivity;
import com.contextgenesis.perplexy.ui.MainActivity;
import com.contextgenesis.perplexy.utils.Constants;
import com.contextgenesis.perplexy.utils.JSONUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rish on 10/3/16.
 */

@Entity
public class GenericAnswerDetails {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public int question_number;
    public int category, status;                                /*correct, incorrect, available, unavailable (int)*/
    public boolean hint_displayed, answer_displayed;
    public int number_incorrect;
    public boolean bookmarked;

    public GenericAnswerDetails() {
    }

    public GenericAnswerDetails(int question_number, int category, int status,
                                boolean hint_displayed, boolean answer_displayed,
                                int number_incorrect) {
        this.question_number = question_number;
        this.category = category;
        this.status = status;
        this.hint_displayed = hint_displayed;
        this.answer_displayed = answer_displayed;
        this.number_incorrect = number_incorrect;
    }

    @Override
    public String toString() {
        return "GenericAnswerDetails{" +
                "question_number=" + question_number +
                ", status=" + status +
                ", hint_displayed=" + hint_displayed +
                ", answer_displayed=" + answer_displayed +
                ", number_incorrect=" + number_incorrect +
                '}';
    }

    // ── Public API (same signatures as before) ────────────────────────────────

    public static void initializeDatabase(Activity activity) {
        new LoadDatabaseInBackgroundThread(activity).execute();
    }

    public static ArrayList<GenericAnswerDetails> listAll(int category) {
        return new ArrayList<>(AppDatabase.db.answerDetailsDao().getByCategory(category));
    }

    public static void incrementNumberOfIncorrect(int question_number, int category) {
        GenericAnswerDetailsDao dao = AppDatabase.db.answerDetailsDao();
        GenericAnswerDetails d = dao.getByQuestionAndCategory(question_number, category);
        if (d != null) {
            d.number_incorrect++;
            dao.update(d);
        }
    }

    public static GenericAnswerDetails getAnswerDetail(int question_number, int category) {
        return AppDatabase.db.answerDetailsDao().getByQuestionAndCategory(question_number, category);
    }

    public static int getStatus(int question_number, int category) {
        GenericAnswerDetails d = AppDatabase.db.answerDetailsDao()
                .getByQuestionAndCategory(question_number, category);
        return d != null ? d.status : Constants.UNAVAILABLE;
    }

    public static void updateStatus(int question_number, int category, int status) {
        GenericAnswerDetailsDao dao = AppDatabase.db.answerDetailsDao();
        GenericAnswerDetails d = dao.getByQuestionAndCategory(question_number, category);
        if (d != null) {
            d.status = status;
            if (status == Constants.CORRECT) d.answer_displayed = true;
            dao.update(d);
        }
    }

    public static GenericAnswerDetails getLastUnlockedQuestion(int category) {
        List<GenericAnswerDetails> list = AppDatabase.db.answerDetailsDao()
                .getByStatusOrStatus(category, Constants.CORRECT, Constants.AVAILABLE);
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    public static GenericAnswerDetails getFirstLocked(int category) {
        return AppDatabase.db.answerDetailsDao()
                .getFirstByStatusAndCategory(category, Constants.UNAVAILABLE);
    }

    /**
     * This unlocks the next question to be unlocked for a given category
     */
    public static int unlockNextQuestion(int category) {
        GenericAnswerDetails nextQuestion = getFirstLocked(category);
        if (nextQuestion != null && nextQuestion.status == Constants.UNAVAILABLE) {
            nextQuestion.status = Constants.AVAILABLE;
            AppDatabase.db.answerDetailsDao().update(nextQuestion);
            return nextQuestion.question_number;
        }
        return -2;
    }

    public static void printAll() {
        List<GenericAnswerDetails> list = AppDatabase.db.answerDetailsDao().getAll();
        for (GenericAnswerDetails g : list) {
            Log.d("PRINTDB", g.toString());
        }
    }

    // ── Background initialization ─────────────────────────────────────────────

    private static class LoadDatabaseInBackgroundThread extends AsyncTask<Void, Void, Void> {

        Activity activity;

        public LoadDatabaseInBackgroundThread(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.finish();
            activity.startActivity(new Intent(activity, LoadingActivity.class));
        }

        @Override
        protected Void doInBackground(Void... params) {
            GenericAnswerDetailsDao dao = AppDatabase.db.answerDetailsDao();
            dao.deleteAll();

            insertForCategory(dao, activity, Constants.GAME_TYPE_LOGIC);
            insertForCategory(dao, activity, Constants.GAME_TYPE_RIDDLE);
            insertForCategory(dao, activity, Constants.GAME_TYPE_SEQUENCES);
            return null;
        }

        private void insertForCategory(GenericAnswerDetailsDao dao, Context ctx, int gameType) {
            ArrayList<GenericQuestion> questions = JSONUtils.getQuestionsFromJSONString(ctx, gameType);
            List<GenericAnswerDetails> toInsert = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                int qNum = questions.get(i).question_number;
                int cat  = questions.get(i).category;
                int status = (i < 3) ? Constants.AVAILABLE : Constants.UNAVAILABLE;
                toInsert.add(new GenericAnswerDetails(qNum, cat, status, false, false, 0));
            }
            dao.insertAll(toInsert);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (LoadingActivity.thisActivity != null)
                LoadingActivity.thisActivity.finish();
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.startActivity(new Intent(activity, HelpActivity.class));
        }
    }
}
