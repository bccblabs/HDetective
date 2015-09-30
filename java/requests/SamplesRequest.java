package requests;

import android.util.Log;

import com.google.gson.Gson;
import com.octo.android.robospice.request.okhttp.OkHttpSpiceRequest;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

import Util.Constants;
import models.HDSample;
import models.HDSampleList;
import models.SampleQuery;

/**
 * Created by bski on 9/29/15.
 */
public class SamplesRequest extends OkHttpSpiceRequest<HDSampleList> {
    private static final String TAG = "HD_SAMPLE_REQUEST";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private SampleQuery sampleQuery;
    public static final com.squareup.okhttp.MediaType JSON =
            com.squareup.okhttp.MediaType.parse("application/json; charset=utf-8");

    public SamplesRequest(SampleQuery sampleQuery_) {
        super(HDSampleList.class);
        this.sampleQuery = sampleQuery_;
    }

    @Override
    public HDSampleList loadDataFromNetwork() throws Exception {
        try {
            String req_json = gson.toJson(sampleQuery);
            RequestBody body = RequestBody.create(JSON, req_json);
            Request request = new Request.Builder()
                    .url(Constants.CLASSIFIER_URL + "/classifications")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException(response.message());
            return gson.fromJson(response.body().charStream(), HDSampleList.class);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            return null;
        }
    }
}