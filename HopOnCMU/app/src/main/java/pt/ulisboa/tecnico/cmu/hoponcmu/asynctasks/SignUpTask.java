package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import pt.ulisboa.tecnico.cmu.command.SignUpCommand;
import pt.ulisboa.tecnico.cmu.hoponcmu.LogIn;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.hoponcmu.SignUp;
import pt.ulisboa.tecnico.cmu.response.SignUpResponse;

public class SignUpTask extends AsyncTask<String, Void, String> {

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
            server = new Socket("10.0.2.2", 9090);

            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(user_code);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
            SignUpResponse response = (SignUpResponse) ois.readObject();
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