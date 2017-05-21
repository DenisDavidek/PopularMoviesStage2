package sk.denis.davidek.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

import sk.denis.davidek.popularmoviesstage2.R;
import sk.denis.davidek.popularmoviesstage2.data.Review;

/**
 * Created by denis on 19.05.2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private ArrayList<Review> reviews;
    private Context mContext;

    public ReviewsAdapter(Context context, ArrayList<Review> reviews) {
        this.mContext = context;
        this.reviews = reviews;
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder {

        private TextView reviewAuthorTextView;
        private TextView reviewTextView;
        private View dividerView;

        public ReviewsViewHolder(View itemView) {
            super(itemView);

            reviewAuthorTextView = (TextView) itemView.findViewById(R.id.tv_author);
            reviewTextView = (TextView) itemView.findViewById(R.id.tv_review);
            dividerView = itemView.findViewById(R.id.review_divider);
        }
    }


    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToRoot = false;

        View movieView = inflater.inflate(R.layout.list_item_reviews, parent, shouldAttachToRoot);
        ReviewsViewHolder viewHolder = new ReviewsViewHolder(movieView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {

        String reviewAuthor = reviews.get(position).getAuthor();
        holder.reviewAuthorTextView.setText(reviewAuthor);

        String reviewText = reviews.get(position).getContent();
        holder.reviewTextView.setText(reviewText);

        if (reviews.size() - 1 == position) {
            holder.dividerView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}