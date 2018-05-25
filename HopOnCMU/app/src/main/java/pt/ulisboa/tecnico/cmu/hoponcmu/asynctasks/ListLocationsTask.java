package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.cmu.command.ListLocationsCommand;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.CryptoUtil;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.hoponcmu.ListTourLocations;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.response.ListLocationsResponse;

public class ListLocationsTask extends AsyncTask<String, Void, String>  {

    private ListTourLocations listLocationsActivity;
    private Map<String,String> locations = null;

    public ListLocationsTask(ListTourLocations listLocationsActivity) {
        this.listLocationsActivity = listLocationsActivity;
    }

    @Override
    protected String doInBackground(String[] params) {      //Username | Code
        Socket server = null;
        String register_success = null;
        ListLocationsCommand user_code = new ListLocationsCommand();

        try {
            KeyPair keys = CryptoUtil.gen();
            CryptoManager cryptoManager = new CryptoManager(keys.getPublic(),keys.getPrivate());
            PublicKey serverK = CryptoUtil.getX509CertificateFromStream(listLocationsActivity.getResources().openRawResource(R.raw.server)).getPublicKey();
            server = new Socket();
            server.connect(new InetSocketAddress("10.0.2.2", 9090),4000);

            Message message = new Message(android.util.Base64.encodeToString(keys.getPublic().getEncoded(), Base64.NO_WRAP),android.util.Base64.encodeToString(serverK.getEncoded(), Base64.DEFAULT) , user_code);
            CipheredMessage cipheredMessage = cryptoManager.makeCipheredMessage(message,serverK);
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
            ListView listlist_location = (ListView) listLocationsActivity.findViewById(R.id.list);
            List<String> locs = new ArrayList<String>();
            for(String key: locations.keySet()){
                locs.add(locations.get(key));
            }
            if (locations.size() != 0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(listLocationsActivity, android.R.layout.simple_list_item_1, locs);
                listlist_location.setAdapter(adapter);
                listLocationsActivity.saveLocations(locations);
            } else {
                ListView nothing = (ListView) listLocationsActivity.findViewById(R.id.nothing);
                nothing.setVisibility(View.VISIBLE);
            }
        }
    }
}