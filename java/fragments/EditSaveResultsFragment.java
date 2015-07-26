package fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
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

import adapters.ClassificationResultsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    public static final String EXTRA_IMAGE_DATA = "extra_image_data";
    private Bitmap bitmap;
    private Context context;
    private HDSampleParse hdSampleParse;
    private HDSample hdSample;
    private SaveResultFragment saveResultFragment;
    private SpiceManager spiceManager = new SpiceManager(InMemorySpiceService.class);
    private ClassificationResultsAdapter classificationResultsAdapter;

    @Bind(R.id.edit_save_container)
    View containerView;

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
        args.putParcelable(SaveResultFragment.EXTRA_SAMPLE_DETAILS, Parcels.wrap(HDSample.class, this.hdSample));
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
                spiceManager.execute(classifyRequest, hdSampleParse.getObjectId(), DurationInMillis.ALWAYS_RETURNED, new PredictionsRequestListener());
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
            if (result!=null) {
                progress_bar.setVisibility(View.GONE);
                classifications_recycler.setVisibility(View.VISIBLE);
                classificationResultsAdapter = new ClassificationResultsAdapter();
                classifications_recycler.setAdapter(classificationResultsAdapter);
                classifications_recycler.setLayoutManager(new LinearLayoutManager(context));
                classifications_recycler.setHasFixedSize(true);

                classificationResultsAdapter.addAll(result.getClassifications().subList(0,3));
                hdSample.setClassifications(result.getClassifications().subList(0,2));
                classificationResultsAdapter.notifyDataSetChanged();

                Toast.makeText(getActivity(), "Predictions Received: " + result.getClassifications().size(), Toast.LENGTH_SHORT).show();

                for (Classification prediction : result.getClassifications()) {

                    Log.i(TAG, prediction.getClass_name() + ": " + prediction.getProb().toString());
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
    private OnRetakePhotoCallback retakePhotoCallback;
    @Override
    public void takeScreenShot () {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                        containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                EditSaveResultsFragment.this.bitmap = bitmap;
            }
        };

        thread.start();
    }

    @Override
    public Bitmap getBitmap() { return bitmap; }


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
        Bundle args = getArguments();
        byte[] image_data = Parcels.unwrap(args.getParcelable(EXTRA_IMAGE_DATA));
        hdSampleParse = new HDSampleParse();
        ParseFile hd_photo = new ParseFile(image_data);
        hdSampleParse.setHDPhoto(hd_photo);
        hdSampleParse.saveInBackground(parseImageSaveCallback);
        hdSample = new HDSample();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_save_result, container, false);
        ButterKnife.bind(this, v);
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

    public class DropDownAnim extends Animation {
        private final int targetHeight;
        private final View view;
        private final boolean down;

        public DropDownAnim(View view, int targetHeight, boolean down) {
            this.view = view;
            this.targetHeight = targetHeight;
            this.down = down;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight;
            if (down) {
                newHeight = (int) (targetHeight * interpolatedTime);
            } else {
                newHeight = (int) (targetHeight * (1 - interpolatedTime));
            }
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth,
                               int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

}
