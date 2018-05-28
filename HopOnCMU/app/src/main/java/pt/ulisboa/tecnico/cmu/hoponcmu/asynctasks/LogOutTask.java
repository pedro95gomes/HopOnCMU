package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.app.Activity;
import android.util.Base64;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import pt.ulisboa.tecnico.cmu.command.LogOutCommand;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.CryptoUtil;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.hoponcmu.LogIn;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.response.LogOutResponse;

public class LogOutTask extends AsyncTask<String, Void, String>  {
    private Activity mainMenu;
    private String ssid;
    private String username;

    public LogOutTask(Activity mainMenu) {
        this.mainMenu = mainMenu;
    }

    @Override
    protected String doInBackground(String[] params) {      //Username | SessionId
        Socket server = null;
        LogOutCommand user_code = new LogOutCommand(params[0]);
        String success = "false";

        try {
            KeyPair keys = CryptoUtil.gen();

            PublicKey serverK = CryptoUtil.getX509CertificateFromStream(mainMenu.getResources().openRawResource(R.raw.server)).getPublicKey();
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

            LogOutResponse response = (LogOutResponse) responseDeciphered.getResponse();
            ssid = response.getSessionId();
            username = response.getUserName();
            success = (ssid.equals(user_code.getSessionId()) && username!=null) ? "true" : "false";

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
            Intent intent = new Intent(mainMenu, LogIn.class);
            intent.putExtra("Toast", "User "+username+" logged out!");
            mainMenu.startActivity(intent);  //Ir para a activity do Log In
            mainMenu.finish();
        }
    }
}