package requests;

import android.util.Log;

import com.google.gson.Gson;
import com.octo.android.robospice.request.okhttp.OkHttpSpiceRequest;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;

import models.Classifications;

public class ClassifyRequest extends OkHttpSpiceRequest<Classifications> {

    private static final String TAG = "PREDICTION_REQUEST";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private String image_url;
    public ClassifyRequest(String image_url_) {
        super(Classifications.class);
        this.image_url = image_url_;
    }

    @Override
    public Classifications loadDataFromNetwork () throws Exception {

        try {
            String url = String.format ("http://52.11.70.25:5000/classify?image_url=%s", this.image_url);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException(response.message());
            return gson.fromJson (response.body().charStream(), Classifications.class);


        } catch (IOException ie) {
            Log.i(TAG, ie.getMessage());
            return null;
        }
    }
}