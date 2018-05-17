package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;

import pt.ulisboa.tecnico.cmu.command.PostAnswersCommand;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.CryptoUtil;
import pt.ulisboa.tecnico.cmu.crypto.KeystoreManager;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.hoponcmu.MainMenu;
import pt.ulisboa.tecnico.cmu.hoponcmu.PostQuizAnswers;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.response.PostAnswersResponse;

public class PostQuizAnswersTask extends BaseTask {

    private String sessionId;

    public PostQuizAnswersTask(PostQuizAnswers postQuizActivity) {
        super(postQuizActivity);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(String[] params) {      //SessionId | Quizname
        Socket server = null;
        sessionId = params[0];
        PostAnswersCommand user_code = new PostAnswersCommand(sessionId,params[1],((PostQuizAnswers)getActivity()).getAnswers());
        String success = "false";

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
            Intent intent = new Intent(getActivity(), MainMenu.class);
            intent.putExtra("ssid", sessionId);
            intent.putExtra("Toast", "Answers submited successfully");
            getActivity().startActivity(intent);  //Ir para a activity do MainMenu
        }
        else {
            Toast.makeText(getActivity().getApplicationContext(), "Not possible to submit answers", Toast.LENGTH_LONG);
        }
    }
}