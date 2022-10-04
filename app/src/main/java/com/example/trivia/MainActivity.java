package com.example.trivia;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding  binding;
    private int currentQuestionIndex = 0;
    List<Question> questionList;
    // to Record the Score
//    private int score = 0;
    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Instentiating Object
        score = new Score();
        prefs = new Prefs(MainActivity.this);

        // Using Shared Prefrences to continue from where left
//        Log.d("main", "Highest Score " + prefs.getHighestScore());

        // to keep the position of the question where we left instead of setting it to 0
        currentQuestionIndex = prefs.getState();


        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        // here after fetching the question QuestionText View is set
        binding.scoreText.setText("Score : "+ String.valueOf(score.getScore()));

        // setting highest Score
        binding.highestScoreText.setText(String.format("highest Score %s", String.valueOf(prefs.getHighestScore())));
        questionList = new Repository().getQuestion(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                binding.questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                updateCounter(questionArrayList);
            }
        });

        binding.buttonNext.setOnClickListener(v -> {
            getNextQuestion();
//            prefs.saveHighestScore(scoreCounter);  we overried onPause method and then save the Highest Score
//            Log.d("highS", "Highest Score " + prefs.getHighestScore());
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

    private void getNextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
    }

    private void checkAnswer(boolean userSelected) {
        // to check what useChosen true/false
        boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        int snackMessageId = 0;
        if(userSelected == answer)
        {
            snackMessageId = R.string.correct_answer;
            fadeAnimation();
            // score will incremented for every right answer
//            score += 10;
            addScorePoints();
//            shakeAnimation(1);
        }
        else
        {
            snackMessageId = R.string.wrong_answer;
            shakeAnimation(0);
            // score will decremented for every wrong answer
            deductScorePoints();
//            if(score >= 0)
//            {
//                score -= 10;
//            }
        }
        Snackbar.make(binding.cardView,snackMessageId,Snackbar.LENGTH_SHORT).show();
        // score is working fien this can be addes to the textView;
        Log.d("score", "checkAnswer: " + score);
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
                getNextQuestion();

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
                getNextQuestion();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void deductScorePoints()
    {

        if(scoreCounter > 0)
        {   scoreCounter -= 100;
            score.setScore(scoreCounter);
        }
        else
        {
            score.setScore(0);
        }
        Log.d("ScoreCounter", "deductScorePoints: " + score.getScore());
        binding.scoreText.setText(String.valueOf(score.getScore()));
    }

    private void addScorePoints()
    {
        scoreCounter += 100;
        // insyead directly updating score we can use getters and setters
        score.setScore(scoreCounter);
        Log.d("ScoreCounter", "addScorePoints: " + score.getScore());
        // since the data we get from the score.getScore() is INTEGER type so need to Type Cast to String
        binding.scoreText.setText(String.valueOf(score.getScore()));
    }


    @Override
    protected void onPause() {
        // override onPause method to store Preferences while the app is in Pause State
        // to Store Highest Score
        prefs.saveHighestScore(score.getScore());

        prefs.setState(currentQuestionIndex);

        Log.d("pause", "onPause: Saving Score " + prefs.getHighestScore());
        super.onPause();
    }
}