package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmu.command.RankingCommand;
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
            server = new Socket("10.0.2.2", 9090);

            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(user_code);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());

            RankingResponse response = (RankingResponse) ois.readObject();
            ranking_list = response.getRanking();

            oos.close();
            ois.close();
            if(ranking_list != null) {     //Só para testar, depois apagar!
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
    }
}

