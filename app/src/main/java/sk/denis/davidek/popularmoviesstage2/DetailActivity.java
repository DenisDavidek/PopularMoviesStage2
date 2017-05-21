package sk.denis.davidek.popularmoviesstage2;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sk.denis.davidek.popularmoviesstage2.adapters.ReviewsAdapter;
import sk.denis.davidek.popularmoviesstage2.adapters.TrailersAdapter;
import sk.denis.davidek.popularmoviesstage2.data.Movie;
import sk.denis.davidek.popularmoviesstage2.data.Review;
import sk.denis.davidek.popularmoviesstage2.data.Trailer;
import sk.denis.davidek.popularmoviesstage2.data.contentprovider.MovieContract;
import sk.denis.davidek.popularmoviesstage2.databinding.ActivityDetailBinding;
import sk.denis.davidek.popularmoviesstage2.interfacesall.OnItemClickListener;
import sk.denis.davidek.popularmoviesstage2.utilities.NetworkUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Review>> {

    private static final String MOVIE_DEFAULT = "movie_default";
    private static final String MOVIE_VIDEOORREVIEW = "moview_videoorreview";

    private static final String MOVIE_QUERY_TEXT = "/movie/";
    private static final String REVIEW_QUERY_TEXT = "/reviews";
    private static final String TRAILER_QUERY_TEXT = "/videos";
    private int REVIEWS_GET_LOADER = 23;
    private int TRAILERS_GET_LOADER = 24;
    private int MOVIES_FAVORITES_GET_LOADER = 25;

    private Movie movie;
    private ActivityDetailBinding mBinding;

    private ArrayList<Trailer> trailers = new ArrayList<>();
    private boolean isFavoriteMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();

        String movieKey = getString(R.string.movie_key);
        if (intent.hasExtra(movieKey)) {

            movie = intent.getParcelableExtra(movieKey);
            String activityTitle = getString(R.string.movie_detail);
            actionBar.setTitle(activityTitle);

            if (movie != null)
                displayMovieDetails(movie);

            if (NetworkUtils.checkInternetConnection(this)) {
                initializeGetReviewsLoader(MOVIE_QUERY_TEXT, REVIEW_QUERY_TEXT);
                initializeGetTrailersLoader(MOVIE_QUERY_TEXT, TRAILER_QUERY_TEXT);
            } else {
                hideReviewsDataView(getString(R.string.no_internet_connection));
                hideTrailersDataView(getString(R.string.no_internet_connection));
            }


        }

        checkIfMovieIsFavorite();

        mBinding.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFavoriteMovie) {
                    Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(movie.getId()).build();
                    getContentResolver().delete(uri, null, null);

                    isFavoriteMovie = false;
                    checkIfMovieIsFavorite();
                    Snackbar.make(mBinding.btnFavorite, getString(R.string.movie_deleted_from_favorites), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.snackbar_action), null).show();

                } else {
                    downloadFile(movie.getPosterUrl());
                    insertFavoriteMovieIntoContentProvidersDatabase(movie, finalMoviePosterUri);
                    Snackbar.make(mBinding.btnFavorite, getString(R.string.movie_added_to_favorites), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.snackbar_action), null).show();
                    checkIfMovieIsFavorite();
                }

            }
        });

    }

    Uri finalMoviePosterUri;

    public void downloadFile(String uRl) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/PopularMoviesDownloadedPosters");

        if (!direct.exists()) {
            direct.mkdirs();
        }
        File testIfExists = new File(direct + File.separator + movie.getOriginalTitle() + ".jpg");

        if (!testIfExists.exists()) {

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            // Toast.makeText(getApplicationContext(), "IMAGE DOWNLOADING", Toast.LENGTH_LONG).show();
            Uri downloadUri = Uri.parse(uRl);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Demo")
                    .setDescription("Something useful. No, really.")
                    .setDestinationInExternalPublicDir("/PopularMoviesDownloadedPosters", movie.getOriginalTitle() + ".jpg");

            manager.enqueue(request);

        }
        Uri moviePosterUri = Uri.parse(direct + File.separator + movie.getOriginalTitle() + ".jpg");
        finalMoviePosterUri = Uri.parse("file://" + moviePosterUri);

    }

    public void insertFavoriteMovieIntoContentProvidersDatabase(Movie movie, Uri finalUri) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URI, finalUri.toString());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getPlotSynopsis());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getUserRating());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());


        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        if (uri != null) {
            // Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private void displayMovieDetails(Movie movie) {

        String originalTitle = movie.getOriginalTitle();
        mBinding.tvMovieTitle.setText(originalTitle);

        String posterUrl = movie.getPosterUrl();

        if (posterUrl.startsWith("file://")) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(posterUrl));
                mBinding.ivMoviePoster.setImageBitmap(bitmap);
                //Toast.makeText(getApplicationContext(), "URI ", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Picasso.with(this).load(posterUrl).into(mBinding.ivMoviePoster);
            //Toast.makeText(getApplicationContext(), "PICASSO ", Toast.LENGTH_LONG).show();
        }

        String plotSynopsis = movie.getPlotSynopsis();
        mBinding.tvMoviePlotSynopsis.setText(plotSynopsis);

        double userRating = movie.getUserRating();
        mBinding.tvMovieUserRating.setText(String.valueOf(userRating));

        String releaseDate = movie.getReleaseDate();

        SimpleDateFormat simpleDateFormatInput = new SimpleDateFormat("yyy-MM-dd");
        SimpleDateFormat simpleDateFormatOutput = new SimpleDateFormat("dd.MM.yyyy");


        String test;
        try {

            Date date = simpleDateFormatInput.parse(releaseDate);
            test = simpleDateFormatOutput.format(date);

            mBinding.tvMovieReleaseDate.setText(test);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void watchYoutubeVideo(String id) {

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));

        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException e) {
            startActivity(webIntent);
        }
    }

    private void initializeGetReviewsLoader(String movie, String videoOrReview) {

        Bundle argsBundle = new Bundle();
        argsBundle.putString(MOVIE_DEFAULT, movie);
        argsBundle.putString(MOVIE_VIDEOORREVIEW, videoOrReview);

        android.support.v4.app.LoaderManager loaderManager = this.getSupportLoaderManager();
        android.support.v4.content.Loader<ArrayList<Review>> getMoviesLoader = loaderManager.getLoader(REVIEWS_GET_LOADER);

        if (getMoviesLoader == null) {
            loaderManager.initLoader(REVIEWS_GET_LOADER, argsBundle, this);
        } else
            loaderManager.restartLoader(REVIEWS_GET_LOADER, argsBundle, this);

    }

    private void initializeGetTrailersLoader(String movie, String videoOrReview) {

        Bundle argsBundle = new Bundle();
        argsBundle.putString(MOVIE_DEFAULT, movie);
        argsBundle.putString(MOVIE_VIDEOORREVIEW, videoOrReview);

        android.support.v4.app.LoaderManager loaderManager = this.getSupportLoaderManager();
        android.support.v4.content.Loader<ArrayList<Trailer>> getMoviesLoader = loaderManager.getLoader(TRAILERS_GET_LOADER);

        if (getMoviesLoader == null) {
            loaderManager.initLoader(TRAILERS_GET_LOADER, argsBundle, new CallbackVideos());
        } else
            loaderManager.restartLoader(TRAILERS_GET_LOADER, argsBundle, new CallbackVideos());

    }


    public void showTrailersDataView() {

        mBinding.rvMovieTrailers.setVisibility(View.VISIBLE);
        mBinding.tvNoTrailersText.setVisibility(View.INVISIBLE);
    }

    public void hideTrailersDataView(String message) {

        mBinding.rvMovieTrailers.setVisibility(View.INVISIBLE);
        mBinding.tvNoTrailersText.setVisibility(View.VISIBLE);
        mBinding.tvNoTrailersText.setText(message);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.trailersSectionDivider.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.tv_no_trailers_text);
    }

    public void showReviewsDataView() {

        mBinding.rvMovieReviews.setVisibility(View.VISIBLE);
        mBinding.tvNoReviewsText.setVisibility(View.INVISIBLE);
    }

    public void hideReviewsDataView(String message) {

        mBinding.rvMovieReviews.setVisibility(View.INVISIBLE);
        mBinding.tvNoReviewsText.setVisibility(View.VISIBLE);
        mBinding.tvNoReviewsText.setText(message);
    }


    // CallbackReviews

    @Override
    public Loader<ArrayList<Review>> onCreateLoader(int id, final Bundle args) {
        return new android.support.v4.content.AsyncTaskLoader<ArrayList<Review>>(this) {

            ArrayList<Review> reviews = new ArrayList<>();

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public ArrayList<Review> loadInBackground() {

                String movieDefault = args.getString(MOVIE_DEFAULT);

                String videoOrReview = args.getString(MOVIE_VIDEOORREVIEW);

                final String apiKey = BuildConfig.API_KEY;

                URL reviewRequestUrl = NetworkUtils.buildReviewsAndVideosUrl(apiKey, movieDefault, movie.getId(), videoOrReview);
                String responseJSONString = NetworkUtils.getResponseFromHttp(reviewRequestUrl);

                try {
                    JSONObject parentJSONObject = new JSONObject(responseJSONString);
                    JSONArray reviewsArray = parentJSONObject.getJSONArray("results");

                    if (reviewsArray.length() != 0) {

                        for (int i = 0; i < reviewsArray.length(); i++) {

                            JSONObject childMoviewObject = reviewsArray.getJSONObject(i);

                            String author = childMoviewObject.getString("author");
                            String content = childMoviewObject.getString("content");

                            reviews.add(new Review(author, content));

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return reviews;
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<ArrayList<Review>> loader, ArrayList<Review> data) {

        if (!data.isEmpty()) {
            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(this, data);
            showReviewsDataView();
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mBinding.rvMovieReviews.setLayoutManager(layoutManager);
            mBinding.rvMovieReviews.setAdapter(reviewsAdapter);
            mBinding.rvMovieReviews.setHasFixedSize(true);
        } else {

            hideReviewsDataView(getString(R.string.movie_no_review));
        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ArrayList<Review>> loader) {

    }


    //Callback Videos Loader
    private class CallbackVideos implements LoaderManager.LoaderCallbacks<ArrayList<Trailer>>, OnItemClickListener {
        @Override
        public Loader<ArrayList<Trailer>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList<Trailer>>(getApplicationContext()) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }

                @Override
                public ArrayList<Trailer> loadInBackground() {

                    String movieDefault = args.getString(MOVIE_DEFAULT);

                    String videoOrReview = args.getString(MOVIE_VIDEOORREVIEW);

                    final String apiKey = BuildConfig.API_KEY;

                    URL trailerRequestUrl = NetworkUtils.buildReviewsAndVideosUrl(apiKey, movieDefault, movie.getId(), videoOrReview);
                    String responseJSONString = NetworkUtils.getResponseFromHttp(trailerRequestUrl);

                    try {
                        JSONObject parentJSONObject = new JSONObject(responseJSONString);
                        JSONArray trailersArray = parentJSONObject.getJSONArray("results");

                        if (trailersArray.length() != 0) {

                            for (int i = 0; i < trailersArray.length(); i++) {

                                JSONObject childMoviewObject = trailersArray.getJSONObject(i);

                                String name = childMoviewObject.getString("name");
                                String key = childMoviewObject.getString("key");

                                trailers.add(new Trailer(name, key));

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return trailers;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Trailer>> loader, ArrayList<Trailer> data) {

            if (!data.isEmpty()) {
                TrailersAdapter reviewsAdapter = new TrailersAdapter(getApplicationContext(), data, this);
                showTrailersDataView();
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                mBinding.rvMovieTrailers.setLayoutManager(layoutManager);
                mBinding.rvMovieTrailers.setAdapter(reviewsAdapter);
                mBinding.rvMovieTrailers.setHasFixedSize(true);
            } else {
                hideTrailersDataView(getString(R.string.movie_no_trailer));
            }


        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Trailer>> loader) {

        }

        @Override
        public void onClick(int position) {
            watchYoutubeVideo(trailers.get(position).getKey());
        }
    }


    //content provider

    private Cursor moviesCursorData;

    private class CallbackQuery implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(getApplicationContext()) {

                Cursor mMoviesData = null;

                @Override
                protected void onStartLoading() {
                    if (mMoviesData != null) {
                        deliverResult(mMoviesData);
                    } else forceLoad();
                }

                @Override
                public Cursor loadInBackground() {

                    try {
                        return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(Cursor data) {
                    mMoviesData = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            moviesCursorData = data;


            while (data.moveToNext()) {

                String movieId = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));

                if (movieId.equals(movie.getId())) {
                    mBinding.btnFavorite.setText(getString(R.string.button_favorite_unfavortext));
                    isFavoriteMovie = true;

                }
            }
            if (!isFavoriteMovie) {
                mBinding.btnFavorite.setText(getString(R.string.button_favorite_text));
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }


    public void checkIfMovieIsFavorite() {
        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();
        android.support.v4.content.Loader<Cursor> getCusorLoader = loaderManager.getLoader(MOVIES_FAVORITES_GET_LOADER);

        if (getCusorLoader == null) {
            loaderManager.initLoader(MOVIES_FAVORITES_GET_LOADER, null, new CallbackQuery());
        } else
            loaderManager.restartLoader(MOVIES_FAVORITES_GET_LOADER, null, new CallbackQuery());
    }

}// end of class
