package pt.ulisboa.tecnico.cmu.hoponcmu.asynctasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

public abstract class BaseTask extends AsyncTask<String, Integer, String> {
    WifiManager wifiManager;
    WifiInfo connectionInfo;
    NetworkInfo networkInfo;
    ConnectivityManager connManager;
    String ssid;
    AppCompatActivity activity;

    public BaseTask(AppCompatActivity activity) {
        this.activity = activity;
    }

    public WifiManager getWifiManager() {
        return wifiManager;
    }

    public String getSsid(){
        return ssid;
    }

    public AppCompatActivity getActivity(){
        return this.activity;
    }

    public String getCurrentSSID() {
        connManager = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }
    /*public void connectWifi(String ssid) {

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(activity.getApplicationContext(), "Enabling WiFi", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            WifiConfiguration wfcfg = configureNetwork(ssid, password);
            int networkId = wifiManager.addNetwork(wfcfg);
            if (networkId != -1) {
                //success
                // wifiManager.enableNetwork...
            }
        }
    }

    public WifiConfiguration configureNetwork(String ssid, String password) {
        WifiConfiguration wfc = new WifiConfiguration();

        wfc.SSID = "\"".concat(ssid).concat("\"");
        wfc.status = WifiConfiguration.Status.DISABLED;
        wfc.priority = 40;
        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wfc.allowedAuthAlgorithms.clear();
        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

        if (isHexString(password)) wfc.wepKeys[0] = password;
        else wfc.wepKeys[0] = "\"".concat(password).concat("\"");
        wfc.wepTxKeyIndex = 0;

        wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        wfc.preSharedKey = "\"".concat(password).concat("\"");

        return wfc;
    }

    private static boolean isHexString(@Nullable String pass) {
        return pass != null && pass.matches("[0-9a-fA-F]+");
    }

    public class WifiReceiver extends BroadcastReceiver {
        WifiInfo info;

        // This method call when number of wifi connections changed
        @Override
        public void onReceive(Context c, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (netInfo.isConnectedOrConnecting()) {
                    info = wifiManager.getConnectionInfo();
                    connected_ssid = info.getSSID();
                }
            }
        }
    }*/
}
