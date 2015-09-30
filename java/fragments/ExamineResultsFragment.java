package fragments;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Date;

import Util.Constants;
import adapters.SamplesAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import carmera.io.wdetector.InMemorySpiceService;
import carmera.io.wdetector.R;
import models.HDSampleList;
import models.SampleQuery;
import requests.SamplesRequest;

/**
 * Created by bski on 9/28/15.
 */
public class ExamineResultsFragment extends Fragment
                                    implements SwipeRefreshLayout.OnRefreshListener,
                                               OnMoreListener {

    private final String TAG = getClass().getCanonicalName();
    @Bind(R.id.classifications)
    SuperRecyclerView clzRecycler;

    private SamplesAdapter samplesAdapter;
    private SpiceManager spiceManager = new SpiceManager(InMemorySpiceService.class);

    private final class SamplesReqListener implements RequestListener<HDSampleList> {
        @Override
        public void onRequestFailure (SpiceException spiceException) {
            Toast.makeText(getActivity(), "Error: " + spiceException.getMessage(), Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onRequestSuccess (HDSampleList result) {
            try {
                samplesAdapter.addAll(result.getSamples());
                samplesAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Listings Adapter length: " + samplesAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public static ExamineResultsFragment newInstance () {
        ExamineResultsFragment examineResultsFragment = new ExamineResultsFragment();
        return examineResultsFragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate (R.layout.examine_results, container, false);
        ButterKnife.bind(this, v);
        clzRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        clzRecycler.setRefreshListener(this);
        clzRecycler.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        clzRecycler.setupMoreListener(this, Constants.MAX_RELOAD);
        samplesAdapter = new SamplesAdapter();
        clzRecycler.setAdapter(samplesAdapter);
        return v;
    }

    @Override
    public void onViewCreated (View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        SampleQuery sampleQuery = new SampleQuery();
        SamplesRequest samplesRequest = new SamplesRequest(sampleQuery);
        spiceManager.execute (samplesRequest, new Date().hashCode(), DurationInMillis.ALWAYS_RETURNED, new SamplesReqListener());
    }

    @Override
    public void onStart () {
        super.onStart();
        spiceManager.start (getActivity());
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

    @Override
    public void onRefresh() {
    }

    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
    }
}
