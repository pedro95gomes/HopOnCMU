package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.content.Context;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.ListLocationsTask;
import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.LogOutTask;

public class ListTourLocations extends Activity {  //Lists all tour locations

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

    public void saveLocations(Map<String,String> locations){
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput("museums.txt", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(locations);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logOut2(View view) {
        new LogOutTask(this).execute(ssid);
    }
}
