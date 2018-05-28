package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.LogOutTask;
import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.PostQuizAnswersTask;

public class PostQuizAnswers extends Activity {

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
        quizname = quizname.substring(0, quizname.indexOf("."));
        new PostQuizAnswersTask(this).execute(sessionId, quizname);
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

    public String getCurrentSSID() {
        ConnectivityManager connManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String ssid= null;
        if (networkInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }

    public Map<String,String> openLocationsFile(){
        Map<String,String> locations = new HashMap<>();
        try {
            FileInputStream fis = getApplicationContext().openFileInput("museums.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            locations = (Map<String,String>) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return locations;
    }

    public void logOut2(View view) {
        new LogOutTask(this).execute(sessionId);
    }
}