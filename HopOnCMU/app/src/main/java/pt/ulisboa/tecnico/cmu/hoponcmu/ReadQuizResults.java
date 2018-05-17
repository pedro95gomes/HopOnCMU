package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.LogOutTask;
import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.ReadResultsTask;
import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.ResultsAdapter;

public class ReadQuizResults extends AppCompatActivity {        //Asks results to server and shows them

    private ListView list;
    private String[] list_quiz;      //Array que vai ter as posições do ranking
    private String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_quiz_results);

        list = (ListView) findViewById(R.id.list);

        ssid = getIntent().getExtras().getString("ssid");
        list_quiz = getApplicationContext().fileList();

        new ReadResultsTask(this).execute(ssid);
    }

    public String[] getQuizNames() {
        return this.list_quiz;
    }

    public void onDestroy() {
        new LogOutTask(this).execute(ssid);
        super.onDestroy();
    }
}
