package sk.denis.davidek.popularmoviesstage2.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by denis on 19.05.2017.
 */

public class Movie implements Parcelable {

    private String id;
    private String originalTitle;
    private String posterUrl;
    private String plotSynopsis;
    private double userRating;
    private String releaseDate;
    private ArrayList<Review> reviews;

    protected Movie(Parcel in) {

        id = in.readString();
        originalTitle = in.readString();
        posterUrl = in.readString();
        plotSynopsis = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
        reviews = in.readArrayList(ClassLoader.getSystemClassLoader());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public Movie(String id, String originalTitle, String posterUrl, String plotSynopsis, double userRating, String releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterUrl = posterUrl;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.reviews = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(originalTitle);
        parcel.writeString(posterUrl);
        parcel.writeString(plotSynopsis);
        parcel.writeDouble(userRating);
        parcel.writeString(releaseDate);
        parcel.writeList(reviews);
    }
}
