package fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.gc.materialdesign.views.ButtonFloat;
import org.parceler.Parcels;
import butterknife.Bind;
import butterknife.ButterKnife;
import carmera.io.wdetector.Base;
import carmera.io.wdetector.R;
import models.HDSample;

public class CaptureFragment extends SupportCameraFragment implements View.OnTouchListener {

    private ButtonFloat capture_btn;
    private FrameLayout camera_preview;
    private HDSample hdSample;
    private ReviewSubmit reviewSubmitDialog;
    @Bind(R.id.label_in_scan)
    TextView label;

    public interface OnCameraResultListener {
        public void OnCameraResult (Parcelable sample_details);
    }

    public static CaptureFragment newInstance () {
        CaptureFragment captureFragment = new CaptureFragment();
        return captureFragment;
    }

    @Override
    public void onCreate (Bundle savedBundleInst) {
        super.onCreate(savedBundleInst);
        hdSample = Parcels.unwrap(getArguments().getParcelable(Base.EXTRA_SAMPLE_DETAILS));
        SimpleCameraHost.Builder builder = new SimpleCameraHost.Builder(new CaptureHost(getActivity()));
        setHost(builder.useFullBleedPreview(true).build());
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View cameraView = super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.capture, container, false);
        camera_preview = (FrameLayout) v.findViewById(R.id.camera_preview);
        ((ViewGroup)v.findViewById(R.id.camera_preview)).addView(cameraView);
        capture_btn = (ButtonFloat) v.findViewById(R.id.capture_btn);
        capture_btn.setKeepScreenOn(true);
        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeSimplePicture ();
            }
        });
        ButterKnife.bind(this, v);
        label.setText(hdSample.getUserHddLabel());
        camera_preview.setOnTouchListener(this);
        return v;
    }

    @Override
    public boolean onTouch (View v, MotionEvent e) {
        autoFocus();
        return true;
    }

    @Override
    public void onViewCreated (View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    class CaptureHost extends SimpleCameraHost {
        public CaptureHost (Context cxt) {
            super (cxt);
        }
        @Override
        public void saveImage (PictureTransaction xact, byte[] image) {
            assert (image != null );
            hdSample.setImageData(Base64.encodeToString(image, Base64.DEFAULT));
            Parcelable hd_sample_pclb = Parcels.wrap(HDSample.class, hdSample);
            Bundle args = new Bundle();
            args.putParcelable(Base.EXTRA_SAMPLE_DETAILS, hd_sample_pclb);
            reviewSubmitDialog = ReviewSubmit.newInstance();
            reviewSubmitDialog.setArguments(args);
            reviewSubmitDialog.show(getChildFragmentManager(), "saved");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    void takeSimplePicture () {
        PictureTransaction xact = new PictureTransaction(getCameraHost());
        takePicture(xact);
    }
}