package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    Button listLocations;
    Button downloadQuestions;
    Button postQuiz;
    Button readResults;
    Button answerQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listLocations = (Button) findViewById(R.id.listLocats);
        downloadQuestions = (Button) findViewById(R.id.dwnldQuestions);
        postQuiz = (Button) findViewById(R.id.postAnswers);
        readResults = (Button) findViewById(R.id.readResults);
        answerQuiz = (Button) findViewById(R.id.answerQuiz);

        setContentView(R.layout.main_menu);
    }

    public void listLocations(View view) {
        Intent intent = new Intent(this, ListTourLocations.class);
        startActivity(intent);
    }

    public void downloadQuestions(View view) {
        Intent intent = new Intent(this, DownloadQuizQuestions.class);
        startService(intent);
    }

    public void postAnswers(View view) {
        Intent intent = new Intent(this, PostQuizAnswers.class);
        startActivity(intent);
    }

    public void readResults(View view) {
        Intent intent = new Intent(this, ReadQuizResults.class);
        startActivity(intent);
    }

    public void getQuiz(View view) {
        Intent intent = new Intent(this, Quiz.class);
        startActivity(intent);
    }
}