package com.example.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.model.Question;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    ArrayList<Question> questionArrayList = new ArrayList<>();
    String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    public List<Question> getQuestion(final AnswerListAsyncResponse callBack)
    {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    for(int i = 0; i < response.length(); i++)
                    {
                        // since the api we are parsing is Array of Array(Questions)
                        try {

                            Question question = new Question(response.getJSONArray(i).get(0).toString(),response.getJSONArray(i).getBoolean(1));

                            // add questions to arraylist/list then return arraylist
                            questionArrayList.add(question);
                            Log.d("hello", "getQuestion: " + questionArrayList); // at this point we have question in the form of objects

                            // DOT--getJsonArray(INDEX) will let you get the first set of Array
                            // then to look inside we need DOT--get(INDEX)
//                            Log.d("Repository", "Question --> " + response.getJSONArray(i).get(0));
//                            Log.d("Repository", "Answer --> " + response.getJSONArray(i).getBoolean(1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if(callBack != null)
                        callBack.processFinished(questionArrayList);


                }, error -> {
            Log.d("Main", "can't Find API");
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

        // returning the array list
        return questionArrayList;
    }
}
