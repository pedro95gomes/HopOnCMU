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

import pt.ulisboa.tecnico.cmu.command.LogInCommand;
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
            server = new Socket("10.0.2.2", 33333);

            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(user_code);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());

            LogInResponse response = (LogInResponse) ois.readObject();
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
