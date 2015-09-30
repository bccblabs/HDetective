package Util;

import android.util.Log;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;

public class Util {
    private static final String TAG = "Util";
    private static Socket uploadSocket;
    public static Socket getUploadSocket () {
        if (uploadSocket == null) {
            try {
                uploadSocket = IO.socket(Constants.CLASSIFIER_URL);
                uploadSocket.connect();
            } catch (URISyntaxException e) {
                Log.i(TAG, e.getMessage());
            }
        }
        Log.i (TAG, uploadSocket.toString());
        return uploadSocket;
    }
}
