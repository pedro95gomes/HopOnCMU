package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Quiz extends AppCompatActivity {

    String[] files;
    ListView quizzes;
    //Answer a quiz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

        quizzes = (ListView) findViewById(R.id.listquizes);
        files = getApplicationContext().fileList();

        ArrayAdapter<String> fileslist = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,files);
        quizzes.setAdapter(fileslist);
        quizzes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String quizname = quizzes.getItemAtPosition(position).toString().trim();
                List<String[]> questions = openQuizFile(quizname);

                Intent intent = new Intent(getApplicationContext(), PostQuizAnswers.class);
                int q_count = 1;
                for(String[] question : questions){
                    intent.putExtra("question"+q_count,question);
                    q_count++;
                }
                intent.putExtra("quizname", quizname);
                intent.putExtra("ssid", getIntent().getStringExtra("ssid"));
                intent.putExtra("q_count", q_count);
                startActivity(intent);
            }
        });
    }

    public List<String[]> openQuizFile(String quizname){
        List<String[]> questions = new ArrayList<String[]>();
        try {
            FileInputStream fis = getApplicationContext().openFileInput(quizname);
            ObjectInputStream ois = new ObjectInputStream(fis);
            questions = (List<String[]>) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return questions;
    }
}