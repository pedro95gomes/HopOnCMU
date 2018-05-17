package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.*;

public class PostQuizAnswers extends AppCompatActivity {

    //Show all questions for user to answer and submit them to server
    List<String[]> questions;
    List<String> answers;
    Button first;
    Button second;
    Button third;
    Button fourth;
    Button submit;
    TextView question;
    TextView qNum;
    TextView texttime;
    String quizname;
    String sessionId;
    int currentQuestion;
    QuizChronometer chronometer;
    int timeTaken = 0;

    @Override
    protected void onPause() {
        super.onPause();
        if(chronometer!=null){
            chronometer.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(chronometer!=null) {
            chronometer.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_quiz_questions);

        first = (Button) findViewById(R.id.resposta1);
        second = (Button) findViewById(R.id.resposta2);
        third = (Button) findViewById(R.id.resposta3);
        fourth = (Button) findViewById(R.id.resposta4);
        submit = (Button) findViewById(R.id.buttonsubmit);
        question = (TextView) findViewById(R.id.pergunta);
        qNum = (TextView) findViewById(R.id.questionnumber);
        texttime = (TextView) findViewById(R.id.texttime);

        chronometer = new QuizChronometer(this);
        answers = new ArrayList<String>();
        currentQuestion = 0;

        if(getIntent() != null && getIntent().getExtras() != null){
            questions = new ArrayList<String[]>();

            Intent intent = getIntent();
            int quiz_count = intent.getExtras().getInt("q_count");
            sessionId = intent.getExtras().getString("ssid");
            quizname = intent.getExtras().getString("quizname");
            for(int i = 1; i <= quiz_count; i++){
                questions.add(intent.getExtras().getStringArray("question" + i));
            }
        }
        setClickListeners();
        updateQuestions();
        chronometer.start();
    }

    public List<String> getAnswers(){
        return this.answers;
    }

    public int getTimeTaken(){
        return timeTaken;
    }

    private void setClickListeners() {
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answers.add(first.getText().toString());
                updateQuestions();
            }
        });
        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answers.add(second.getText().toString());
                updateQuestions();
            }
        });
        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answers.add(third.getText().toString());
                updateQuestions();
            }
        });
        fourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answers.add(fourth.getText().toString());
                updateQuestions();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAnswers();
            }
        });
    }

    private void submitAnswers(){
        new PostQuizAnswersTask(this).execute(sessionId,quizname);
    }

    private void updateQuestions() {
        if(currentQuestion+1 >= questions.size()){
            question.setVisibility(View.INVISIBLE);
            qNum.setVisibility(View.INVISIBLE);
            first.setVisibility(View.INVISIBLE);
            second.setVisibility(View.INVISIBLE);
            third.setVisibility(View.INVISIBLE);
            fourth.setVisibility(View.INVISIBLE);
            submit.setVisibility(View.VISIBLE);
            chronometer.stop();
            timeTaken = chronometer.getMsElapsed();
            Log.d("TimeTaken", String.valueOf(timeTaken));
            texttime.setText("Time taken (milliseconds): "+String.valueOf(timeTaken));
            texttime.setVisibility(View.VISIBLE);
        }
        else{
            qNum.setText(Integer.toString(currentQuestion+1));
            question.setText(questions.get(currentQuestion)[0]);
            first.setText(questions.get(currentQuestion)[1]);
            second.setText(questions.get(currentQuestion)[2]);
            third.setText(questions.get(currentQuestion)[3]);
            fourth.setText(questions.get(currentQuestion)[4]);
            currentQuestion++;
        }
    }

    public void onDestroy() {
        new LogOutTask(this).execute(sessionId);
        super.onDestroy();
    }
}