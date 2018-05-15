package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.net.wifi.p2p.WifiP2pManager.*;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by goncalo on 5/15/18.
 */

public class ShareQuizzes extends AppCompatActivity {

    private final IntentFilter intentFilter = new IntentFilter();
    WifiManager wifiManager;
    Button btnOff, btnDiscover, btnSend;
    ListView listView;
    TextView readmagBox, connectionStatus;
    EditText writeMsg;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNames;
    WifiP2pDevice[] devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share_quizzes);

        initialWork();
        exqListner();


    }

    private void exqListner() {
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view){
                if(wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(false);
                    btnOff.setText("ON");
                }else{
                    wifiManager.setWifiEnabled(true);
                    btnOff.setText("OFF");
                }
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener(){
                    @Override
                    public void onSuccess(){

                        connectionStatus.setText("Discovery Started");
                    }

                    @Override
                    public void onFailure(int i){

                        connectionStatus.setText("Discovery Starting fail");
                    }
                });
            }
        });
    }

    private void initialWork() {
        btnOff = findViewById(R.id.onOff);
        btnSend = findViewById(R.id.send);
        btnDiscover = findViewById(R.id.discover);
        listView = findViewById(R.id.list);
        readmagBox = findViewById(R.id.readMsg);
        writeMsg = findViewById(R.id.writeMsg);
        connectionStatus = findViewById(R.id.status);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this.getMainLooper(),null);
        mReceiver = new WiFiDirectBroacastReceiver(mManager,mChannel,this);

        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    WifiP2pManager.PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList list) {
            if(!list.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(list.getDeviceList());

                deviceNames = new String[list.getDeviceList().size()];
                devices = new WifiP2pDevice[list.getDeviceList().size()];
                int index =0;

                for(WifiP2pDevice device : list.getDeviceList()){
                    deviceNames[index]=device.deviceName;
                    devices[index]=device;
                    index++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,deviceNames);
                listView.setAdapter(adapter);

            }

            if(peers.size()==0){
                Toast.makeText(getApplicationContext(),"No Devices Found",Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
