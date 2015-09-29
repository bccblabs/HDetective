package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import carmera.io.wdetector.Base;
import carmera.io.wdetector.R;
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;
import models.HDSample;
import widgets.SquareImageView;

/**
 * Created by bski on 9/29/15.
 *
 *  On camera result, first display bytes as image
 */
public class ReviewSubmit extends SupportBlurDialogFragment {
    public final String TAG = getClass().getCanonicalName();
    private CaptureFragment.OnCameraResultListener cameraResultListener;
    private Parcelable hdSample;
    @Bind(R.id.photo)
    SquareImageView photo_preview;

    @OnClick(R.id.retake_photo_btn)
    public void retake_photo () {
        this.dismiss();
    }

    @OnClick(R.id.confirm_upload)
    public void upload_photo () {
        this.dismiss();
        cameraResultListener.OnCameraResult(hdSample);
    }
    public static ReviewSubmit newInstance () {
        return new ReviewSubmit();
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.verify_photo, null);
        ButterKnife.bind(this, v);
        builder.setView(v);
        return builder.create();
    }

    @Override
    public void onCreate (Bundle savedBundleInstance) {
        hdSample = getArguments().getParcelable(Base.EXTRA_SAMPLE_DETAILS);
        super.onCreate(savedBundleInstance);
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        try {
            cameraResultListener = (CaptureFragment.OnCameraResultListener) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
