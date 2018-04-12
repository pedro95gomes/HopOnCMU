package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ReadQuizResults extends AppCompatActivity {        //Asks results to server and shows them

    private ListView list;
    private ArrayList<String> list_rank;      //Array que vai ter as posições do ranking

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_quiz_results);

        list = (ListView) findViewById(R.id.list);
        list_rank = new ArrayList<String>();

        //Adicionar locations manualmente agora. Depois, isto é pedido ao servidor
        list_rank.add("1º - User1");
        list_rank.add("2º - User2");
        list_rank.add("3º - User3");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_rank);
        list.setAdapter(adapter);
    }
}
