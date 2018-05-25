package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import pt.ulisboa.tecnico.cmu.command.LogInCommand;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.CryptoUtil;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.hoponcmu.LogIn;
import pt.ulisboa.tecnico.cmu.hoponcmu.MainMenu;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.response.LogInResponse;

public class LogInTask extends AsyncTask<String, Void, String>  {
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

            KeyPair keys = CryptoUtil.gen();
            CryptoManager cryptoManager = new CryptoManager(keys.getPublic(),keys.getPrivate());
            PublicKey serverK = CryptoUtil.getX509CertificateFromStream(logInActivity.getResources().openRawResource(R.raw.server)).getPublicKey();
            server = new Socket();
            server.connect(new InetSocketAddress("10.0.2.2", 9090),4000);


            Message message = new Message(android.util.Base64.encodeToString(keys.getPublic().getEncoded(), Base64.DEFAULT),android.util.Base64.encodeToString(serverK.getEncoded(), Base64.DEFAULT) , user_code);
            CipheredMessage cipheredMessage = cryptoManager.makeCipheredMessage(message,serverK);
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