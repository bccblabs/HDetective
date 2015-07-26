package fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.gc.materialdesign.views.ButtonFloat;
import org.parceler.Parcels;
import butterknife.ButterKnife;
import carmera.io.wdetector.R;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

public class CaptureFragment extends SupportCameraFragment implements SeekBar.OnSeekBarChangeListener,
        ScreenShotable,
        View.OnTouchListener {

    public final String TAG = getClass().getCanonicalName();
    private Bitmap bitmap;
    private View containerView;
    private SeekBar zoom = null;
    private ButtonFloat capture_btn = null;
    private FrameLayout camera_preview;
    private OnCameraResultListener camera_result_callback = null;

    public interface OnCameraResultListener {
        public void OnCameraResult (Parcelable image_data);
    }

    public static CaptureFragment newInstance () {
        CaptureFragment captureFragment = new CaptureFragment();
        return captureFragment;
    }



    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        try {
            camera_result_callback = (OnCameraResultListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    ": needs to implement CameraResultListener" );
        }
    }

    @Override
    public void takeScreenShot () {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                        containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                CaptureFragment.this.bitmap = bitmap;
            }
        };

        thread.start();
    }

    @Override
    public Bitmap getBitmap() { return bitmap; }

    @Override
    public void onCreate (Bundle savedBundleInst) {
        super.onCreate(savedBundleInst);
        SimpleCameraHost.Builder builder = new SimpleCameraHost.Builder(new CaptureHost(getActivity()));
        setHost(builder.useFullBleedPreview(true).build());
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRetainInstance(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View cameraView = super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.capture, container, false);
        camera_preview = (FrameLayout) v.findViewById(R.id.camera_preview);

        ((ViewGroup)v.findViewById(R.id.camera_preview)).addView(cameraView);
        zoom = (SeekBar) v.findViewById(R.id.zoombar);
        capture_btn = (ButtonFloat) v.findViewById(R.id.capture_btn);

        zoom.setKeepScreenOn(true);
        capture_btn.setKeepScreenOn(true);
        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeSimplePicture ();
            }
        });

        ButterKnife.bind(this, v);
        camera_preview.setOnTouchListener(this);
        return v;
    }


    @Override
    public boolean onTouch (View v, MotionEvent e) {
        autoFocus();
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if (fromUser) {
            zoom.setEnabled(false);
            zoomTo(zoom.getProgress()).onComplete(new Runnable() {
                @Override
                public void run() {
                    zoom.setEnabled(true);
                }
            }).go();
        }
    }

    @Override
    public void onViewCreated (View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.camera_preview);
    }

    class CaptureHost extends SimpleCameraHost {
        public CaptureHost (Context cxt) {
            super (cxt);
        }

        @Override
        public void saveImage (PictureTransaction xact, byte[] image) {
            assert (image != null );
            Parcelable image_data = Parcels.wrap(byte[].class, image);
            camera_result_callback.OnCameraResult(image_data);
        }

        @Override
        public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
            if (doesZoomReallyWork() && parameters.getMaxZoom() > 0) {
                zoom.setMax(parameters.getMaxZoom());
                zoom.setOnSeekBarChangeListener(CaptureFragment.this);
            }
            else {
                zoom.setEnabled(false);
            }
            return(super.adjustPreviewParameters(parameters));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    void takeSimplePicture () {
        PictureTransaction xact = new PictureTransaction(getHost());
        takePicture(xact);
    }

    interface Contract {
        boolean isSingleShotMode();
        void setSingleShotMode (boolean mode);
    }

    Contract getContract() {
        return ((Contract) getActivity());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}


}