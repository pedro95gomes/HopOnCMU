package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.ListLocationsTask;
import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.LogOutTask;

public class ListTourLocations extends AppCompatActivity {  //Lists all tour locations

    private ListView list;
    private ArrayList<String> list_location;      //Array que vai ter o strings com o nome das locations
    private TextView nothing;
    private String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tour_locations);

        list = (ListView) findViewById(R.id.list);
        nothing = (TextView) findViewById(R.id.nothing);

        ssid = getIntent().getExtras().getString("ssid");

        new ListLocationsTask(ListTourLocations.this).execute();

        //TODO
        //Depois se quisermos, também dá para fazer uma ação se o user clicar em cada item da lista
    }

    public void onDestroy() {
        new LogOutTask(this).execute(ssid);
        super.onDestroy();
    }
}
