package com.contextgenesis.perplexy.ui;

/**
 * Created by dhruv on 22/3/17.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.contextgenesis.perplexy.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContributeActivity extends Activity {


    @Bind(R.id.contribute_question)
    EditText question;

    @Bind(R.id.contribute_hint)
    EditText hint;

    @Bind(R.id.contribute_answer)
    EditText solution;

    @Bind(R.id.contribute_category)
    EditText category;

    @Bind(R.id.contribute_options)
    EditText options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.contribute_submit)
    public void onClick_submit() {
        if (question.getText().toString().length() != 0 && hint.getText().toString().length() != 0
                && category.getText().toString().length() != 0 && solution.getText().toString().length() != 0) {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"contextgenesis@gmail.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Perplexy Question Contribution");
            email.putExtra(Intent.EXTRA_TEXT, "Hi,\n Question: " + question.getText().toString() +
                    "\nCategory: " + category.getText().toString() +
                    "\nHint: " + hint.getText().toString() +
                    "\nSolution: " + solution.getText().toString() +
                    "\nOptions: " + options.getText().toString());

            //need this to prompts email client only
            email.setType("message/rfc822");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }
        else{
            if(question.getText().toString().length() == 0)
                Toast.makeText(getApplicationContext(), "Question Field is required", Toast.LENGTH_LONG).show();
            else if(category.getText().toString().length() == 0)
                Toast.makeText(getApplicationContext(), "Category Field is required", Toast.LENGTH_LONG).show();
            else if(hint.getText().toString().length() == 0)
                Toast.makeText(getApplicationContext(), "Hint Field is required", Toast.LENGTH_LONG).show();
            else if(solution.getText().toString().length() == 0)
                Toast.makeText(getApplicationContext(), "Solution Field is required", Toast.LENGTH_LONG).show();
        }
    }
}