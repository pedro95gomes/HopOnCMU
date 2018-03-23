package pt.ulisboa.tecnico.cmov.hoponcmu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUp extends AppCompatActivity {

    private EditText username;
    private EditText code;
    private TextView invalid_account;
    private Button btn_sing_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        username = (EditText) findViewById(R.id.username);
        code = (EditText) findViewById(R.id.code);
        invalid_account = (TextView) findViewById(R.id.invalid_account);
        btn_sing_up = (Button) findViewById(R.id.btn_sign_up);
    }

    public void signUp(View v) {
        //TODO fazer o registo do user no servidor
        boolean registo_valido = true;   //Só para testar

        if (registo_valido) {
            Intent intent = new Intent(SignUp.this, LogIn.class);
            intent.putExtra("Toast", "User registered successfully!");
            startActivity(intent);  //Ir para a activity do LogIn
        } else
            invalid_account.setVisibility(View.VISIBLE);
    }
}

