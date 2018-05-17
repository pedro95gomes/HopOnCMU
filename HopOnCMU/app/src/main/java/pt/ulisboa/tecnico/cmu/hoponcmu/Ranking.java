package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.LogOutTask;
import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.RankingTask;

public class Ranking extends AppCompatActivity {

    private ListView list;
    private String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);

        list = (ListView) findViewById(R.id.list);

        ssid = getIntent().getExtras().getString("ssid");

        new RankingTask(Ranking.this).execute();

    }

    public void onDestroy() {
        new LogOutTask(this).execute(ssid);
        super.onDestroy();
    }
}
