package fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import org.parceler.Parcels;

import java.util.List;

import adapters.ClassificationResultsAdapter;
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
import widgets.SquareImageView;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Created by bski on 7/25/15.
 */
public class EditSaveResultsFragment extends Fragment implements ScreenShotable {

    public final String TAG = getClass().getCanonicalName();
    private Context context;
    private HDSampleParse hdSampleParse;
    private HDSample hdSample;
    private SaveResultFragment saveResultFragment;
    private SpiceManager spiceManager = new SpiceManager(InMemorySpiceService.class);
    private ClassificationResultsAdapter classificationResultsAdapter;
    private OnRetakePhotoCallback retakePhotoCallback;

    @Bind(R.id.photo)
    SquareImageView photo_holder;

    @Bind(R.id.upload_progress_bar)
    ProgressBarCircularIndeterminate progress_bar;

    @Bind(R.id.classifications_recycler)
    RecyclerView classifications_recycler;

    @OnClick(R.id.retake_photo_btn)
    public void backToCamera () {
        spiceManager.cancelAllRequests();
        retakePhotoCallback.retakePhoto();
    }
    @OnClick(R.id.save_item)
    public void saveItem() {
        Bundle args = new Bundle();
        args.putParcelable(Base.EXTRA_SAMPLE_DETAILS, Parcels.wrap(HDSample.class, this.hdSample));
        saveResultFragment = SaveResultFragment.newInstance(false);
        saveResultFragment.setArguments(args);
        saveResultFragment.show(getChildFragmentManager(), "edit_and_save");
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

                ClassifyRequest classifyRequest = new ClassifyRequest(hdSampleParse.getHDPhoto().getUrl());
                spiceManager.execute(classifyRequest, hdSampleParse.getObjectId(),
                                                      DurationInMillis.ALWAYS_RETURNED,
                                                      new PredictionsRequestListener());
                hdSample.setDate(hdSampleParse.getCreatedAt().toString());
                hdSample.setParse_id(hdSampleParse.getObjectId());
            }
        }
    };

    private final class PredictionsRequestListener implements RequestListener<Classifications> {
        @Override
        public void onRequestFailure (SpiceException spiceException) {
            Toast.makeText(getActivity(), "Error: " + spiceException.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess (Classifications result) {
            if (result != null) {
                List<Classification> classifications = result.getClassifications();
                progress_bar.setVisibility(View.GONE);
                classifications_recycler.setVisibility(View.VISIBLE);
                classificationResultsAdapter.addAll(classifications);
                if (classifications.size() > 0) {
                    hdSample.setProb(classifications.get(0).prob);
                    hdSample.setLabel(classifications.get(0).class_name);
                }
                classificationResultsAdapter.notifyDataSetChanged();
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
            retakePhotoCallback = (OnRetakePhotoCallback) activity;
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
            retakePhotoCallback.retakePhoto();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_save_result, container, false);
        ButterKnife.bind(this, v);
        classificationResultsAdapter = new ClassificationResultsAdapter();
        classifications_recycler.setAdapter(classificationResultsAdapter);
        classifications_recycler.setLayoutManager(new LinearLayoutManager(context));
        classifications_recycler.setHasFixedSize(true);
        return v;
    }

    @Override
    public void onViewCreated (View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Picasso.with(context).load(R.drawable.hard_disk_holder)
                .fit()
                .centerCrop()
                .into(photo_holder);
    }

    @Override
    public void onStart () {
        super.onStart();
        spiceManager.start (getActivity());
        if (hdSampleParse != null )
            spiceManager.addListenerIfPending(Classifications.class, hdSampleParse.getObjectId(), new PredictionsRequestListener());
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
