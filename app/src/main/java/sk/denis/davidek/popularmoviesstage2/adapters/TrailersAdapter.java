package sk.denis.davidek.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import sk.denis.davidek.popularmoviesstage2.R;

import sk.denis.davidek.popularmoviesstage2.data.Trailer;
import sk.denis.davidek.popularmoviesstage2.interfacesall.OnItemClickListener;

/**
 * Created by denis on 20.05.2017.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    private ArrayList<Trailer> trailers;
    private Context mContext;
    private OnItemClickListener itemClickListener;

    public TrailersAdapter(Context context, ArrayList<Trailer> trailers, OnItemClickListener clickListener) {
        this.mContext = context;
        this.trailers = trailers;
        this.itemClickListener = clickListener;
    }

    public class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView trailerNameTextView;
        private View dividerView;


        public TrailersViewHolder(View itemView) {
            super(itemView);

            trailerNameTextView = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            dividerView = itemView.findViewById(R.id.trailer_divider);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            itemClickListener.onClick(position);
        }
    }


    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToRoot = false;

        View movieView = inflater.inflate(R.layout.list_item_trailers, parent, shouldAttachToRoot);
        TrailersViewHolder viewHolder = new TrailersViewHolder(movieView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailersViewHolder holder, int position) {

        String trailerName = trailers.get(position).getName();
        holder.trailerNameTextView.setText(trailerName);

        // hide divider when it is the last item.
        if (trailers.size() - 1 == position) {
            holder.dividerView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }
}