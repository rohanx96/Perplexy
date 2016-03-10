package com.rohanx96.admobproto.elements;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.orm.util.NamingHelper;
import com.rohanx96.admobproto.utils.Constants;
import com.rohanx96.admobproto.utils.JSONUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rish on 10/3/16.
 */
public class GenericAnswerDetails extends SugarRecord {

    public int question_number;
    public int category, status; /*correct, incorrect, available, unavailable (int)*/
    public boolean hint_displayed, answer_displayed;
    public int number_incorrect;
    public boolean bookmarked;

    public GenericAnswerDetails() {

    }

    public GenericAnswerDetails(int question_number, int category, int status, boolean hint_displayed, boolean answer_displayed, int number_incorrect) {
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
                ", status='" + status + '\'' +
                ", hint_displayed=" + hint_displayed +
                ", answer_displayed=" + answer_displayed +
                ", number_incorrect=" + number_incorrect +
                '}';
    }

    public static void initializeDatabase(Context context) {
        /*
         *Initialize database with total number of answered questions with default value as that in the JSON question
         * question_id : from JSON File
         * status : UNAVAILABLE
         * hint : false
         * answer : false
         * number_incorrect : 0
         */

        ArrayList<GenericQuestion> allQuestions = new ArrayList<>();
        allQuestions.addAll(JSONUtils.getQuestionsFromJSONString(context, Constants.GAME_TYPE_RIDDLE));
        allQuestions.addAll(JSONUtils.getQuestionsFromJSONString(context, Constants.GAME_TYPE_SEQUENCES));
        /* allQuestions.addAll(JSONUtils.getQuestionsFromJSONString(context, Constants.GAME_TYPE_LOGIC));*/

        for (int i = 0; i < allQuestions.size(); i++) {
            int question_number = allQuestions.get(i).question_number;
            int category = allQuestions.get(i).category;
            GenericAnswerDetails genericAnswerDetails;

            // Initialise first three questions as available. TODO: unlock for each category
            if (i < 3)
                genericAnswerDetails = new GenericAnswerDetails(question_number, category, Constants.AVAILABLE, false, false, 0);
            else
                genericAnswerDetails = new GenericAnswerDetails(question_number, category, Constants.UNAVAILABLE, false, false, 0);
            genericAnswerDetails.save();
        }
    }

    public static ArrayList<GenericAnswerDetails> listAll(int category) {
        return (ArrayList<GenericAnswerDetails>) Select.from(GenericAnswerDetails.class)
                .where(Condition.prop("category").eq(category))
                .list();
    }

    public static void incrementNumberOfIncorrect(int question_number, int category) {
        GenericAnswerDetails genericAnswerDetail = Select.from(GenericAnswerDetails.class).where(Condition.prop(NamingHelper.toSQLNameDefault("category")).eq(category)).where(Condition.prop(NamingHelper.toSQLNameDefault("questionNumber")).eq(question_number)).first();
        genericAnswerDetail.number_incorrect++;
        genericAnswerDetail.save();
    }

    public static GenericAnswerDetails getAnswerDetail(int question_number, int category) {
        GenericAnswerDetails genericAnswerDetail = Select.from(GenericAnswerDetails.class)
                .where(Condition.prop(NamingHelper.toSQLNameDefault("category")).eq(category))
                .where(Condition.prop(NamingHelper.toSQLNameDefault("question_number")).eq(question_number))
                .first();
        return genericAnswerDetail;
    }

    public static int getStatus(int question_number, int category) {
        GenericAnswerDetails genericAnswerDetail = Select.from(GenericAnswerDetails.class)
                .where(Condition.prop(NamingHelper.toSQLNameDefault("category")).eq(category))
                .where(Condition.prop(NamingHelper.toSQLNameDefault("question_number")).eq(question_number))
                .first();
        return genericAnswerDetail.status;
    }

    public static void updateStatus(int question_number, int category, int status) {
        GenericAnswerDetails genericAnswerDetail = Select.from(GenericAnswerDetails.class)
                .where(Condition.prop(NamingHelper.toSQLNameDefault("category")).eq(category))
                .where(Condition.prop(NamingHelper.toSQLNameDefault("question_number")).eq(question_number))
                .first();
        genericAnswerDetail.status = status;
        genericAnswerDetail.save();
    }

    /*
    *Not sure if this is correct. Check logcat to see the SQL command that is returned.
     */
    public static GenericAnswerDetails getLastUnlockedQuestion(int category) {
        ArrayList<GenericAnswerDetails> answerDetails = (ArrayList<GenericAnswerDetails>) Select.from(GenericAnswerDetails.class)
                .where(Condition.prop(NamingHelper.toSQLNameDefault("category"))
                        .eq(category))
                .where(Condition.prop(NamingHelper.toSQLNameDefault("status")).eq(Constants.CORRECT))
                .whereOr(Condition.prop(NamingHelper.toSQLNameDefault("status")).eq(Constants.AVAILABLE))
                .list();
        return answerDetails.get(answerDetails.size() - 1);

        /*
        *If you cant figure out how to do this, just create and arraylist using this -
         *Select.from(GenericAnswerDetails.class).list()
          * and then traverse manually to find whichever condition we want.
         */
    }

    public static void printAll() {
        List<GenericAnswerDetails> genericAnswerDetails = Select.from(GenericAnswerDetails.class).list();
        for (GenericAnswerDetails g : genericAnswerDetails) {
            Log.d("PRINTDB", g.toString());
        }
    }
}