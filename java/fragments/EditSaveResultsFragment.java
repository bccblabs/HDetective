package fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.squareup.picasso.Picasso;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import Util.Util;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import carmera.io.wdetector.Base;
import carmera.io.wdetector.InMemorySpiceService;
import carmera.io.wdetector.R;
import models.Classification;
import models.Classifications;
import models.HDSample;
import models.HDSampleParse;
import requests.ClassifyRequest;
import widgets.KeyPairBoolData;
import widgets.MultiSpinnerSearch;
import widgets.SquareImageView;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Created by bski on 7/25/15.
 */
public class EditSaveResultsFragment extends Fragment implements ScreenShotable {

    public final String TAG = getClass().getCanonicalName();
    private Context context;
    private Shimmer shimmer = new Shimmer();

    private HDSampleParse hdSampleParse;
    private HDSample hdSample;
    private SampleSavedDialog sampleSavedDialog;
    private SpiceManager spiceManager = new SpiceManager(InMemorySpiceService.class);
    private InitFragment.StartCaptureListener startCaptureListener;

    @Bind(R.id.photo)
    SquareImageView photo_holder;

    @Bind(R.id.upload_progress_bar)
    ProgressBarCircularIndeterminate progress_bar;

    @Bind(R.id.result_txt)
    ShimmerTextView result_text;

    @Bind(R.id.desc_text)
    ShimmerTextView desc_text;


    @OnClick(R.id.retake_photo_btn)
    public void backToCamera () {
        spiceManager.cancelAllRequests();
        startCaptureListener.OnStartCapture(Parcels.wrap(HDSample.class, hdSample));
    }

    @OnClick(R.id.save_result_btn)
    public void SaveResult () {
        spiceManager.cancelAllRequests();
        sampleSavedDialog = SampleSavedDialog.newInstance();
        Bundle args = new Bundle();
        Parcelable sample = Parcels.wrap(HDSample.class, hdSample);
        args.putParcelable(Base.EXTRA_SAMPLE_DETAILS, sample);
        sampleSavedDialog.setArguments(args);
        sampleSavedDialog.show(getChildFragmentManager(), "saved");
    }

    private SaveCallback parseImageSaveCallback = new SaveCallback() {
        @Override
        public void done(ParseException e) {
            String image_url = hdSampleParse.getHDPhoto().getUrl();
            if (photo_holder != null) {
                Picasso.with(context).load(image_url)
                        .fit()
                        .centerCrop()
                        .into(photo_holder);

                Log.i (TAG, hdSample.getSerial_code().toUpperCase());
                ClassifyRequest classifyRequest = new ClassifyRequest(hdSampleParse.getHDPhoto().getUrl(), hdSample.getSerial_code());
                spiceManager.execute(classifyRequest, null,
                                                      DurationInMillis.ALWAYS_RETURNED,
                                                      new PredictionsRequestListener());
            }
        }
    };

    private final class PredictionsRequestListener implements RequestListener<Classifications> {

        public void setValues (Classification classification, HDSample sample, HDSampleParse sampleParse) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.US);
            hdSample.setDate(df.format(hdSampleParse.getCreatedAt()));
            hdSampleParse.setSerialCode(hdSample.serial_code);
            hdSampleParse.setProductName(hdSample.product_name);
            hdSampleParse.saveEventually();
            if (classification.class_name.equals("match")) {
                sampleParse.setClassifiedLabel("PASSED");
                sample.setLabel("PASSED");
                EditSaveResultsFragment.this.result_text.setText("PASSED");
                EditSaveResultsFragment.this.result_text.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                sampleParse.setClassifiedLabel("FAILED");
                sample.setLabel("FAILED");
                EditSaveResultsFragment.this.result_text.setText("FAILED");
                EditSaveResultsFragment.this.result_text.setTextColor(context.getResources().getColor(R.color.red));
            }
            EditSaveResultsFragment.this.shimmer.start (EditSaveResultsFragment.this.result_text);
        }
        @Override
        public void onRequestFailure (SpiceException spiceException) {
//            Toast.makeText(getActivity(), "Error: " + spiceException.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess (Classifications result) {
            if (result != null) {
                List<Classification> classifications = result.getClassifications();
                progress_bar.setVisibility(View.GONE);
                Classification first = classifications.get(0);
                Classification second = classifications.get(1);
//                for (Classification classification: result.getClassifications()) {
//                    Toast.makeText(context, classification.getProb() + ": " + classification.getClass_name(), Toast.LENGTH_SHORT);
//                }
                if (first.getProb() > second.getProb()) {
                    setValues(first, EditSaveResultsFragment.this.hdSample, EditSaveResultsFragment.this.hdSampleParse);
                } else {
                    setValues(second, EditSaveResultsFragment.this.hdSample, EditSaveResultsFragment.this.hdSampleParse);
                }
            }
        }
    }


    public interface OnRetakePhotoCallback {
        public void retakePhoto ();
    }

    public static EditSaveResultsFragment newInstance () {
        return new EditSaveResultsFragment();
    }

    @Override
    public void takeScreenShot () {
    }

    @Override
    public Bitmap getBitmap() { return null; }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        try {
            startCaptureListener = (InitFragment.StartCaptureListener) activity;
        } catch (ClassCastException e) {
            Log.e (TAG, e.getMessage());
        }
    }

    @Override
    public void onCreate (Bundle savedBundle) {
        super.onCreate(savedBundle);
        try {
            Bundle args = getArguments();
            hdSample = Parcels.unwrap(args.getParcelable(Base.EXTRA_SAMPLE_DETAILS));
            hdSampleParse = new HDSampleParse();
            ParseFile hd_photo = new ParseFile(hdSample.image_data);
            hdSampleParse.setHDPhoto(hd_photo);
            hdSampleParse.saveInBackground(parseImageSaveCallback);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            spiceManager.cancelAllRequests();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_save_result, container, false);
        ButterKnife.bind(this, v);
        shimmer.setDuration(2000);
        shimmer.start(desc_text);

        return v;
    }

    @Override
    public void onViewCreated (View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
.    }

    @Override
    public void onStart () {
        super.onStart();
        spiceManager.start (getActivity());
        if (hdSampleParse != null )
            spiceManager.addListenerIfPending(Classifications.class, null, new PredictionsRequestListener());
    }

    @Override
    public void onStop () {
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
