package fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rengwuxian.materialedittext.MaterialEditText;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import carmera.io.wdetector.R;
import models.HDSample;
import org.parceler.Parcels;

/**
 * Created by bski on 7/28/15.
 */
public class InitFragment extends Fragment implements ScreenShotable {

    public final String TAG = getClass().getCanonicalName();
    private StartCaptureListener startCaptureListener;
    private HDSample hdSample;
    private Bitmap bitmap;

    public interface StartCaptureListener {
        public void OnStartCapture(Parcelable hd_sample);
    }

    public static InitFragment newInstance () {
        return new InitFragment();
    }

    @Bind(R.id.serial_code_input)
    MaterialEditText serial_code_input;

    @OnClick(R.id.init_capture)
    public void init_capture() {
        hdSample.setSerial_code(serial_code_input.getText().toString());
        Parcelable hd_sample_extra = Parcels.wrap(HDSample.class, hdSample);
        startCaptureListener.OnStartCapture(hd_sample_extra);
    }

    @Override
    public void onCreate (Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        hdSample = new HDSample();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.init_capture, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        try {
            startCaptureListener = (StartCaptureListener) activity;
        } catch (ClassCastException e ) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void takeScreenShot () {
    }

    @Override
    public Bitmap getBitmap() { return bitmap; }

}
