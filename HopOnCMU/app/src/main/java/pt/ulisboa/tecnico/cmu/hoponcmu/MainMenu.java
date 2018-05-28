package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Activity;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.LogOutTask;
import pt.ulisboa.tecnico.cmu.wifip2p.SimWifiP2pBroadcastReceiver;

public class MainMenu extends Activity implements SimWifiP2pManager.PeerListListener {

    Button listLocations;
    Button downloadQuestions;
    Button postQuiz;
    Button readResults;
    Button answerQuiz;
    Button shareQuizzes;
    Button logOut;
    String sessionId;
    String beacon="";
    public static final String TAG = "peerscanner";

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private boolean mBound = false;
    private SimWifiP2pBroadcastReceiver mReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        listLocations = (Button) findViewById(R.id.listLocats);
        downloadQuestions = (Button) findViewById(R.id.dwnldQuestions);
        readResults = (Button) findViewById(R.id.readResults);
        answerQuiz = (Button) findViewById(R.id.answerQuiz);
        shareQuizzes = (Button) findViewById(R.id.shareQuizzes);
        logOut = (Button) findViewById(R.id.logOut);

        guiSetButtonListeners();
        guiUpdateInitState();

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);

        sessionId = getIntent().getExtras() != null ? getIntent().getExtras().getString("ssid") : null;
    }

    public void listLocations(View view) {
        if(beacon.contains("M")) {
            Intent intent = new Intent(this, ListTourLocations.class);
            intent.putExtra("ssid", sessionId);
            startActivity(intent);
        }else{
            Toast.makeText(this, "no near museums", Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadQuestions(View view) {
        if(beacon.contains("M")) {
            Intent intent = new Intent(this, DownloadQuizQuestions.class);
            intent.putExtra("ssid", sessionId);
            intent.putExtra("beacon", beacon);
            startActivity(intent);
        }else{
            Toast.makeText(this, "no near museums", Toast.LENGTH_SHORT).show();
        }
    }

    public void readResults(View view) {
        if(beacon.contains("M")) {
            Intent intent = new Intent(this, ReadQuizResults.class);
            intent.putExtra("ssid", sessionId);
            startActivity(intent);
        }else{
            Toast.makeText(this, "no near museums", Toast.LENGTH_SHORT).show();
        }
    }

    public void ranking(View view) {
        if(beacon.contains("M")) {
            Intent intent = new Intent(this, Ranking.class);
            intent.putExtra("ssid", sessionId);
            startActivity(intent);
        }else{
            Toast.makeText(this, "no near museums", Toast.LENGTH_SHORT).show();
            }
    }

    public void getQuiz(View view) {
        Intent intent = new Intent(this, Quiz.class);
        intent.putExtra("ssid", sessionId);
        if(beacon.contains("M")) {
            intent.putExtra("beacon", beacon);
        }else{
            intent.putExtra("beacon", "N");
        }
        startActivity(intent);
    }

    public void shareQuizzes(View view) {
        Intent intent = new Intent(this, ShareQuizzes.class);
        intent.putExtra("ssid", sessionId);
        startActivity(intent);
    }

    public void logOut(View v) {
        new LogOutTask(this).execute(sessionId);
    }

    public void onDestroy() {
        new LogOutTask(this).execute(sessionId);
        super.onDestroy();
    }


    private View.OnClickListener listenerWifiOnButton = new View.OnClickListener() {
        public void onClick(View v){
            Intent intent = new Intent(v.getContext(), SimWifiP2pService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
            guiUpdateDisconnectedState();
        }
    };

    private View.OnClickListener listenerWifiOffButton = new View.OnClickListener() {
        public void onClick(View v){
            if (mBound) {
                unbindService(mConnection);
                mBound = false;
                guiUpdateInitState();
            }
        }
    };

    private View.OnClickListener listenerInRangeButton = new View.OnClickListener() {
        public void onClick(View v){
            if (mBound) {
                mManager.requestPeers(mChannel, MainMenu.this);
            } else {
                Toast.makeText(v.getContext(), "Service not bound",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

	/*
	 * Termite listeners
	 */

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        StringBuilder peersStr = new StringBuilder();
        beacon="";
        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            peersStr.append(devstr);
            if(device.deviceName.contains("M"))
                beacon = device.deviceName;
        }
        System.out.print("QQQQQQQQQQQQQQQQQQQQQ" + beacon);

        // display list of devices in range
        new AlertDialog.Builder(this)
                .setTitle("Devices in WiFi Range")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

	/*
	 * Helper methods for updating the interface
	 */

    private void guiSetButtonListeners() {

        findViewById(R.id.idWifiOnButton).setOnClickListener(listenerWifiOnButton);
        findViewById(R.id.idWifiOffButton).setOnClickListener(listenerWifiOffButton);
        findViewById(R.id.idInRangeButton).setOnClickListener(listenerInRangeButton);
    }

    private void guiUpdateInitState() {

        findViewById(R.id.idWifiOnButton).setEnabled(true);
        findViewById(R.id.idWifiOffButton).setEnabled(false);
        findViewById(R.id.idInRangeButton).setEnabled(false);
    }

    private void guiUpdateDisconnectedState() {

        findViewById(R.id.idWifiOnButton).setEnabled(false);
        findViewById(R.id.idWifiOffButton).setEnabled(true);
        findViewById(R.id.idInRangeButton).setEnabled(true);
    }
}