package pt.ulisboa.tecnico.cmov.hoponcmu.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import pt.ulisboa.tecnico.cmov.command.HelloCommand;
import pt.ulisboa.tecnico.cmov.hoponcmu.SignUp;
import pt.ulisboa.tecnico.cmov.response.HelloResponse;

public class SignUpTask extends AsyncTask<String, Void, String> {

    private SignUp signUpActivity;

    public SignUpTask(SignUp signUpActivity) {
        this.signUpActivity = signUpActivity;
    }

    @Override
    protected String doInBackground(String[] params) {
        Socket server = null;
        String reply = null;
        HelloCommand hc = new HelloCommand(params[0]);
        try {
            server = new Socket("10.0.2.2", 9090);

            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(hc);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
            HelloResponse hr = (HelloResponse) ois.readObject();
            reply = hr.getMessage();

            oos.close();
            ois.close();
            Log.d("DummyClient", "Hi there!!");
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
        return reply;
    }

    @Override
    protected void onPostExecute(String o) {
        if (o != null) {
            Toast.makeText(signUpActivity, o, Toast.LENGTH_SHORT).show();
        }
    }
}
