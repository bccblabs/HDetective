package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import carmera.io.wdetector.Base;
import carmera.io.wdetector.R;
import com.parse.ParseQuery;
import org.parceler.Parcels;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;
import models.HDSample;
import models.HDSampleParse;

/**
 * Created by bski on 7/26/15.
 */
public class SaveResultFragment extends SupportBlurDialogFragment {

    public final String TAG = getClass().getCanonicalName();

    private Context context;
    private HDSample hdSample;
    private HDSampleParse hdSampleParse;
    private EditSaveResultsFragment.OnRetakePhotoCallback retakePhotoCallback;
    private static boolean is_report = true;

    @Bind(R.id.date_text)
    TextView date;

    @Bind(R.id.serial_text)
    TextView serial;

    @Bind(R.id.exam_result_text)
    TextView exam_result;

    @Bind(R.id.certainty_text)
    TextView certainty;

    @OnClick(R.id.done_button)
    public void onDone () {
        ParseQuery<HDSampleParse> query = ParseQuery.getQuery("HDSample");
        try {
            hdSampleParse = query.get(hdSample.getParse_id());
            hdSampleParse.setClassifiedLabel(hdSample.getLabel());
            hdSampleParse.setClassificationProb(hdSample.getProb());
            hdSampleParse.setReported(false);
        } catch (com.parse.ParseException e) {
            Log.e (TAG, e.getMessage());
        }
        hdSampleParse.saveEventually();
        this.dismiss();
        if (!is_report)
            retakePhotoCallback.retakePhoto();
    }

    public static SaveResultFragment newInstance(boolean is_report_) {
        is_report = is_report_;
        return new SaveResultFragment();
    }

    @Override
    public void onCreate (Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        hdSample = Parcels.unwrap(getArguments().getParcelable(Base.EXTRA_SAMPLE_DETAILS));
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        this.context = getActivity();
        if (!is_report) {
            try {
                retakePhotoCallback = (EditSaveResultsFragment.OnRetakePhotoCallback) activity;
            } catch (ClassCastException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public Dialog onCreateDialog (Bundle savedBundleInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getActivity().getLayoutInflater().inflate(R.layout.save_result_dialog, null);
        builder.setView(view);
        ButterKnife.bind(this, view);
        date.setText(hdSample.getDate());
        serial.setText(hdSample.getSerial_code());
        exam_result.setText(hdSample.getLabel());
        certainty.setText(String.format("%.2f %%", hdSample.getProb()));
        return builder.create();
    }


    @Override
    protected float getDownScaleFactor() {
        return 5.0f;
    }

    @Override
    protected int getBlurRadius() {
        return 10;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return true;
    }

    @Override
    protected boolean isDimmingEnable() {
        return true;
    }
}
