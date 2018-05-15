package pt.ulisboa.tecnico.cmu.hoponcmu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

/**
 * Created by goncalo on 5/15/18.
 */

public class WiFiDirectBroacastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private ShareQuizzes shareQuizzes;

    public WiFiDirectBroacastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, ShareQuizzes shareQuizzes) {
    }

    public void WiFiDirectBroadcastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, ShareQuizzes shareQuizzes){
        this.mChannel=mChannel;
        this.mManager=mManager;
        this.shareQuizzes=shareQuizzes;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);

            if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context,"Wifi is on",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"Wifi is off",Toast.LENGTH_SHORT).show();
            }

        } else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(mManager!=null){
                mManager.requestPeers(mChannel,shareQuizzes.peerListListener);
            }
        } else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            //do something
        } else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
            //do something
        }

    }
}
