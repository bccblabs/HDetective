package fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import Util.Util;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import carmera.io.wdetector.R;
import models.HDSample;
import org.parceler.Parcels;

import java.util.Arrays;

/**
 * Created by bski on 7/28/15.
 */
public class InitFragment extends Fragment implements ScreenShotable,
                                                      AdapterView.OnItemSelectedListener{

    public final String TAG = getClass().getCanonicalName();
    private StartCaptureListener startCaptureListener;
    private HDSample hdSample;
    private Shimmer shimmer = new Shimmer();

    @Bind (R.id.manual_enter)
    Spinner manual_entry;


    @Bind (R.id.desc_text)
    ShimmerTextView desc_text;

    @Bind(R.id.manual_enter_text)
    ShimmerTextView manual_enter_text;

    public interface StartCaptureListener {
        public void OnStartCapture(Parcelable hd_sample);
    }

    public static InitFragment newInstance () {
        return new InitFragment();
    }

    @Bind(R.id.serial_code_input)
    MaterialAutoCompleteTextView serial_code_input;

    @OnClick(R.id.init_capture)
    public void init_capture() {
        String serial_code = serial_code_input.getText().toString().replace("\n", "").replace("\r", "");
        hdSample.setSerial_code(serial_code);
        hdSample.setProduct_name(Util.getSampleProductName(serial_code));
        Parcelable hd_sample_extra = Parcels.wrap(HDSample.class, hdSample);
        startCaptureListener.OnStartCapture(hd_sample_extra);
    }

    @Override
    public void onCreate (Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        hdSample = new HDSample();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.init_capture, container, false);
        ButterKnife.bind(this, v);
        shimmer.setDuration(2000);
        shimmer.start(desc_text);
        shimmer.start (manual_enter_text);
        serial_code_input.setAdapter(new ArrayAdapter<String> ( getActivity(),
                                                                android.R.layout.simple_dropdown_item_1line,
                                                                getActivity().getResources().getStringArray(R.array.serial_number_array)));
        serial_code_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String serial_input = s.toString();
                if (serial_input.contains("\n") || serial_input.contains("\r")) {
                    String input_string = serial_input.replace("\n", "").replace("\r", "");
                    hdSample.setSerial_code(input_string);
                    hdSample.setProduct_name(Util.getSampleProductName(input_string));
                    Parcelable hd_sample_extra = Parcels.wrap(HDSample.class, hdSample);
                    startCaptureListener.OnStartCapture(hd_sample_extra);
                }
            }
        });

        ArrayAdapter<CharSequence> models = ArrayAdapter.createFromResource(getActivity(),
                R.array.serial_number_array,
                R.layout.textview_for_spinner);
        models.setDropDownViewResource(R.layout.textview_for_spinner);
        manual_entry.setAdapter(models);
        manual_entry.setOnItemSelectedListener(this);

        return v;
    }

    @Override
    public void onItemSelected (AdapterView<?> parent, View view, int pos, long id) {
        if (pos > 0) {
            String input_string = parent.getItemAtPosition(pos).toString();
            Log.i(TAG, input_string);
            hdSample.setSerial_code(input_string);
            hdSample.setProduct_name(Util.getSampleProductName(input_string));
            Parcelable hd_sample_extra = Parcels.wrap(HDSample.class, hdSample);
            startCaptureListener.OnStartCapture(hd_sample_extra);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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
    public Bitmap getBitmap() { return null; }

}
