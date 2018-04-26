package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmu.command.ListLocationsCommand;
import pt.ulisboa.tecnico.cmu.hoponcmu.ListTourLocations;
import pt.ulisboa.tecnico.cmu.response.ListLocationsResponse;

public class ListLocationsTask extends AsyncTask<String, Void, String> {

    private ListTourLocations listLocationsActivity;
    private ArrayList<String> locations = null;

    public ListLocationsTask(ListTourLocations listLocationsActivity) {
        this.listLocationsActivity = listLocationsActivity;
    }

    @Override
    protected String doInBackground(String[] params) {      //Username | Code
        Socket server = null;
        String register_success = null;
        ListLocationsCommand user_code = new ListLocationsCommand();

        try {
            server = new Socket("10.0.2.2", 9090);

            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(user_code);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());

            ListLocationsResponse response = (ListLocationsResponse) ois.readObject();
            locations = response.getLocations();

            oos.close();
            ois.close();
            if(locations != null) {     //Só para testar, depois apagar!
                register_success = "true";
            }
            else {
                register_success = "false";
            }
            Log.d("DummyClient", "SUCCESS= " + locations.toString());
        }
        catch (Exception e) {
            Log.d("DummyClient", "DummyTask failed..." + e.getMessage());
            e.printStackTrace();
        } finally {
            if (server != null) {
                try { server.close(); }
                catch (Exception e) { }
            }
        }
        //return reply;
        return register_success;
    }

    @Override
    protected void onPostExecute(String o) {
        if (o != null && o.equals("true")) {
            Toast.makeText(listLocationsActivity, o, Toast.LENGTH_SHORT).show();
            listLocationsActivity.updateLocations(locations);
        }
    }
}
