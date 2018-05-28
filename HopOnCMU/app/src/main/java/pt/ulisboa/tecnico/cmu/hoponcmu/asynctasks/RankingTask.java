package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.List;

import pt.ulisboa.tecnico.cmu.command.RankingCommand;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.CryptoUtil;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.hoponcmu.Ranking;
import pt.ulisboa.tecnico.cmu.response.RankingResponse;

public class RankingTask extends AsyncTask<String, Void, String> {

    private Ranking ranking_activity;
    private List<String> ranking_list = null;

    public RankingTask(Ranking r) {
        ranking_activity = r;
    }

    @Override
    protected String doInBackground(String[] params) {      //Username | Code
        Socket server = null;
        String register_success = null;
        RankingCommand user_code = new RankingCommand();

        try {
            KeyPair keys = CryptoUtil.gen();

            PublicKey serverK = CryptoUtil.getX509CertificateFromStream(this.ranking_activity.getResources().openRawResource(R.raw.server)).getPublicKey();
            CryptoManager cryptoManager = new CryptoManager(keys.getPublic(),keys.getPrivate(),serverK);
            server = new Socket();
            server.connect(new InetSocketAddress("10.0.2.2", 9090),4000);


            Message message = new Message(android.util.Base64.encodeToString(keys.getPublic().getEncoded(), Base64.NO_WRAP),android.util.Base64.encodeToString(serverK.getEncoded(), Base64.NO_WRAP) , user_code);
            CipheredMessage cipheredMessage = cryptoManager.makeCipheredMessage(message,serverK);
            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(cipheredMessage);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
            CipheredMessage responseCiphered = (CipheredMessage) ois.readObject();
            Message responseDeciphered = cryptoManager.decipherCipheredMessage(responseCiphered);

            RankingResponse response = (RankingResponse) responseDeciphered.getResponse();
            ranking_list = response.getRanking();

            oos.close();
            ois.close();
            if(ranking_list != null) {     //Sรณ para testar, depois apagar!
                register_success = "true";
            }
            else {
                register_success = "false";
            }
            Log.d("DummyClient", "SUCCESS= " + ranking_list.toString());
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
        /*
        if (o != null && o.equals("true")) {
            ListView list_ranking = ranking_activity.findViewById(R.id.list);
            if (ranking_list.size() != 0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ranking_activity, android.R.layout.simple_list_item_1, ranking_list);
                list_ranking.setAdapter(adapter);
            } else {
                ListView nothing = ranking_activity.findViewById(R.id.nothing);
                nothing.setVisibility(View.VISIBLE);
            }
        }
        */
        if (o != null && o.equals("true")) {
            ListView list = (ListView) ranking_activity.findViewById(R.id.list);
            ResultsAdapterRanking fileslist = new ResultsAdapterRanking(ranking_activity, ranking_list);
            list.setAdapter(fileslist);
        }
    }
}

