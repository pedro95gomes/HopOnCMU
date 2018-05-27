package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.net.wifi.p2p.WifiP2pManager.*;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.LogOutTask;
import pt.ulisboa.tecnico.cmu.wifip2p.SimWifiP2pBroadcastReceiver;


public class ShareQuizzes extends Activity {

    /* REAL */
    private final IntentFilter intentFilter = new IntentFilter();
    WifiManager wifiManager;
    Button btnOff, btnDiscover, btnSend;
    ListView listView;
    TextView readmagBox, connectionStatus;
    EditText writeMsg;
    BroadcastReceiver mReceiver;
    Boolean simbound = false;

    /* normal */
    List<SimWifiP2pDevice> peers = new ArrayList<SimWifiP2pDevice>();
    String[] deviceNames;
    SimWifiP2pDevice[] devices;

    /* group */
    List<SimWifiP2pDevice> peers_group = new ArrayList<SimWifiP2pDevice>();
    String[] deviceNames_group;
    SimWifiP2pDevice[] devices_group;
    ListView listView_group;

    static final int MESSAGE_READ=1;

    ServerClass server;
    ClientClass client;
    SendReceive sendReceive;
    String ssid;

    /* SIMULATOR */
    SimWifiP2pManager simmManager;
    SimWifiP2pManager.Channel simmChannel = null;
    Messenger simmService = null;
    IntentFilter terIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_quizzes);

        ssid = getIntent().getExtras().getString("ssid");

        SimWifiP2pSocketManager.Init(getApplicationContext());

        /* SIMULATOR */
        initWork();
        //exqListner();
        //TODO
    }

   private void initWork() {
        btnOff = (Button) findViewById(R.id.onOff);
        btnSend = (Button)  findViewById(R.id.send);
        btnDiscover = (Button)  findViewById(R.id.discover);
        listView = (ListView) findViewById(R.id.list);
        readmagBox = (TextView) findViewById(R.id.readMsg);
        writeMsg = (EditText) findViewById(R.id.writeMsg);
        connectionStatus = (TextView) findViewById(R.id.status);

        terIntentFilter = new IntentFilter();

        terIntentFilter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        terIntentFilter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        terIntentFilter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        terIntentFilter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);

        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, terIntentFilter);
    }

    private void exqListner() {
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view){
                if(!simbound){
                    Intent intent = new Intent(view.getContext(), SimWifiP2pService.class);
                    bindService(intent, simmConnection, Context.BIND_AUTO_CREATE);
                    simbound = true;
                    btnOff.setText("ON");
                }else{
                    unbindService(simmConnection);
                    simbound = false;
                    btnOff.setText("OFF");
                }
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(simbound) {
                    simmManager.requestPeers(simmChannel, peerListListener);
                    simmManager.requestGroupInfo(simmChannel, connectionInfoListener);
                } else{
                    Toast.makeText(view.getContext(), "Service not bound",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final SimWifiP2pDevice device = devices[position];

                simmManager.connect(mChannel,config, new ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"Connected to" + device.deviceName,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int position) {
                        Toast.makeText(getApplicationContext(),"Not Connected ",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = writeMsg.getText().toString();
                sendReceive.write(msg);
            }
        });
    }

    SimWifiP2pManager.PeerListListener peerListListener = new SimWifiP2pManager.PeerListListener(){

        @Override
        public void onPeersAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList) {
            if(!simWifiP2pDeviceList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(simWifiP2pDeviceList.getDeviceList());

                deviceNames = new String[simWifiP2pDeviceList.getDeviceList().size()];
                devices = new SimWifiP2pDevice[simWifiP2pDeviceList.getDeviceList().size()];
                int index =0;

                for(SimWifiP2pDevice device : simWifiP2pDeviceList.getDeviceList()){
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

    SimWifiP2pManager.GroupInfoListener connectionInfoListener = new SimWifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {

            if(!simWifiP2pInfo.getDevicesInNetwork().equals(peers_group)){
                peers_group.clear();
                peers_group.addAll(simWifiP2pDeviceList.getDeviceList());

                deviceNames_group = new String[simWifiP2pDeviceList.getDeviceList().size()];
                devices_group = new SimWifiP2pDevice[simWifiP2pDeviceList.getDeviceList().size()];
                int index =0;

                for(String deviceName : simWifiP2pInfo.getDevicesInNetwork()){
                    SimWifiP2pDevice device = simWifiP2pDeviceList.getByName(deviceName);
                    deviceNames_group[index]=device.deviceName;
                    devices_group[index]=device;
                    index++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,deviceNames);
                listView_group.setAdapter(adapter);

            }

            if(peers_group.size()==0){
                Toast.makeText(getApplicationContext(),"No Devices Found",Toast.LENGTH_SHORT).show();
                return;
            }

            /*InetAddress groupOwnerAddress = null;

            if(simWifiP2pInfo.askIsConnected() && simWifiP2pInfo.askIsGO()){
                connectionStatus.setText("Host");
                server = new ServerClass();
                server.start();
            } else if(simWifiP2pInfo.askIsConnected()){
                for(String swd : simWifiP2pInfo.getDevicesInNetwork()){
                }
                groupOwnerAddress = simWifiP2pInfo.
                connectionStatus.setText("Client");
                client = new ClientClass(groupOwnerAddress);
                client.start();
            }*/
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

    public void logOut(View view) {
        new LogOutTask(this).execute(ssid);
    }

    public class ServerClass extends Thread{

        SimWifiP2pSocket socket;
        SimWifiP2pSocketServer serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new SimWifiP2pSocketServer(10001);
                socket = serverSocket.accept();
                sendReceive= new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread{

        SimWifiP2pSocket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress){
            hostAdd = hostAddress.getHostAddress();
            socket = null;
        }

        @Override
        public void run() {
            try {
                socket = new SimWifiP2pSocket(hostAdd,10001);
                sendReceive= new SendReceive(socket);
                sendReceive.start();
            } catch (UnknownHostException e) {
                System.out.println("Unknown Host:" + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendReceive extends Thread{

        private SimWifiP2pSocket socket;
        private BufferedReader inputStream;
        private OutputStream outputStream;

        public SendReceive(SimWifiP2pSocket skt){
            socket=skt;
            try {
                inputStream=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;

            while(socket!=null){
                try {
                    message=inputStream.readLine();
                    if(message!=null){
                        readmagBox.setText(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(String message){
            try {
                outputStream.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* SIMULATOR */
    private ServiceConnection simmConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            simmService = new Messenger(service);
            simmManager = new SimWifiP2pManager(simmService);
            simmChannel   =  simmManager.initialize(getApplication(),   getMainLooper(),
                    null);
            simbound=true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            simmService = null;
            simmManager = null;
            simmChannel = null;
            simbound=false;
        }
    };

}
