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
import pt.ulisboa.tecnico.cmu.command.PostAnswersCommand;
import pt.ulisboa.tecnico.cmu.hoponcmu.LogIn;
import pt.ulisboa.tecnico.cmu.hoponcmu.MainMenu;
import pt.ulisboa.tecnico.cmu.hoponcmu.PostQuizAnswers;
import pt.ulisboa.tecnico.cmu.hoponcmu.R;
import pt.ulisboa.tecnico.cmu.response.LogInResponse;
import pt.ulisboa.tecnico.cmu.response.PostAnswersResponse;

public class PostQuizAnswersTask extends AsyncTask<String, Void, String> {

    private PostQuizAnswers postQuizActivity;
    private String sessionId;

    public PostQuizAnswersTask(PostQuizAnswers postQuizActivity) {
        this.postQuizActivity = postQuizActivity;
    }

    @Override
    protected String doInBackground(String[] params) {      //SessionId | Quizname
        Socket server = null;
        sessionId = params[0];
        PostAnswersCommand user_code = new PostAnswersCommand(sessionId,params[1],postQuizActivity.getAnswers());
        String success = "false";

        try {
            server = new Socket("10.0.2.2", 33333);

            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(user_code);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());

            PostAnswersResponse response = (PostAnswersResponse) ois.readObject();
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
            Intent intent = new Intent(postQuizActivity, MainMenu.class);
            intent.putExtra("ssid", sessionId);
            intent.putExtra("Toast", "Answers submited successfully");
            postQuizActivity.startActivity(intent);  //Ir para a activity do MainMenu
        }
        else {
            Toast.makeText(postQuizActivity.getApplicationContext(), "Not possible to submit answers", Toast.LENGTH_LONG);
        }
   }
}
