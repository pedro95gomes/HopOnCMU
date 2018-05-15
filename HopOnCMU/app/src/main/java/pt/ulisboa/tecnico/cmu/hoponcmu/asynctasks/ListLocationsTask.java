package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import pt.ulisboa.tecnico.cmu.command.ListLocationsCommand;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.KeystoreManager;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.hoponcmu.ListTourLocations;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.response.ListLocationsResponse;

public class ListLocationsTask extends AsyncTask<String, Void, String> {

    private ListTourLocations listLocationsActivity;
    private List<String> locations = null;

    public ListLocationsTask(ListTourLocations listLocationsActivity) {
        this.listLocationsActivity = listLocationsActivity;
    }

    @Override
    protected String doInBackground(String[] params) {      //Username | Code
        Socket server = null;
        String register_success = null;
        ListLocationsCommand user_code = new ListLocationsCommand();

        try {
            KeystoreManager keysManager = new KeystoreManager("phone", "123456", this.listLocationsActivity);
            CryptoManager cryptoManager = new CryptoManager(keysManager.getKeyPair("phone", "123456").getPublic(), keysManager.getKeyPair("phone", "123456").getPrivate());
            server = new Socket("10.0.2.2", 9090);

            Message message = new Message(cryptoManager.getPublicKey(), keysManager.getKeyStore().getCertificate("server").getPublicKey(), user_code);
            CipheredMessage cipheredMessage = cryptoManager.makeCipheredMessage(message,cryptoManager.getPublicKey());
            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(cipheredMessage);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
            CipheredMessage responseCiphered = (CipheredMessage) ois.readObject();
            Message responseDeciphered = cryptoManager.decipherCipheredMessage(responseCiphered);
            ListLocationsResponse response = (ListLocationsResponse) responseDeciphered.getResponse();
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
            ListView listlist_location = listLocationsActivity.findViewById(R.id.list);
            if (locations.size() != 0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(listLocationsActivity, android.R.layout.simple_list_item_1, locations);
                listlist_location.setAdapter(adapter);
            } else {
                ListView nothing = listLocationsActivity.findViewById(R.id.nothing);
                nothing.setVisibility(View.VISIBLE);
            }
        }
    }
}
