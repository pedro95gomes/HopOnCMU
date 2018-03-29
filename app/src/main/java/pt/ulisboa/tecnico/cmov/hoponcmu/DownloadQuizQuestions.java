package pt.ulisboa.tecnico.cmov.hoponcmu;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloadQuizQuestions extends Service {

    //Download (only) quiz questions from server

    public DownloadQuizQuestions() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
