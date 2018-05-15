package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import pt.ulisboa.tecnico.cmu.command.DownloadQuestionsCommand;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.KeystoreManager;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.hoponcmu.DownloadQuizQuestions;
import pt.ulisboa.tecnico.cmu.hoponcmu.MainMenu;
import pt.ulisboa.tecnico.cmu.response.DownloadQuestionsResponse;

public class DownloadQuizTask extends AsyncTask<String, Integer, String> {

    private DownloadQuizQuestions downloadQuizActivity;
    private List<String[]> questions;
    private String ssid;

    public DownloadQuizTask(DownloadQuizQuestions downloadQuizActivity) {
        this.downloadQuizActivity = downloadQuizActivity;
    }

    @Override
    protected String doInBackground(String[] params) {      //Username | Code
        Socket server = null;
        String register_success = null;
        DownloadQuestionsCommand user_code = new DownloadQuestionsCommand(params[0]);

        ssid = params[1];


        try {
            KeystoreManager keysManager = new KeystoreManager("phone", "123456", this.downloadQuizActivity);
            CryptoManager cryptoManager = CryptoManager.getInstance(keysManager.getKeyPair("phone", "123456").getPublic(), keysManager.getKeyPair("phone", "123456").getPrivate());
            server = new Socket("10.0.2.2", 9090);
            Message message = new Message(cryptoManager.getPublicKey(), keysManager.getKeyStore().getCertificate("server").getPublicKey(), user_code);
            CipheredMessage cipheredMessage = cryptoManager.makeCipheredMessage(message,keysManager.getKeyStore().getCertificate("server").getPublicKey());
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
            downloadQuizActivity.saveQuizFile(questions);
            Intent intent = new Intent(downloadQuizActivity, MainMenu.class);
            intent.putExtra("Toast", "File downloaded successfully");
            intent.putExtra("ssid", ssid);
            Log.d("File:", "Successfully downloaded");
            downloadQuizActivity.startActivity(intent);  //Ir para a activity do LogIn
        }
    }
}
