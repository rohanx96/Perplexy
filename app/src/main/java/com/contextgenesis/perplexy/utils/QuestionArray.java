package com.contextgenesis.perplexy.utils;

import com.google.gson.annotations.SerializedName;
import com.contextgenesis.perplexy.elements.GenericQuestion;

import java.util.List;

/**
 * Created by rish on 5/4/16.
 */

public class QuestionArray {

    @SerializedName("questions")
    public List<GenericQuestion> questionsArray;

}
