package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

import pt.ulisboa.tecnico.cmu.command.DownloadQuestionsCommand;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.CryptoUtil;
import pt.ulisboa.tecnico.cmu.crypto.KeystoreManager;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.hoponcmu.DownloadQuizQuestions;
import pt.ulisboa.tecnico.cmu.hoponcmu.MainMenu;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.response.DownloadQuestionsResponse;

public class DownloadQuizTask extends BaseTask {

    private List<String[]> questions;
    private String ssid;

    public DownloadQuizTask(DownloadQuizQuestions downloadQuizActivity) {
        super(downloadQuizActivity);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(String[] params) {      //Username | Code
        Socket server = null;
        String register_success = null;
        DownloadQuestionsCommand user_code = new DownloadQuestionsCommand(params[0]);

        ssid = params[1];

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
            DownloadQuestionsResponse response = (DownloadQuestionsResponse) responseDeciphered.getResponse();
            questions = response.getQuestions();

            oos.close();
            ois.close();
            if(questions != null) {
                register_success = "true";
            }
            else {
                register_success = "false";
            }
            Log.d("DummyClient", "SUCCESS= " + questions.toString());
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
        return register_success;
    }

    @Override
    protected void onPostExecute(String o) {
        if (o != null && o.equals("true")) {
            ((DownloadQuizQuestions)getActivity()).saveQuizFile(questions);
            Intent intent = new Intent(getActivity(), MainMenu.class);
            intent.putExtra("Toast", "File downloaded successfully");
            intent.putExtra("ssid", ssid);
            Log.d("File:", "Successfully downloaded");
            getActivity().startActivity(intent);  //Ir para a activity do LogIn
        }
    }
}