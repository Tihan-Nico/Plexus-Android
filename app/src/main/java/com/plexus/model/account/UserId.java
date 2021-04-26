package com.plexus.model.account;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;

import com.plexus.utils.DelimiterUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserId {

    private static final long UNKNOWN_ID = -1;
    private static final char DELIMITER  = ',';

    public static final UserId UNKNOWN = UserId.from(String.valueOf(UNKNOWN_ID));

    private final String id;

    public static UserId from(@NonNull String id) {
        try {
            return UserId.from(id);
        } catch (NumberFormatException e) {
            throw new InvalidStringUserIdError();
        }
    }

    @AnyThread
    public static void clearCache() {
        UserIdCache.INSTANCE.clear();
    }

    private UserId(String id) {
        this.id = id;
    }

    private UserId(Parcel in) {
        id = in.readString();
    }

    public static List<UserId> fromSerializedList(@NonNull String serialized) {
        String[]          stringIds = DelimiterUtil.split(serialized, DELIMITER);
        List<UserId> out       = new ArrayList<>(stringIds.length);

        for (String stringId : stringIds) {
            UserId id = UserId.from(stringId);
            out.add(id);
        }

        return out;
    }

    public static boolean serializedListContains(@NonNull String serialized, @NonNull UserId UserId) {
        return Pattern.compile("\\b" + UserId.serialize() + "\\b")
                .matcher(serialized)
                .find();
    }

    public boolean isUnknown() {
        return false;
    }

    public @NonNull String serialize() {
        return String.valueOf(id);
    }

    public @NonNull String toQueueKey() {
        return toQueueKey(false);
    }

    public @NonNull String toQueueKey(boolean forMedia) {
        return "UserId::" + id + (forMedia ? "::MEDIA" : "");
    }

    @Override
    public @NonNull String toString() {
        return "UserId::" + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserId that = (UserId) o;

        return id == that.id;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
    }

    public static final Parcelable.Creator<UserId> CREATOR = new Parcelable.Creator<UserId>() {
        @Override
        public UserId createFromParcel(Parcel in) {
            return new UserId(in);
        }

        @Override
        public UserId[] newArray(int size) {
            return new UserId[size];
        }
    };

    private static class InvalidLongUserIdError extends AssertionError {}
    private static class InvalidStringUserIdError extends AssertionError {}
    
}
