package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import pt.ulisboa.tecnico.cmu.command.LogInCommand;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.KeystoreManager;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.hoponcmu.LogIn;
import pt.ulisboa.tecnico.cmu.hoponcmu.MainMenu;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.response.LogInResponse;

public class LogInTask extends AsyncTask<String, Void, String> {

    private LogIn logInActivity;
    private String sessionId;

    public LogInTask(LogIn logInActivity) {
        this.logInActivity = logInActivity;
    }

    @Override
    protected String doInBackground(String[] params) {      //Username | Code
        Socket server = null;
        LogInCommand user_code = new LogInCommand(params[0],params[1]);
        String success = "false";

        try {
            KeystoreManager keysManager = new KeystoreManager("phone", "123456", this.logInActivity);
            CryptoManager cryptoManager = CryptoManager.getInstance(keysManager.getKeyPair("phone", "123456").getPublic(), keysManager.getKeyPair("phone", "123456").getPrivate());
            server = new Socket("10.0.2.2", 9090);

            Message message = new Message(cryptoManager.getPublicKey(), keysManager.getKeyStore().getCertificate("server").getPublicKey(), user_code);
            CipheredMessage cipheredMessage = cryptoManager.makeCipheredMessage(message,keysManager.getKeyStore().getCertificate("server").getPublicKey());
            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(cipheredMessage);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
            CipheredMessage responseCiphered = (CipheredMessage) ois.readObject();
            Message responseDeciphered = cryptoManager.decipherCipheredMessage(responseCiphered);

            LogInResponse response = (LogInResponse) responseDeciphered.getResponse();
            sessionId = response.getSessionId();
            success = sessionId!=null ? "true" : "false";


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
            Intent intent = new Intent(logInActivity, MainMenu.class);
            intent.putExtra("ssid", sessionId);
            logInActivity.startActivity(intent);  //Ir para a activity do MainMenu
        }
        else {
            TextView login_invalido = (TextView) logInActivity.findViewById(R.id.invalid_login);
            login_invalido.setVisibility(View.VISIBLE);
        }
    }
}