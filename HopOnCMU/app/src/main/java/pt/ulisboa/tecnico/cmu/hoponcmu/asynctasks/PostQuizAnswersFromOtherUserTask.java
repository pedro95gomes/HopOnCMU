package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import pt.ulisboa.tecnico.cmu.command.PostAnswersCommand;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.CryptoUtil;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.hoponcmu.MainMenu;
import pt.ulisboa.tecnico.cmu.hoponcmu.PostQuizAnswers;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.hoponcmu.ShareQuizzes;
import pt.ulisboa.tecnico.cmu.response.PostAnswersResponse;

public class PostQuizAnswersFromOtherUserTask extends AsyncTask<String, Void, String> {

    private ShareQuizzes activity;
    private String sessionId;

    public PostQuizAnswersFromOtherUserTask(ShareQuizzes activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String[] params) {      //SessionId | Quizname
        Socket server = null;
        sessionId = params[0];
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAA" + activity.getTimeTaken());
        PostAnswersCommand user_code = new PostAnswersCommand(sessionId,params[1],activity.getAnswers(),activity.getTimeTaken());
        String success = "false";

        try {
            KeyPair keys = CryptoUtil.gen();

            PublicKey serverK = CryptoUtil.getX509CertificateFromStream(activity.getResources().openRawResource(R.raw.server)).getPublicKey();
            CryptoManager cryptoManager = new CryptoManager(keys.getPublic(),keys.getPrivate(),serverK);
            server = new Socket();
            server.connect(new InetSocketAddress("10.0.2.2", 9090),4000);

            Message message = new Message(Base64.encodeToString(keys.getPublic().getEncoded(), Base64.NO_WRAP), Base64.encodeToString(serverK.getEncoded(), Base64.NO_WRAP) , user_code);
            CipheredMessage cipheredMessage = cryptoManager.makeCipheredMessage(message,serverK);
            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(cipheredMessage);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
            CipheredMessage responseCiphered = (CipheredMessage) ois.readObject();
            Message responseDeciphered = cryptoManager.decipherCipheredMessage(responseCiphered);
            PostAnswersResponse response = (PostAnswersResponse) responseDeciphered.getResponse();
            success = response.getSuccess() ? "true" : "false";

            oos.close();
            ois.close();
            Log.d("DummyClient", "SUCCESS= " + success);
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
        return success;
    }

    @Override
    protected void onPostExecute(String o) {
        if (o != null && o.equals("true")) {
            Toast t = Toast.makeText(activity, "Answers submitted successfully",Toast.LENGTH_SHORT);
            t.show();
        }
        else {
            Toast t = Toast.makeText(activity, "Not possible to submit answers", Toast.LENGTH_LONG);
            t.show();
        }
    }
}