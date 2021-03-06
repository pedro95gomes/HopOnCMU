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

import pt.ulisboa.tecnico.cmu.command.SignUpCommand;
import pt.ulisboa.tecnico.cmu.crypto.CipheredMessage;
import pt.ulisboa.tecnico.cmu.crypto.CryptoManager;
import pt.ulisboa.tecnico.cmu.crypto.CryptoUtil;
import pt.ulisboa.tecnico.cmu.crypto.Message;
import pt.ulisboa.tecnico.cmu.hoponcmu.LogIn;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.hoponcmu.SignUp;
import pt.ulisboa.tecnico.cmu.response.SignUpResponse;

public class SignUpTask extends AsyncTask<String, Void, String>  {

    private SignUp signUpActivity;

    public SignUpTask(SignUp signUpActivity) {
        this.signUpActivity = signUpActivity;
    }

    @Override
    protected String doInBackground(String[] params) {      //Username | Code
        Socket server = null;
        SignUpCommand user_code = new SignUpCommand(params[0],params[1]);
        String success = null;
        try {
            KeyPair keys = CryptoUtil.gen();

            PublicKey serverK = CryptoUtil.getX509CertificateFromStream(signUpActivity.getResources().openRawResource(R.raw.server)).getPublicKey();
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
            SignUpResponse response = (SignUpResponse) responseDeciphered.getResponse();
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
        return success;
    }

    @Override
    protected void onPostExecute(String o) {
        if (o != null && o.equals("true")) {
            //Toast.makeText(signUpActivity, "Registered Successfully", Toast.LENGTH_SHORT);
            Intent intent = new Intent(signUpActivity, LogIn.class);
            intent.putExtra("Toast", "User registered successfully!");
            signUpActivity.startActivity(intent);  //Ir para a activity do LogIn
        } else{
            TextView t = (TextView) signUpActivity.findViewById(R.id.invalid_account);
            t.setVisibility(View.VISIBLE);
        }
    }
}