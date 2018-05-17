package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ListView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

import pt.ulisboa.tecnico.cmu.command.QuizResultsCommand;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.CryptoUtil;
import pt.ulisboa.tecnico.cmu.crypto.KeystoreManager;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.hoponcmu.ReadQuizResults;
import pt.ulisboa.tecnico.cmu.response.QuizResultsResponse;

public class ReadResultsTask extends BaseTask {

    private String[] files;
    private Map<String, Integer> results;
    private Map<String, Integer> numQuestions;

    public ReadResultsTask(ReadQuizResults readQuizResultsActivity) {
        super(readQuizResultsActivity);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(String[] params) {      //Username | Code
        Socket server = null;
        String register_success = null;
        files = ((ReadQuizResults)getActivity()).getQuizNames();
        QuizResultsCommand user_code = new QuizResultsCommand(params[0],files);

        try {
            KeyPair keys = CryptoUtil.gen();
            CryptoManager cryptoManager = new CryptoManager(keys.getPublic(),keys.getPrivate());
            PublicKey serverK = CryptoUtil.getX509CertificateFromStream(getActivity().getResources().openRawResource(R.raw.server)).getPublicKey();
            server = new Socket("10.0.2.2", 9090);

            Message message = new Message(Base64.getEncoder().encodeToString(keys.getPublic().getEncoded()),Base64.getEncoder().encodeToString(serverK.getEncoded()) , user_code);
            CipheredMessage cipheredMessage = cryptoManager.makeCipheredMessage(message,serverK);
            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(cipheredMessage);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
            CipheredMessage responseCiphered = (CipheredMessage) ois.readObject();
            Message responseDeciphered = cryptoManager.decipherCipheredMessage(responseCiphered);
            QuizResultsResponse response = (QuizResultsResponse) responseDeciphered.getResponse();

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
            ListView list = (ListView) getActivity().findViewById(R.id.list);
            ResultsAdapter fileslist = new ResultsAdapter(getActivity(), files, results, numQuestions);
            list.setAdapter(fileslist);
        }
    }
}