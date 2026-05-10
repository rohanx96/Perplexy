package com.contextgenesis.perplexy.ui;

/**
 * Created by dhruv on 22/3/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.contextgenesis.perplexy.databinding.ActivityContributeBinding;
import com.contextgenesis.perplexy.utils.Coins;
import com.contextgenesis.perplexy.utils.SoundManager;

public class ContributeActivity extends Activity {

    private ActivityContributeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContributeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.contributeBack.setOnClickListener(v -> goBack());
        binding.contributeSubmit.setOnClickListener(v -> onClick_submit());
    }

    public void goBack() {
        SoundManager.playButtonClickSound(getApplicationContext());
        onBackPressed();
    }

    public void onClick_submit() {
        if (binding.contributeQuestion.getText().toString().length() != 0 && binding.contributeHint.getText().toString().length() != 0
                && binding.contributeCategory.getText().toString().length() != 0 && binding.contributeAnswer.getText().toString().length() != 0) {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"contextgenesis@gmail.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Perplexy Question Contribution");
            email.putExtra(Intent.EXTRA_TEXT, "Hi,\n    I would like to contribute a question for app Perplexy.\n\nQuestion: " + binding.contributeQuestion.getText().toString() +
                    "\nCategory: " + binding.contributeCategory.getText().toString() +
                    "\nHint: " + binding.contributeHint.getText().toString() +
                    "\nSolution: " + binding.contributeAnswer.getText().toString() +
                    "\nOptions: " + binding.contributeOptions.getText().toString());

            //need this to prompts email client only
            email.setType("message/rfc822");

            Toast.makeText(this, "You have earned 200 coins! :)", Toast.LENGTH_LONG).show();
            Coins.contribute_question(this);

            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        } else {
            if (binding.contributeQuestion.getText().toString().length() == 0) {
                binding.contributeQuestion.requestFocus();
                Toast.makeText(getApplicationContext(), "Question Field is required", Toast.LENGTH_LONG).show();
            } else if (binding.contributeCategory.getText().toString().length() == 0) {
                binding.contributeCategory.requestFocus();
                Toast.makeText(getApplicationContext(), "Category Field is required", Toast.LENGTH_LONG).show();
            } else if (binding.contributeAnswer.getText().toString().length() == 0) {
                binding.contributeAnswer.requestFocus();
                Toast.makeText(getApplicationContext(), "Solution Field is required", Toast.LENGTH_LONG).show();
            } else if (binding.contributeHint.getText().toString().length() == 0) {
                binding.contributeHint.requestFocus();
                Toast.makeText(getApplicationContext(), "Hint Field is required", Toast.LENGTH_LONG).show();
            }
        }
    }
}
