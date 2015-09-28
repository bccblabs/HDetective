package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import carmera.io.wdetector.Base;
import carmera.io.wdetector.R;
import fr.tvbarthel.lib.blurdialogfragment.BlurDialogFragment;
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;
import models.HDSample;

/**
 * Created by bski on 7/29/15.
 */
public class SampleSavedDialog extends SupportBlurDialogFragment {

    public final String TAG = getClass().getCanonicalName();

    private HDSample hdSample;

    private Context context;

    private EditSaveResultsFragment.OnRetakePhotoCallback retakePhotoCallback;

    @Bind(R.id.date_text)
    TextView date_text;

    @Bind(R.id.serial_text)
    TextView serial_text;

    @Bind(R.id.exam_result_text)
    TextView exam_result_text;

    @Bind(R.id.sample_product_name)
    TextView product_name;

    @OnClick(R.id.done_button)
    public void reinit () {
        this.dismiss();
        retakePhotoCallback.retakePhoto();
    }

    public static SampleSavedDialog newInstance () {
        return new SampleSavedDialog();
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.save_result_dialog, null);
        ButterKnife.bind(this, v);
        date_text.setText(hdSample.getDate());
//        serial_text.setText(hdSample.getSerial_code());
//        product_name.setText(hdSample.getProduct_name());
//        exam_result_text.setText(hdSample.getLabel());
        builder.setView(v);
        return builder.create();
    }

    @Override
    public void onCreate (Bundle savedBundleInstance) {
        hdSample = Parcels.unwrap(getArguments().getParcelable(Base.EXTRA_SAMPLE_DETAILS));
        super.onCreate(savedBundleInstance);
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        context = (Context) activity;
        try {
            retakePhotoCallback = (EditSaveResultsFragment.OnRetakePhotoCallback) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected float getDownScaleFactor() {
        // Allow to customize the down scale factor.
        return (float) 5.0;
    }

    @Override
    protected int getBlurRadius() {
        // Allow to customize the blur radius factor.
        return 7;
    }

    @Override
    protected boolean isActionBarBlurred() {
        // Enable or disable the blur effect on the action bar.
        // Disabled by default.
        return true;
    }

    @Override
    protected boolean isDimmingEnable() {
        // Enable or disable the dimming effect.
        // Disabled by default.
        return true;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        // Enable or disable the use of RenderScript for blurring effect
        // Disabled by default.
        return true;
    }

    @Override
    protected boolean isDebugEnable() {
        // Enable or disable debug mode.
        // False by default.
        return false;
    }
}
