package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.DownloadQuizTask;
import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.LogOutTask;

public class DownloadQuizQuestions extends Activity {

    //Download (only) quiz questions from server
    private String name;
    private TextView network;
    String[] files;
    String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloadquiz);
        network = (TextView) findViewById(R.id.network);
        files = getApplicationContext().fileList();

        ssid = getIntent().getExtras().getString("ssid");

        String netssid = getCurrentSSID();
        netssid = new String("M"+1); // o wifi esta a retornar unknow
        System.out.println("aaaaaaaaaaaaaaaaaaa" + netssid);
        Map<String,String> museums = openLocationsFile();
        for(String key: museums.keySet()){
            if(netssid.equals("M"+key)){
                name = museums.get(key);
            }
        }

        /* DESCOMENTAR PARA TESTAR SEM WIFI */
        //name = "BelemTower";

        if(!netssid.isEmpty()){
            network.setText(name);
            network.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    downloadQuiz();
                }
            });
        }
        else{
            network.setText("Museum WiFi is unreachable");
            network.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    warningWifi();
                }
            });
        }
    }

    private void warningWifi() {
        Toast toast = Toast.makeText(this,"Please connect to the museum WiFi",Toast.LENGTH_LONG);
        toast.show();
    }

    private void downloadQuiz() {
        /*for(String file : files){
            if(file.equals(name+".txt")){
                Toast.makeText(this, "Quiz from this museum already Downloaded",Toast.LENGTH_LONG);
                return;
            }
        }*/
        new DownloadQuizTask(this).execute(name+".txt", ssid);
    }

    public void saveQuizFile(List<String[]> questions){
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(name+".txt", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(questions);
            Log.d("File questions:", questions.toString());
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentSSID() {
        ConnectivityManager connManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String ssid= null;
        if (networkInfo.isConnected()) {
            System.out.println("bbbbbbbbbbb");
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                System.out.println("bbbbbbbbbbb");
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }

    public Map<String,String> openLocationsFile(){
        Map<String,String> locations = new HashMap<>();
        try {
            FileInputStream fis = getApplicationContext().openFileInput("museums.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            locations = (Map<String,String>) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return locations;
    }
}
