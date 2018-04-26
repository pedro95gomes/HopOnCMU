package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.DownloadQuizTask;

public class DownloadQuizQuestions extends AppCompatActivity {

    //Download (only) quiz questions from server
    private String name;
    private TextView network;
    String[] files;

    public DownloadQuizQuestions() {
        files = getApplicationContext().fileList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        network = (TextView) findViewById(R.id.network);

        // detectar redes ligadas -> wifi
        // broadcast receiver
        name = "BelemTower";

        //Se detetar rede conectada mudar nome textview pra rede e tornar clickable
        if(!name.isEmpty()){
            network.setText(name);
            network.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    downloadQuiz();
                }
            });
        }
    }

    private void downloadQuiz() {
        for(String file : files){
            if(file.equals(name+".txt")){
                Toast.makeText(this, "Quiz from this museum already Downloaded",Toast.LENGTH_LONG);
                return;
            }
        }
        new DownloadQuizTask(this).execute(name+".txt");
    }

    public void saveQuizFile(List<String[]> questions){
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(name, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(questions);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
