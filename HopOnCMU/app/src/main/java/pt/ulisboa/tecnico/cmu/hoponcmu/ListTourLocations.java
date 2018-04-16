package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.ListLocationsTask;

public class ListTourLocations extends AppCompatActivity {  //Lists all tour locations

    private ListView list;
    private ArrayList<String> list_location;      //Array que vai ter o strings com o nome das locations
    private TextView nothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tour_locations);

        list = (ListView) findViewById(R.id.list);
        nothing = (TextView) findViewById(R.id.nothing);

        new ListLocationsTask(ListTourLocations.this);

        //TODO
        //Depois se quisermos, também dá para fazer uma ação se o user clicar em cada item da lista
    }

    public void updateLocations(ArrayList<String> locations) {
        if(locations.size()!=0) {
            list_location = locations;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_location);
            list.setAdapter(adapter);
        }
        else
            nothing.setVisibility(View.VISIBLE);
    }
}