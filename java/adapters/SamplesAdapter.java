package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import carmera.io.wdetector.R;
import models.HDSample;
import widgets.CanaroTextView;
import widgets.SquareImageView;

/**
 * Created by bski on 9/29/15.
 */
public class SamplesAdapter extends BetterRecyclerAdapter<HDSample, SamplesAdapter.ViewHolder> {


    public String TAG = getClass().getCanonicalName();
    private Context cxt;

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_item, parent, false);
        this.cxt = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        try {
            HDSample sample = getItem(i);
            viewHolder.barcode.setText(sample.getBarcode());
            viewHolder.date.setText(sample.getDate());
            viewHolder.status.setText(sample.getClzRes());
            Picasso.with(cxt)
                    .load(sample.getS3Url())
                    .centerCrop()
                    .fit()
                    .into(viewHolder.hdd_image);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.hdd_image)
        public SquareImageView hdd_image;
        @Bind(R.id.barcode)
        public CanaroTextView barcode;
        @Bind(R.id.date)
        public CanaroTextView date;
        @Bind(R.id.status)
        public CanaroTextView status;
        public ViewHolder (View itemView) {
            super (itemView);
            ButterKnife.bind (this, itemView);
        }
    }

}
