package sk.denis.davidek.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import sk.denis.davidek.popularmoviesstage2.R;
import sk.denis.davidek.popularmoviesstage2.data.Movie;
import sk.denis.davidek.popularmoviesstage2.interfacesall.OnItemClickListener;


/**
 * Created by denis on 19.05.2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private ArrayList<Movie> movies;
    private Context mContext;
    private final OnItemClickListener mClickListener;

    public MoviesAdapter(Context context, ArrayList<Movie> movies, OnItemClickListener clickListener) {
        this.mContext = context;
        this.movies = movies;
        this.mClickListener = clickListener;
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView moviePosterImageView;

        public MoviesViewHolder(View itemView) {
            super(itemView);

            moviePosterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mClickListener.onClick(clickedPosition);

        }
    }


    @Override
    public MoviesAdapter.MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToRoot = false;

        View movieView = inflater.inflate(R.layout.list_item, parent, shouldAttachToRoot);
        MoviesViewHolder viewHolder = new MoviesViewHolder(movieView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.MoviesViewHolder holder, int position) {

        String movieImageUrl = movies.get(position).getPosterUrl();
        Picasso.with(mContext)
                .load(movieImageUrl)
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder_error)
                .into(holder.moviePosterImageView);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
