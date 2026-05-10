package com.contextgenesis.perplexy.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.elements.GenericAnswerDetails;
import com.contextgenesis.perplexy.ui.NumberLineActivity;
import com.contextgenesis.perplexy.ui.QuestionsActivity;
import com.contextgenesis.perplexy.utils.Constants;

import java.util.ArrayList;

/**
 * Created by bhutanidhruv16 on 07-Mar-16.
 */

public class NumberLineAdapter extends BaseAdapter {

    NumberLineActivity context;
    ArrayList<GenericAnswerDetails> answerDetails;
    LayoutInflater inflater;

    public NumberLineAdapter(NumberLineActivity context, ArrayList<GenericAnswerDetails> answerDetails) {
        this.context = context;
        this.answerDetails = answerDetails;
        /*
        * Add for padding of the arrows on the number line
        */
        this.answerDetails.add(0, new GenericAnswerDetails(-1, Constants.CORRECT, Constants.UNAVAILABLE, false, false, 0));
        this.answerDetails.add(answerDetails.size(), new GenericAnswerDetails(-1, Constants.CORRECT, Constants.UNAVAILABLE, false, false, 0));
    }

    @Override
    public int getCount() {
        Log.i("number line", " " + answerDetails.size());
        return answerDetails.size();
    }

    @Override
    public GenericAnswerDetails getItem(int position) {
        return answerDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View vi = convertView;
        int type = getItemViewType(position);
        ViewHolder holder = new ViewHolder();
        if (type == 0) {
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.element_number_line_left, null);
            holder.tv = (Button) vi.findViewById(R.id.element_number_line_left_textview);
            holder.bookmark_star = (ImageView) vi.findViewById(R.id.bookmark_left_textview);
        } else if (type == 1) {
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.element_number_line_right, null);
            holder.tv = (Button) vi.findViewById(R.id.element_number_line_right_textview);
            holder.bookmark_star = (ImageView) vi.findViewById(R.id.bookmark_right_textview);
        } else if (type == -1) {
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.element_number_line_start, null);
            holder.tv = (Button) vi.findViewById(R.id.element_number_line_start_uparrow);
        } else {
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.element_number_line_end, null);
            holder.tv = (Button) vi.findViewById(R.id.element_number_line_end_downarrow);
        }

        vi.setTag(holder);

        /*
        * Process answer details here and set backgrounds and lock image accordingly
        */

        // Text and click listeners are not set for number line arrows
        if (position != 0 && position != answerDetails.size() - 1) {
            holder.tv.setText(position + "");
            // Set background drawable for question number based on status
            setBackgroundDrawable(position, holder);
            setOnClickListeners(position, holder);
        }

        return vi;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return -1;
        else if (position == answerDetails.size() - 1)
            return -2;

        return position % 2;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    public class ViewHolder {
        Button tv;
        ImageView bookmark_star;
    }

    public void setBackgroundDrawable(int position, ViewHolder holder) {
        // Set background drawable for question number based on status
        switch (answerDetails.get(position).status) {
            case Constants.CORRECT:
                if (answerDetails.get(position).bookmarked) {
                    holder.bookmark_star.setVisibility(View.VISIBLE);
                } else {
                    holder.bookmark_star.setVisibility(View.GONE);
                }
                holder.tv.setBackgroundResource(R.drawable.circle_question_number_correcct);
                break;
            case Constants.INCORRECT:
                if (answerDetails.get(position).bookmarked) {
                    holder.bookmark_star.setVisibility(View.VISIBLE);
                } else {
                    holder.bookmark_star.setVisibility(View.GONE);
                }
                holder.tv.setBackgroundResource(R.drawable.circle_question_number_incorrect);
                break;
            case Constants.AVAILABLE:
                if (answerDetails.get(position).bookmarked) {
                    holder.bookmark_star.setVisibility(View.VISIBLE);
                } else {
                    holder.bookmark_star.setVisibility(View.GONE);
                }
                holder.tv.setBackgroundResource(R.drawable.circle_question_number_available);
                break;
            case Constants.UNAVAILABLE:
                holder.tv.setBackgroundResource(R.drawable.circle_question_number_unavailable);
                break;

        }
    }

    public void setOnClickListeners(final int position, final ViewHolder holder) {
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuestionsActivity.class);
                intent.putExtra(Constants.BUNDLE_QUESTION_NUMBER, getItem(position).question_number);
                intent.putExtra(Constants.BUNDLE_QUESTION_CATEGORY, getItem(position).category);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // TODO: Bug here
                if (Build.VERSION.SDK_INT >= 21)
                    context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(context, holder.tv, "character").toBundle());
                else
                    context.startActivity(intent);
                context.setAnimationRunning(false); // Stop the background color change animation on leaving activity
            }
        });

    }

    public void setAnswerDetails(ArrayList<GenericAnswerDetails> answerDetails) {
        this.answerDetails = answerDetails;
        /*
        * Add for padding of the arrows on the number line
        */
        this.answerDetails.add(0, new GenericAnswerDetails(-1, Constants.CORRECT, Constants.UNAVAILABLE, false, false, 0));
        this.answerDetails.add(answerDetails.size(), new GenericAnswerDetails(-1, Constants.CORRECT, Constants.UNAVAILABLE, false, false, 0));
    }
}