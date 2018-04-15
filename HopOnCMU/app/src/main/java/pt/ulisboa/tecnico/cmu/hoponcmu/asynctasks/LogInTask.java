package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import pt.ulisboa.tecnico.cmu.command.LogInCommand;
import pt.ulisboa.tecnico.cmu.hoponcmu.LogIn;
import pt.ulisboa.tecnico.cmu.response.LogInResponse;

public class LogInTask extends AsyncTask<String, Void, String> {

    private LogIn logInActivity;

    public LogInTask(LogIn logInActivity) {
        this.logInActivity = logInActivity;
    }

    @Override
    protected String doInBackground(String[] params) {      //Username | Code
        Socket server = null;
        String register_success = null;
        LogInCommand user_code = new LogInCommand(params[0],params[1]);

        try {
            server = new Socket("10.0.2.2", 9090);

            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(user_code);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());

            LogInResponse response = (LogInResponse) ois.readObject();
            boolean a = response.getSuccess();
            if(a==true) //Só para testar, depois apagar!
                System.out.println("TRUE");
            else
                System.out.println("FALSE");

            oos.close();
            ois.close();
            Log.d("DummyClient", "SUCCESS= " + a);
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
        return register_success;
    }

    @Override
    protected void onPostExecute(String o) {
       if (o != null) {
            Toast.makeText(logInActivity, o, Toast.LENGTH_SHORT).show();
       }
    }
}
