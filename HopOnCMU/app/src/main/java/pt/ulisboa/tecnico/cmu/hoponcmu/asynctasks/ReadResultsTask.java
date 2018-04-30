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
import java.util.Map;

import pt.ulisboa.tecnico.cmu.command.ListLocationsCommand;
import pt.ulisboa.tecnico.cmu.command.QuizResultsCommand;
import pt.ulisboa.tecnico.cmu.hoponcmu.ListTourLocations;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.hoponcmu.ReadQuizResults;
import pt.ulisboa.tecnico.cmu.response.ListLocationsResponse;
import pt.ulisboa.tecnico.cmu.response.QuizResultsResponse;

public class ReadResultsTask extends AsyncTask<String, Void, String> {

    private ReadQuizResults readQuizResultsActivity;
    private String[] files;
    private Map<String, Integer> results;
    private Map<String, Integer> numQuestions;

    public ReadResultsTask(ReadQuizResults readQuizResultsActivity) {
        this.readQuizResultsActivity = readQuizResultsActivity;
    }

    @Override
    protected String doInBackground(String[] params) {      //Username | Code
        Socket server = null;
        String register_success = null;
        files = readQuizResultsActivity.getQuizNames();
        QuizResultsCommand user_code = new QuizResultsCommand(params[0],files);

        try {
            server = new Socket("10.0.2.2", 9090);

            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(user_code);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());

            QuizResultsResponse response = (QuizResultsResponse) ois.readObject();

            oos.close();
            ois.close();
            if(response.getResults() != null) {     //Só para testar, depois apagar!
                register_success = "true";
                results = response.getResults();
                numQuestions = response.getnumQuestions();
            }
            else {
                register_success = "false";
            }
            Log.d("DummyClient", "SUCCESS= " + response.toString());
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
            ListView list = (ListView) readQuizResultsActivity.findViewById(R.id.list);
            ResultsAdapter fileslist = new ResultsAdapter(readQuizResultsActivity, files, results, numQuestions);
            list.setAdapter(fileslist);
        }
    }
}
