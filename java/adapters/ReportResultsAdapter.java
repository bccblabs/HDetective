package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import Util.Util;
import butterknife.Bind;
import butterknife.ButterKnife;
import carmera.io.wdetector.R;
import models.HDSampleParse;

/**
 * Created by bski on 7/26/15.
 */
public class ReportResultsAdapter extends BetterRecyclerAdapter<HDSampleParse, ReportResultsAdapter.ViewHolder> {

    private Context context;

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_item, parent, false);
        this.context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        HDSampleParse captured_sample = getItem(i);
        viewHolder.captured_date.setText(captured_sample.getCreatedAt().toString());
        viewHolder.classified_labels.setText(Util.joinStrings(captured_sample.getClassifiedLabels(), ", "));
        Picasso.with(context).load(captured_sample.getHDPhoto().getUrl())
                             .placeholder(R.drawable.hard_disk_holder)
                             .fit().centerCrop()
                             .into(viewHolder.sample_image);


    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.sample_image)
        public SquareImageView sample_image;

        @Bind(R.id.captured_date)
        public TextView captured_date;

        @Bind(R.id.classified_labels)
        public TextView classified_labels;

        public ViewHolder (View itemView) {
            super (itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
