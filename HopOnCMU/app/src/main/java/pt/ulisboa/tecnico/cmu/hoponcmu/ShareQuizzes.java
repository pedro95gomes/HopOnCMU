package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.app.Activity;
import android.util.Log;
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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
import pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks.ReadResultsForSharingTask;
import pt.ulisboa.tecnico.cmu.wifip2p.SimWifiP2pBroadcastReceiver;

public class ShareQuizzes extends Activity {

    /* REAL */
    private final IntentFilter intentFilter = new IntentFilter();
    WifiManager wifiManager;
    Button btnOff, btnDiscover, btnSend, btnDisconnect, btnGet,btnQuiz;
    ListView listView;
    TextView connectionStatus;
    EditText writeMsg;
    BroadcastReceiver mReceiver;
    Boolean simbound = false;
    private boolean finished=false;

    String[] list_quiz;

    /* normal */
    List<SimWifiP2pDevice> peers = new ArrayList<SimWifiP2pDevice>();
    String[] deviceNames;
    SimWifiP2pDevice[] devices;

    /* group */
    List<SimWifiP2pDevice> peers_group = new ArrayList<SimWifiP2pDevice>();
    String[] deviceNames_group;
    SimWifiP2pDevice[] devices_group;
    ListView listView_group;

    //ServerClass server;
    //ClientClass client;
    SimWifiP2pSocket mCliSocket;
    SimWifiP2pSocketServer mSrvSocket;
    //SendReceive sendReceive;
    String ssid;

    /* SIMULATOR */
    SimWifiP2pManager simmManager;
    SimWifiP2pManager.Channel simmChannel = null;
    Messenger simmService = null;
    IntentFilter terIntentFilter;
    Map<String, Integer> results;

    String beacon="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_quizzes);

        ssid = getIntent().getExtras().getString("ssid");

        SimWifiP2pSocketManager.Init(getApplicationContext());

        list_quiz = getApplicationContext().fileList();

        /* SIMULATOR */
        initService();
        setupListeners();
    }

   private void initService() {
        btnOff = (Button) findViewById(R.id.onOff);
        btnSend = (Button)  findViewById(R.id.send);
       btnQuiz = (Button)  findViewById(R.id.quiz);
       btnGet = (Button)  findViewById(R.id.get);
       btnDiscover = (Button)  findViewById(R.id.discover);
        btnDiscover.setEnabled(false);
        btnDisconnect = (Button)  findViewById(R.id.idDisconnectButton);
        btnDisconnect.setEnabled(false);
        listView = (ListView) findViewById(R.id.list);
        connectionStatus = (TextView) findViewById(R.id.status);

        terIntentFilter = new IntentFilter();

        terIntentFilter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        terIntentFilter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        terIntentFilter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        terIntentFilter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);

        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, terIntentFilter);
    }

    private void setupListeners() {
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view){
                if(!simbound){
                    Intent intent = new Intent(view.getContext(), SimWifiP2pService.class);
                    bindService(intent, simmConnection, Context.BIND_AUTO_CREATE);
                    simbound = true;
                    btnDiscover.setEnabled(true);
                    btnOff.setText("Turn OFF");

                    // spawn the chat server background task
                    new IncommingCommTask().executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR);

                }else{
                    unbindService(simmConnection);
                    simbound = false;
                    btnDiscover.setEnabled(false);
                    btnOff.setText("Turn ON");
                }
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(simbound) {
                    simmManager.requestPeers(simmChannel, peerListListener);
                    //simmManager.requestGroupInfo(simmChannel, connectionInfoListener);
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

                new OutgoingCommTask().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        device.getVirtIp());

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = createMessageFromMap(results);

                new SendCommTask().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        msg);
            }
        });

        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = createMessageFromMap(results);

                new SendCommTask().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        msg);
            }
        });

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadResultsForSharingTask task = new ReadResultsForSharingTask(ShareQuizzes.this);
                task.execute(ssid);
            }
        });


        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.idDisconnectButton).setEnabled(false);
                if (mCliSocket != null) {
                    try {
                        mCliSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mCliSocket = null;
            }
        });
    }

    private String createMessageFromMap(Map<String, Integer> results){
        StringBuilder sb = new StringBuilder();
        for(String name : results.keySet()){
            sb.append(name);
            sb.append(" : ");
            sb.append(results.get(name));
            sb.append(" correct answers-");
        }
        return sb.toString();
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
                    if(device.deviceName.contains("M"))
                        beacon=device.deviceName;
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

    /*
     * Asynctasks implementing message exchange
     */

    public class IncommingCommTask extends AsyncTask<Void, String, Boolean> {

        String st = null;

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d("Incoming", "IncommingCommTask started (" + this.hashCode() + ").");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(Integer.parseInt(getString(R.string.port)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    try {
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                        st = sockIn.readLine();
                        if(st!=null)
                            return true;
                        else
                            return false;
                    } catch (IOException e) {
                        Log.d("Error reading socket:", e.getMessage());
                    } finally {
                        sock.close();
                    }
                } catch (IOException e) {
                    Log.d("Error socket:", e.getMessage());
                    break;
                    //e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result) {
                StringBuilder b = new StringBuilder();
                String[] sts= st.split("-");
                for(String s : sts){
                    b.append(s+"\n");
                }
                //readmagBox.setText(st);
                new AlertDialog.Builder(ShareQuizzes.this)
                        .setTitle("Quiz Results")
                        .setMessage(b.toString())
                        .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        }
    }

    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            Toast t = Toast.makeText(ShareQuizzes.this, "Connecting...", Toast.LENGTH_SHORT);
            t.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                mCliSocket = new SimWifiP2pSocket(params[0],
                        Integer.parseInt(getString(R.string.port)));
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                findViewById(R.id.send).setEnabled(false);
            } else {
                btnDisconnect.setEnabled(true);
            }
        }
    }

    public class SendCommTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... msg) {
            try {
                mCliSocket.getOutputStream().write(msg[0].getBytes());
                mCliSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCliSocket = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast t = Toast.makeText(ShareQuizzes.this, "Sent",Toast.LENGTH_SHORT);
            t.show();
        }
    }

    public String[] getQuizNames() {
        return this.list_quiz;
    }

    public void setQuizResult(Map<String, Integer> res){
        this.results = res;
    }

    public void setFinished(boolean f){
        this.finished=f;
    }
}
