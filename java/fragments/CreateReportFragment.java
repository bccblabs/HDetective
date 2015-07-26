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
import android.view.WindowManager;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import adapters.BetterRecyclerAdapter;
import adapters.ReportResultsAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import carmera.io.wdetector.R;
import models.Classification;
import models.HDSample;
import models.HDSampleParse;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Created by bski on 7/25/15.
 */
public class CreateReportFragment extends Fragment implements ScreenShotable {

    @Bind(R.id.create_report_container)
    View containerView;

    @Bind(R.id.unreported_samples_recycler)
    RecyclerView samples_recycler;


    @OnClick(R.id.report)
    public void reportSamples () {
        for (HDSampleParse sample : reportResultsAdapter.getItems()) {
            sample.setReported(true);
            sample.saveEventually();
        }
        Toast.makeText(context, String.format(" %d samples reported!", reportResultsAdapter.getItemCount()), Toast.LENGTH_SHORT).show();
//        loadUnreportedItems();
    }

    private ReportResultsAdapter reportResultsAdapter;
    private SaveResultFragment saveResultFragment;
    public final String TAG = getClass().getCanonicalName();
    private Bitmap bitmap;
    private Context context;

    public static CreateReportFragment newInstance () {
        return new CreateReportFragment();
    }

    private void loadUnreportedItems() {
        ParseQuery<HDSampleParse> unreported_samples_query = ParseQuery.getQuery("HDSample");
        unreported_samples_query.whereEqualTo("reported", false);
        unreported_samples_query.findInBackground(unreported_query_callback);

    }

    private FindCallback unreported_query_callback = new FindCallback<HDSampleParse>() {
        @Override
        public void done(List<HDSampleParse> unreported_samples, ParseException e) {
            if (e != null) {
                Log.e(TAG, e.getMessage());
            } else {
                reportResultsAdapter.clear();
                reportResultsAdapter.addAll(unreported_samples);
                reportResultsAdapter.notifyDataSetChanged();
                Toast.makeText(context, String.format(" %d samples unreported!", reportResultsAdapter.getItemCount()), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void takeScreenShot () {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                        containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                CreateReportFragment.this.bitmap = bitmap;
            }
        };

        thread.start();
    }

    @Override
    public Bitmap getBitmap() { return bitmap; }


    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate (R.layout.create_report, container, false);
        ButterKnife.bind(this, v);
        reportResultsAdapter = new ReportResultsAdapter();
        samples_recycler.setAdapter(reportResultsAdapter);
        samples_recycler.setLayoutManager(new LinearLayoutManager(context));
        samples_recycler.setHasFixedSize(true);
        reportResultsAdapter.setOnItemClickListener(new BetterRecyclerAdapter.OnItemClickListener<HDSampleParse>() {
            @Override
            public void onItemClick(View v, HDSampleParse item, int position) {
                HDSample sample = new HDSample();
                sample.setCustomer_input_label(item.getTargetLabel());
                sample.setParse_id(item.getObjectId());
                sample.setDate(item.getCreatedAt().toString());
                List<Classification> classifications = new ArrayList<Classification>();
                for (String classificationLabel : item.getClassifiedLabels()) {
                    Classification classification = new Classification();
                    classification.setClass_name(classificationLabel);
                    classification.setProb(-1.0);
                    classifications.add (classification);
                }
                sample.setClassifications(classifications);
                Bundle args = new Bundle();
                args.putParcelable(SaveResultFragment.EXTRA_SAMPLE_DETAILS, Parcels.wrap(HDSample.class, sample));
                saveResultFragment = SaveResultFragment.newInstance(true);
                saveResultFragment.setArguments(args);
                saveResultFragment.show(getChildFragmentManager(), "edit_and_save");
            }
        });
        loadUnreportedItems();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
