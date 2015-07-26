package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import carmera.io.wdetector.R;
import models.Classification;
import widgets.SquareImageView;

/**
 * Created by bski on 7/26/15.
 */
public class ClassificationResultsAdapter extends BetterRecyclerAdapter<Classification, ClassificationResultsAdapter.ViewHolder> {

    private Context context;

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hd_class_item, parent, false);
        this.context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        Classification classification = getItem(i);
        switch (classification.getClass_name()) {
            case "Dell_2TB": {
                viewHolder.label_name.setText("Dell (Unlabeled Hardrive) 2 TB");
                Picasso.with(context).load(R.drawable.dell_2tb).fit().centerCrop().into(viewHolder.hd_label_image);
                break;
            }
            case "WD_Black_Enterprise_500Gb": {
                viewHolder.label_name.setText("Western Digital Enterprise Black 500 GB");
                Picasso.with(context).load(R.drawable.wd_blk_enterprice_500gb).fit().centerCrop().into(viewHolder.hd_label_image);
                break;
            }
            case "WD_Red_4.0TB": {
                viewHolder.label_name.setText("Western Digital Green 4 TB");
                Picasso.with(context).load(R.drawable.wd_green_4tb).fit().centerCrop().into(viewHolder.hd_label_image);
                break;
            }
            case "WD_Green_6.0TB": {
                viewHolder.label_name.setText("Western Digital Green 6 TB");
                Picasso.with(context).load(R.drawable.wd_green_6tb).fit().centerCrop().into(viewHolder.hd_label_image);
                break;
            }
            case "WD_Green_power": {
                viewHolder.label_name.setText("Western Digital Green Power");
                Picasso.with(context).load(R.drawable.wd_green_power).fit().centerCrop().into(viewHolder.hd_label_image);
                break;
            }
            case "WD_Red_6.0TB": {
                viewHolder.label_name.setText("Western Digital Red 6 TB");
                Picasso.with(context).load(R.drawable.wd_red_6tb).fit().centerCrop().into(viewHolder.hd_label_image);
                break;
            }
        }
        if (classification.getProb() > 0 )
            viewHolder.hd_label_prob.setText(String.format("%.2f", classification.getProb() * 100) + " %");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.hd_image)
        SquareImageView hd_label_image;

        @Bind(R.id.hd_label_name)
        public TextView label_name;

        @Bind(R.id.hd_label_prob)
        public TextView hd_label_prob;

        public ViewHolder (View itemView) {
            super (itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
