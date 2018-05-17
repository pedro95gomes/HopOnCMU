package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.LogOutTask;

public class MainMenu extends AppCompatActivity {

    Button listLocations;
    Button downloadQuestions;
    Button postQuiz;
    Button readResults;
    Button answerQuiz;
    Button shareQuizzes;
    Button logOut;
    String sessionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        listLocations = (Button) findViewById(R.id.listLocats);
        downloadQuestions = (Button) findViewById(R.id.dwnldQuestions);
        readResults = (Button) findViewById(R.id.readResults);
        answerQuiz = (Button) findViewById(R.id.answerQuiz);
        shareQuizzes = (Button) findViewById(R.id.shareQuizzes);
        logOut = (Button) findViewById(R.id.logOut);

        sessionId = getIntent().getExtras() != null ? getIntent().getExtras().getString("ssid") : null;
    }

    public void listLocations(View view) {
        Intent intent = new Intent(this, ListTourLocations.class);
        intent.putExtra("ssid", sessionId);
        startActivity(intent);
    }

    public void downloadQuestions(View view) {
        Intent intent = new Intent(this, DownloadQuizQuestions.class);
        intent.putExtra("ssid", sessionId);
        startActivity(intent);
    }

    public void postAnswers(View view) {
        Intent intent = new Intent(this, PostQuizAnswers.class);
        intent.putExtra("ssid", sessionId);
        startActivity(intent);
    }

    public void readResults(View view) {
        Intent intent = new Intent(this, ReadQuizResults.class);
        intent.putExtra("ssid", sessionId);
        startActivity(intent);
    }

    public void ranking(View view) {
        Intent intent = new Intent(this, Ranking.class);
        intent.putExtra("ssid", sessionId);
        startActivity(intent);
    }

    public void getQuiz(View view) {
        Intent intent = new Intent(this, Quiz.class);
        intent.putExtra("ssid", sessionId);
        startActivity(intent);
    }

    public void shareQuizzes(View view) {
        Intent intent = new Intent(this, ShareQuizzes.class);
        intent.putExtra("ssid", sessionId);
        startActivity(intent);
    }

    public void logOut(View v) {
        new LogOutTask(this).execute(sessionId);
    }

    public void onDestroy() {
        new LogOutTask(this).execute(sessionId);
        super.onDestroy();
    }
}