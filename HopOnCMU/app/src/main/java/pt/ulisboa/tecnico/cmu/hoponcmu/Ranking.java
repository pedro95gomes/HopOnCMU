package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.*;

public class Ranking extends Activity {

    private ListView list;
    private String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);

        list = (ListView) findViewById(R.id.list);

        ssid = getIntent().getExtras().getString("ssid");

        new RankingTask(this).execute();

    }


    public void logOut2(View view) {
        new LogOutTask(this).execute(ssid);
    }
}