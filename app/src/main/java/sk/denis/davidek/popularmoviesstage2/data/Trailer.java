package sk.denis.davidek.popularmoviesstage2.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by denis on 20.05.2017.
 */

public class Trailer implements Parcelable {

    private String name;
    private String key;

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }


    public Trailer(String name, String key) {
        this.name = name;
        this.key = key;
    }

    protected Trailer(Parcel in) {
        name = in.readString();
        key = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(key);
    }
}
