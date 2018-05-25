package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;


public class SimWifiBroadcastReceiver extends BroadcastReceiver {

    private SimWifiP2pManager mManager;
    private SimWifiP2pManager.Channel mChannel;
    private ShareQuizzes shareQuizzes;

    public SimWifiBroadcastReceiver(SimWifiP2pManager mManager, SimWifiP2pManager.Channel mChannel, ShareQuizzes shareQuizzes){
        this.mChannel=mChannel;
        this.mManager=mManager;
        this.shareQuizzes=shareQuizzes;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE,
                        -
                                1);
                if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
                    Toast.makeText(context, "Wifi is on", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Wifi is off", Toast.LENGTH_SHORT).show();
                }
            }

        } else if(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(mManager!=null){
                mManager.requestPeers(mChannel,shareQuizzes.peerListListener);
            }
        } else if(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.
                equals(action)){
            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
            if(mManager==null){
                return;
            }

            if(ginfo.askIsConnected()){
                mManager.requestGroupInfo(mChannel,shareQuizzes.connectionInfoListener);
            }else{
                shareQuizzes.connectionStatus.setText("Device Disconnected");
            }

        } else if(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.
                equals(action)) {
            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
            //TODO
        }

    }
}
