package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import Util.Util;
import butterknife.Bind;
import butterknife.ButterKnife;
import carmera.io.wdetector.R;
import models.HDSampleParse;
import widgets.SquareImageView;

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
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd/hh:mm");
        viewHolder.date.setText(df.format(captured_sample.getCreatedAt()));
        viewHolder.sample_serial_code.setText(captured_sample.getSerialCode());
        viewHolder.classified_labels.setText(captured_sample.getClassifiedLabel());
        viewHolder.sample_serial_code.setText(captured_sample.getSerialCode());
        viewHolder.product_name.setText(captured_sample.getProductName());

        Picasso.with(context).load(captured_sample.getHDPhoto().getUrl())
                             .placeholder(R.drawable.hard_disk_holder)
                             .fit().centerCrop()
                             .into(viewHolder.sample_image);


    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.sample_image)
        public ImageView sample_image;

        @Bind(R.id.date_text)
        public TextView date;

        @Bind(R.id.serial_text)
        public TextView sample_serial_code;

        @Bind(R.id.exam_result_text)
        public TextView classified_labels;

        @Bind(R.id.sample_product_name)
        public TextView product_name;

        @Bind(R.id.certainty_text)
        public TextView certainty;


        public ViewHolder (View itemView) {
            super (itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
