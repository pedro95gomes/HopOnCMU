package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import pt.ulisboa.tecnico.cmu.command.SignUpCommand;
import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.SignUpTask;
import pt.ulisboa.tecnico.cmu.response.Response;
import pt.ulisboa.tecnico.cmu.response.SignUpResponse;

public class SignUp extends Activity {

    private EditText username;
    private EditText code;
    private TextView invalid_account;
    private Button btn_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        username = (EditText) findViewById(R.id.username);
        code = (EditText) findViewById(R.id.code);
        invalid_account = (TextView) findViewById(R.id.invalid_account);
        btn_sign_up = (Button) findViewById(R.id.btn_sign_up);

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signUp(v);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void signUp(View v) throws IOException, ClassNotFoundException {

        new SignUpTask(SignUp.this).execute(username.getText().toString(), code.getText().toString());

        /*if (invalid_account.getVisibility() == View.INVISIBLE) {
            Intent intent = new Intent(SignUp.this, LogIn.class);
            intent.putExtra("Toast", "User registered successfully!");
            startActivity(intent);  //Ir para a activity do LogIn
        }*/
    }
}