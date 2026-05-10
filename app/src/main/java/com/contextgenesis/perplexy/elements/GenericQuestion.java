package com.contextgenesis.perplexy.elements;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rish on 10/3/16.
 */

public class GenericQuestion {

    @SerializedName("question_number")
    public int question_number;

    @SerializedName("layout_type")
    public int layout_type;

    @SerializedName("category")
    public int category;

    @SerializedName("question_name")
    public String question_name;

    @SerializedName("question")
    public String question;

    @SerializedName("answer")
    public String answer;

    @SerializedName("hint")
    public String hint;

    @SerializedName("message")
    public String message;

    @SerializedName("explanation")
    public String explanation;

    @SerializedName("pad_characters")
    public String pad_characters;

    @SerializedName("options")
    public String options;

    public GenericQuestion() {
    }


    public GenericQuestion(int question_number, int layout_type, int category, String question_name, String question, String answer, String hint, String message, String explanation, String pad_characters, String options) {
        this.question_number = question_number;
        this.layout_type = layout_type;
        this.category = category;
        this.question_name = question_name;
        this.question = question;
        this.answer = answer;
        this.hint = hint;
        this.message = message;
        this.explanation = explanation;
        this.pad_characters = pad_characters;
        this.options = options;
    }

    @Override
    public String toString() {

        /*
        *  Must parse jsonarray to figure out all options. cant simply print JSONArray
         */

        String optionString = "";
        String option[] = options.split(";");

        for (int i = 0; i < option.length - 1; i++) {
            optionString += option[i] + ";";
        }
        optionString += option[option.length - 1];


        return "GenericQuestion{" +
                "questionNumber=" + question_number +
                ", layout_type=" + layout_type +
                ", category='" + category + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", hint='" + hint + '\'' +
                ", message='" + message + '\'' +
                ", explanation='" + explanation + '\'' +
                ", pad_characters='" + pad_characters + '\'' +
                ", options=" + optionString +
                '}';
    }
}