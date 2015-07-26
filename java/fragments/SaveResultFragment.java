package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import carmera.io.wdetector.R;

import com.parse.ParseQuery;

import org.parceler.Parcels;
import java.util.ArrayList;
import java.util.List;

import adapters.ClassificationResultsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ganfra.materialspinner.MaterialSpinner;
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;
import models.Classification;
import models.HDSample;
import models.HDSampleParse;

/**
 * Created by bski on 7/26/15.
 */
public class SaveResultFragment extends SupportBlurDialogFragment implements AdapterView.OnItemSelectedListener {

    public static final String EXTRA_SAMPLE_DETAILS = "extra_sample_details";
    public final String TAG = getClass().getCanonicalName();

    private Context context;
    private HDSample hdSample;
    private HDSampleParse hdSampleParse;
    private ClassificationResultsAdapter classificationResultsAdapter;
    private EditSaveResultsFragment.OnRetakePhotoCallback retakePhotoCallback;
    private static boolean is_report = true;
    @Bind(R.id.classifications_recycler)
    RecyclerView classifications_recycler;


    @Bind(R.id.correct_input_spinner)
    MaterialSpinner correct_input_spinner;

    @Bind(R.id.date_view)
    TextView date_view;

    @OnClick(R.id.done_button)
    public void onDone () {
        ParseQuery<HDSampleParse> query = ParseQuery.getQuery("HDSample");
        try {
            hdSampleParse = query.get(hdSample.getParse_id());
            hdSampleParse.setTargetLabel(hdSample.getCustomer_input_label());
            hdSampleParse.setReported(false);
            List<String> labels = new ArrayList<>();
            for (Classification classification : hdSample.getClassifications()) {
                labels.add(classification.getClass_name());
            }
            hdSampleParse.setClassifiedLabels(labels);
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
        hdSample = Parcels.unwrap(getArguments().getParcelable(EXTRA_SAMPLE_DETAILS));
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
        classificationResultsAdapter = new ClassificationResultsAdapter();
        classifications_recycler.setAdapter(classificationResultsAdapter);
        classifications_recycler.setLayoutManager(new LinearLayoutManager(context));
        classifications_recycler.setHasFixedSize(true);
        classificationResultsAdapter.addAll(hdSample.getClassifications());
        classificationResultsAdapter.notifyDataSetChanged();
        date_view.setText(hdSample.getDate());
        ArrayAdapter<String> hd_choice_adapter = new ArrayAdapter<String>(context,
                                                                          android.R.layout.simple_spinner_item,
                                                                          context.getResources().getStringArray(R.array.hd_name_array));
        hd_choice_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        correct_input_spinner.setAdapter(hd_choice_adapter);
        correct_input_spinner.setOnItemSelectedListener(this);
        String sampleCustomerLabel = hdSample.getCustomer_input_label();
        if (sampleCustomerLabel != null) {
            int spinnerPos = hd_choice_adapter.getPosition(sampleCustomerLabel);
            correct_input_spinner.setSelection (spinnerPos);
        }
        return builder.create();
    }


    @Override
    protected float getDownScaleFactor() {
        // Allow to customize the down scale factor.
        return 5.0f;
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
    public void onItemSelected (AdapterView<?> parent, View view, int pos, long id) {
        if (parent.getItemAtPosition(pos) != null)
            hdSample.setCustomer_input_label(parent.getItemAtPosition(pos).toString());
    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
