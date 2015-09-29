package fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.parceler.Parcels;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import carmera.io.wdetector.Base;
import carmera.io.wdetector.InMemorySpiceService;
import carmera.io.wdetector.R;
import models.Classification;
import models.Classifications;
import models.HDSample;
import widgets.SquareImageView;

/**
 * Created by bski on 7/25/15.
 */
public class EditSaveResultsFragment extends Fragment {

    public final String TAG = getClass().getCanonicalName();
    private Context context;
//    private Shimmer shimmer = new Shimmer();

//    private HDSampleParse hdSampleParse;
    private HDSample hdSample;
//    private SampleSavedDialog sampleSavedDialog;
    private SpiceManager spiceManager = new SpiceManager(InMemorySpiceService.class);
    private InitFragment.StartCaptureListener startCaptureListener;
    private SortedMap<String, String> product_serial = new TreeMap<>();

    @Bind(R.id.photo)
    SquareImageView photo_holder;

    @Bind(R.id.upload_progress_bar)
    ProgressBarCircularIndeterminate progress_bar;

//    @Bind(R.id.result_txt)
//    ShimmerTextView result_text;
//
//    @Bind(R.id.desc_text)
//    ShimmerTextView desc_text;

    @Bind(R.id.msg)
    TextView msg;

    @OnClick(R.id.retake_photo_btn)
    public void backToCamera () {
        spiceManager.cancelAllRequests();
        startCaptureListener.OnStartCapture(Parcels.wrap(HDSample.class, hdSample));
    }

    @OnClick(R.id.save_result_btn)
    public void SaveResult () {
        spiceManager.cancelAllRequests();
        Bundle args = new Bundle();
        Parcelable sample = Parcels.wrap(HDSample.class, hdSample);
        args.putParcelable(Base.EXTRA_SAMPLE_DETAILS, sample);
//        sampleSavedDialog = SampleSavedDialog.newInstance();
//        sampleSavedDialog.setArguments(args);
//        sampleSavedDialog.show(getChildFragmentManager(), "saved");
//        hdSampleParse.saveEventually();
    }


    private final class PredictionsRequestListener implements RequestListener<Classifications> {

//        public void setValues (Classification classification, HDSample sample, HDSampleParse sampleParse) {
//            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.US);
//            hdSample.setDate(df.format(hdSampleParse.getCreatedAt()));
//            hdSampleParse.setSerialCode(hdSample.serial_code);
//            hdSampleParse.setProductName(hdSample.product_name);
//
//            if (product_serial.get(classification.class_name) == null ||
//                    (!product_serial.get(classification.class_name).equals(hdSample.serial_code) && classification.prob > 0.9) ) {
//                sampleParse.setClassifiedLabel("FAILED");
//                sample.setLabel("FAILED");
//                EditSaveResultsFragment.this.result_text.setText("FAILED");
//                EditSaveResultsFragment.this.result_text.setTextColor(context.getResources().getColor(R.color.red));
//            } else if (product_serial.get(classification.class_name)
//                              .equals (hdSample.serial_code) && classification.prob >= 0.9999) {
//
//                sampleParse.setClassifiedLabel("PASSED");
//                sample.setLabel("PASSED");
//                EditSaveResultsFragment.this.result_text.setText("PASSED");
//                EditSaveResultsFragment.this.result_text.setTextColor(context.getResources().getColor(R.color.green));
//            } else {
//                sampleParse.setClassifiedLabel("RETRY");
//                sample.setLabel("RETRY");
//                EditSaveResultsFragment.this.result_text.setText("RETRY");
//                msg.setText("PHOTO REJECTED");
//                EditSaveResultsFragment.this.result_text.setTextColor(context.getResources().getColor(R.color.green));
//            }
//            EditSaveResultsFragment.this.shimmer.start (EditSaveResultsFragment.this.result_text);
//        }
        @Override
        public void onRequestFailure (SpiceException spiceException) {
            progress_bar.setVisibility(View.GONE);
//            EditSaveResultsFragment.this.result_text.setText("RETRY");
            msg.setText("Certainty level not reached");
//            EditSaveResultsFragment.this.result_text.setTextColor(context.getResources().getColor(R.color.green));
//            EditSaveResultsFragment.this.shimmer.start (EditSaveResultsFragment.this.result_text);
        }

        @Override
        public void onRequestSuccess (Classifications result) {
            if (result != null) {
                progress_bar.setVisibility(View.GONE);
                double best = Double.MIN_VALUE;
                Classification maxClass = new Classification();
                for (Classification classification: result.getClassifications()) {
                    if (classification.getProb() > best) {
                        maxClass = classification;
                        best = classification.getProb();
                    }
                }

//                setValues(maxClass, EditSaveResultsFragment.this.hdSample, EditSaveResultsFragment.this.hdSampleParse);
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
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            spiceManager.cancelAllRequests();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_save_result, container, false);
        ButterKnife.bind(this, v);
//        shimmer.setDuration(2000);
//        shimmer.start(desc_text);
        product_serial.put ("tahoexl", "WD5000AAKX-00ERMA0");
        product_serial.put ("green6", "WD60EZRX-00MVLB1");
        product_serial.put ("enterprise", "WD4000FYYZ-01UL1B1");
        product_serial.put ("greenpower", "WD30EURS-63SPKY0");
        product_serial.put ("nohdd", "Non-HDD");
        product_serial.put ("wd5000aakx", "WD5000AAKX-00ERMA0");
        product_serial.put ("wd4000fyyz", "WD4000FYYZ-01UL1B1");
        product_serial.put ("wd60efrx", "WD60EFRX-00MVLB1");
        product_serial.put ("wd60efrx", "WD60EZRX-00MVLB1");
        product_serial.put ("desk_obj", "Non-HDD");

        return v;
    }

    @Override
    public void onViewCreated (View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart () {
        super.onStart();
        spiceManager.start (getActivity());
//        if (hdSampleParse != null )
//            spiceManager.addListenerIfPending(Classifications.class, null, new PredictionsRequestListener());
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
