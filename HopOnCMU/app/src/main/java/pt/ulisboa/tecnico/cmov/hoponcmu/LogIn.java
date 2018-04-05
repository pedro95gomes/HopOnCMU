package pt.ulisboa.tecnico.cmov.hoponcmu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {

    private EditText username;
    private EditText code;
    private TextView login_invalido;
    private Button btn_sing_up;
    private Button btn_log_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        username = (EditText) findViewById(R.id.username);
        code = (EditText) findViewById(R.id.code);
        login_invalido = (TextView) findViewById(R.id.invalid_login);
        btn_log_in = (Button) findViewById(R.id.btn_log_in);
        btn_sing_up = (Button) findViewById(R.id.btn_sign_up);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Intent intent = getIntent();
            String toast_text = intent.getExtras().getString("Toast");  //Toast a dizer que o user foi registado com sucesso
            Toast.makeText(LogIn.this, toast_text, Toast.LENGTH_LONG).show();
        }

    }

    public void logIn (View v){
        //TODO autenticar o user com o servidor
        boolean loginValido=true;   //Só para testar

        if(loginValido) {
            Intent intent = new Intent(LogIn.this, MainMenu.class);
            startActivity(intent);  //Ir para a activity do MainMenu
        }
        else
            login_invalido.setVisibility(View.VISIBLE);

    }

    public void signUp (View v){
        Intent intent = new Intent(LogIn.this, SignUp.class);
        startActivity(intent);  //Ir para a activity do SignUp
    }

}
