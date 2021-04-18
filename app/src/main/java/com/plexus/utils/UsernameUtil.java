package com.plexus.utils;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.plexus.core.utils.logging.Log;
import com.plexus.utils.guava.Optional;

import java.util.regex.Pattern;

public class UsernameUtil {

    private static final String TAG = Log.tag(UsernameUtil.class);

    public static final int MIN_LENGTH = 4;
    public static final int MAX_LENGTH = 26;

    private static final Pattern FULL_PATTERN        = Pattern.compile("^[a-z_][a-z0-9_]{3,25}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DIGIT_START_PATTERN = Pattern.compile("^[0-9].*$");

    public static boolean isValidUsernameForSearch(@Nullable String value) {
        return !TextUtils.isEmpty(value) && !DIGIT_START_PATTERN.matcher(value).matches();
    }

    public static Optional<InvalidReason> checkUsername(@Nullable String value) {
        if (value == null) {
            return Optional.of(InvalidReason.TOO_SHORT);
        } else if (value.length() < MIN_LENGTH) {
            return Optional.of(InvalidReason.TOO_SHORT);
        } else if (value.length() > MAX_LENGTH) {
            return Optional.of(InvalidReason.TOO_LONG);
        } else if (DIGIT_START_PATTERN.matcher(value).matches()) {
            return Optional.of(InvalidReason.STARTS_WITH_NUMBER);
        } else if (!FULL_PATTERN.matcher(value).matches()) {
            return Optional.of(InvalidReason.INVALID_CHARACTERS);
        } else {
            return Optional.absent();
        }
    }

    public enum InvalidReason {
        TOO_SHORT, TOO_LONG, INVALID_CHARACTERS, STARTS_WITH_NUMBER
    }
}
