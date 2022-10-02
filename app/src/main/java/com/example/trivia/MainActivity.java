package com.example.trivia;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding  binding;
    private int currentQuestionIndex = 0;
    List<Question> questionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        // here after fetching the question QuestionText View is set
        questionList = new Repository().getQuestion(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                binding.questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                updateCounter(questionArrayList);
            }
        });

        binding.buttonNext.setOnClickListener(v -> {
            currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
            updateQuestion();
        });
        binding.buttonFalse.setOnClickListener(v -> {
            checkAnswer(true);
            updateQuestion();


        });
        binding.buttonTrue.setOnClickListener(v -> {
            checkAnswer(false);
            updateQuestion();

        });




        // the arraylist outside the repository class is showing Empty list which is no true
        // we used interface to sync fetching and populating the interface
        // we created an interface class named AnswerListAsyncResponse which passed as a parameter to check whether the parsing is done or not
//        List<Question> question = new Repository().getQuestion(new AnswerListAsyncResponse() {
//            @Override
//            public void processFinished(ArrayList<Question> questionArrayList) {
//                Log.d("main", "onCreate: " + questionArrayList);
//            }
//        });


    }

    private void checkAnswer(boolean userSelected) {
        // to check what useChosen true/false
        boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        int snackMessageId = 0;
        if(userSelected == answer)
        {
            snackMessageId = R.string.correct_answer;
            fadeAnimation();
//            shakeAnimation(1);
        }
        else
        {
            snackMessageId = R.string.wrong_answer;
            shakeAnimation(0);
        }
        Snackbar.make(binding.cardView,snackMessageId,Snackbar.LENGTH_SHORT).show();
    }

    private void updateCounter(ArrayList<Question> questionArrayList) {
        binding.textViewOutOf.setText(String.format("Question : %d/%d", currentQuestionIndex, questionArrayList.size()));
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        binding.questionTextView.setText(question);
        updateCounter((ArrayList<Question>)questionList);
    }

    private void fadeAnimation()
    {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setRepeatCount(1);

        // binding this animation to TextView
        binding.questionTextView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.BLACK);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void shakeAnimation(int flag)
    {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        binding.cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(flag == 0)
                {
                    binding.questionTextView.setTextColor(Color.RED);
                }
                else
                {
                    binding.questionTextView.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.BLACK);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}