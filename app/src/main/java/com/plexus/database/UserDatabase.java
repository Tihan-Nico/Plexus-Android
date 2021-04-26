package com.plexus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.annimon.stream.Stream;
import com.google.protobuf.InvalidProtocolBufferException;
import com.plexus.core.utils.guava.Optional;
import com.plexus.core.utils.logging.Log;
import com.plexus.database.helpers.SQLCipherOpenHelper;
import com.plexus.databaseprotos.Wallpaper;
import com.plexus.model.account.User;
import com.plexus.model.account.UserId;
import com.plexus.utils.CursorUtil;
import com.plexus.utils.Pair;
import com.plexus.utils.SqlUtil;
import com.plexus.utils.StringUtil;
import com.plexus.utils.Util;
import com.plexus.utils.UuidUtil;
import com.plexus.wallpaper.ChatWallpaper;
import com.plexus.wallpaper.ChatWallpaperFactory;
import com.plexus.wallpaper.WallpaperStorage;

import java.io.Closeable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class UserDatabase extends Database {

    private static final String TAG = Log.tag(UserDatabase.class);

    static final String TABLE_NAME = "recipient";
    public static final String ID = "_id";
    private static final String UUID = "uuid";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String BIO = "bio";
    private static final String ABOUT = "about";
    private static final String COUNTRY = "country";
    private static final String IMAGEURL = "imageurl";
    private static final String PROFILE_COVER = "profile_cover";
    private static final String VERIFIED = "verified";
    private static final String USER = "user";
    private static final String COMPANY = "company";
    private static final String ARTIST = "artist";
    private static final String BIRTHDAY = "birthday";
    private static final String GENDER = "gender";
    private static final String WEBSITE = "website";
    private static final String FEELING = "feeling";
    private static final String ACTIVE = "active";
    private static final String ACCOUNT_TYPE = "account_type";
    private static final String REGISTRATION_TIME = "registration_time";
    private static final String ONLINE_PRESENCE = "online_presence";
    private static final String PRIVATE_ACCOUNT = "private_account";
    private static final String BLOCKED_TIMESTAMP = "blocked_timestamp";
    private static final String BLOCKED_PLATFORM = "blocked_platform";
    private static final String NICKNAME = "nickname";
    private static final String BANNED = "banned";
    private static final String TOKEN = "token";
    private static final String ONLINE = "online";
    private static final String USERNAME = "username";
    private static final String BLOCKED = "blocked";
    private static final String MESSAGE_RINGTONE = "message_ringtone";
    private static final String MESSAGE_VIBRATE = "message_vibrate";
    private static final String CALL_RINGTONE = "call_ringtone";
    private static final String CALL_VIBRATE = "call_vibrate";
    private static final String NOTIFICATION_CHANNEL = "notification_channel";
    private static final String MUTE_UNTIL = "mute_until";
    private static final String STORAGE_SERVICE_ID = "storage_service_key";
    private static final String MENTION_SETTING = "mention_setting";
    private static final String WALLPAPER = "wallpaper";
    private static final String WALLPAPER_URI = "wallpaper_file";

    private static final String[] RECIPIENT_PROJECTION = new String[]{
            ID, UUID, USERNAME, NAME, SURNAME, BIO, ONLINE, TOKEN, BANNED, NICKNAME, ARTIST,
            BIRTHDAY, WEBSITE, FEELING, ACTIVE, ONLINE_PRESENCE, PRIVATE_ACCOUNT, BLOCKED_PLATFORM,
            BLOCKED_TIMESTAMP, GENDER, COUNTRY, IMAGEURL, PROFILE_COVER, VERIFIED, USER, COMPANY,
            BLOCKED, MESSAGE_RINGTONE, CALL_RINGTONE, MESSAGE_VIBRATE, CALL_VIBRATE, MUTE_UNTIL,
            NOTIFICATION_CHANNEL, ACCOUNT_TYPE, REGISTRATION_TIME,
            STORAGE_SERVICE_ID,
            MENTION_SETTING, WALLPAPER, WALLPAPER_URI,
            MENTION_SETTING,
            ABOUT
    };

    private static final String[] ID_PROJECTION = new String[]{ID};
    private static final String[] TYPED_RECIPIENT_PROJECTION = Stream.of(RECIPIENT_PROJECTION)
            .map(columnName -> TABLE_NAME + "." + columnName)
            .toList().toArray(new String[0]);

    static final String[] TYPED_RECIPIENT_PROJECTION_NO_ID = Arrays.copyOfRange(TYPED_RECIPIENT_PROJECTION, 1, TYPED_RECIPIENT_PROJECTION.length);

    public enum VibrateState {
        DEFAULT(0), ENABLED(1), DISABLED(2);

        private final int id;

        VibrateState(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static VibrateState fromId(int id) {
            return values()[id];
        }

        public static VibrateState fromBoolean(boolean enabled) {
            return enabled ? ENABLED : DISABLED;
        }
    }

    public enum MentionSetting {
        ALWAYS_NOTIFY(0), DO_NOT_NOTIFY(1);

        private final int id;

        MentionSetting(int id) {
            this.id = id;
        }

        int getId() {
            return id;
        }

        public static MentionSetting fromId(int id) {
            return values()[id];
        }
    }

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    UUID + " TEXT UNIQUE DEFAULT NULL, " +
                    USERNAME + " TEXT UNIQUE DEFAULT NULL, " +
                    NAME + " TEXT UNIQUE DEFAULT NULL, " +
                    SURNAME + " TEXT UNIQUE DEFAULT NULL, " +
                    BLOCKED + " INTEGER DEFAULT 0, " +
                    BIO + " TEXT UNIQUE DEFAULT NULL, " +
                    ABOUT + " TEXT UNIQUE DEFAULT NULL, " +
                    COUNTRY + " TEXT UNIQUE DEFAULT NULL, " +
                    IMAGEURL + " TEXT UNIQUE DEFAULT NULL, " +
                    PROFILE_COVER + " TEXT UNIQUE DEFAULT NULL, " +
                    VERIFIED + " INTEGER DEFAULT 0, " +
                    USER + " INTEGER DEFAULT 0, " +
                    COMPANY + " INTEGER DEFAULT 0, " +
                    ARTIST + " INTEGER DEFAULT 0, " +
                    BIRTHDAY + " TEXT UNIQUE DEFAULT NULL, " +
                    GENDER + " TEXT UNIQUE DEFAULT NULL, " +
                    WEBSITE + " TEXT UNIQUE DEFAULT NULL, " +
                    FEELING + " TEXT UNIQUE DEFAULT NULL, " +
                    ACTIVE + " INTEGER DEFAULT 0, " +
                    ONLINE_PRESENCE + " INTEGER DEFAULT 0, " +
                    PRIVATE_ACCOUNT + " INTEGER DEFAULT 0, " +
                    BLOCKED_TIMESTAMP + " TEXT UNIQUE DEFAULT NULL, " +
                    BLOCKED_PLATFORM + " TEXT UNIQUE DEFAULT NULL, " +
                    NICKNAME + " TEXT UNIQUE DEFAULT NULL, " +
                    BANNED + " INTEGER DEFAULT 0, " +
                    TOKEN + " TEXT UNIQUE DEFAULT NULL, " +
                    ONLINE + " INTEGER DEFAULT 0, " +
                    ACCOUNT_TYPE + " TEXT UNIQUE DEFAULT NULL, " +
                    REGISTRATION_TIME + " TEXT UNIQUE DEFAULT NULL, " +
                    MESSAGE_RINGTONE + " TEXT DEFAULT NULL, " +
                    MESSAGE_VIBRATE + " INTEGER DEFAULT " + VibrateState.DEFAULT.getId() + ", " +
                    CALL_RINGTONE + " TEXT DEFAULT NULL, " +
                    CALL_VIBRATE + " INTEGER DEFAULT " + VibrateState.DEFAULT.getId() + ", " +
                    NOTIFICATION_CHANNEL + " TEXT DEFAULT NULL, " +
                    MUTE_UNTIL + " INTEGER DEFAULT 0, " +
                    STORAGE_SERVICE_ID + " TEXT UNIQUE DEFAULT NULL, " +
                    MENTION_SETTING + " INTEGER DEFAULT " + MentionSetting.ALWAYS_NOTIFY.getId() + ", " +
                    WALLPAPER + " BLOB DEFAULT NULL, " +
                    WALLPAPER_URI + " TEXT DEFAULT NULL, " +
                    ABOUT + " TEXT DEFAULT NULL);";

    public UserDatabase(Context context, SQLCipherOpenHelper databaseHelper) {
        super(context, databaseHelper);
    }

    public Cursor getBlocked() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        return database.query(TABLE_NAME, ID_PROJECTION, BLOCKED + " = 1",
                null, null, null, null, null);
    }

    public RecipientReader readerForBlocked(Cursor cursor) {
        return new RecipientReader(cursor);
    }

    public RecipientReader getRecipientsWithNotificationChannels() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, ID_PROJECTION, NOTIFICATION_CHANNEL + " NOT NULL",
                null, null, null, null, null);

        return new RecipientReader(cursor);
    }

    public @NonNull RecipientSettings getRecipientSettings(@NonNull UserId id) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String         query    = ID + " = ?";
        String[]       args     = new String[] { id.serialize() };

        try (Cursor cursor = database.query(TABLE_NAME, RECIPIENT_PROJECTION, query, args, null, null, null)) {
            if (cursor != null && cursor.moveToNext()) {
                return getRecipientSettings(context, cursor);
            } else {
                Optional<UserId> remapped = RemappedRecords.getInstance().getRecipient(context, id);
                if (remapped.isPresent()) {
                    Log.w(TAG, "Missing recipient for " + id + ", but found it in the remapped records as " + remapped.get());
                    return getRecipientSettings(remapped.get());
                } else {
                    throw new MissingRecipientException(id);
                }
            }
        }
    }

    static @NonNull
    RecipientSettings getRecipientSettings(@NonNull Context context, @NonNull Cursor cursor) {
        return getRecipientSettings(context, cursor, ID);
    }

    static @NonNull
    RecipientSettings getRecipientSettings(@NonNull Context context, @NonNull Cursor cursor, @NonNull String idColumnName) {
        String id = CursorUtil.requireString(cursor, idColumnName);
        UUID uuid = UuidUtil.parseOrNull(CursorUtil.requireString(cursor, UUID));
        String username = CursorUtil.requireString(cursor, USERNAME);
        String name = CursorUtil.requireString(cursor, NAME);
        String surname = CursorUtil.requireString(cursor, SURNAME);
        String bio = CursorUtil.requireString(cursor, BIO);
        boolean banned = CursorUtil.requireBoolean(cursor, BANNED);
        String birthay = CursorUtil.requireString(cursor, BIRTHDAY);
        String account_type = CursorUtil.requireString(cursor, ACCOUNT_TYPE);
        String registration_time = CursorUtil.requireString(cursor, REGISTRATION_TIME);
        String blocked_platform = CursorUtil.requireString(cursor, BLOCKED_PLATFORM);
        boolean private_account = CursorUtil.requireBoolean(cursor, PRIVATE_ACCOUNT);
        String profile_cover = CursorUtil.requireString(cursor, PROFILE_COVER);
        String imageurl = CursorUtil.requireString(cursor, IMAGEURL);
        boolean user = CursorUtil.requireBoolean(cursor, USER);
        boolean company = CursorUtil.requireBoolean(cursor, COMPANY);
        String country = CursorUtil.requireString(cursor, COUNTRY);
        boolean active = CursorUtil.requireBoolean(cursor, ACTIVE);
        boolean artist = CursorUtil.requireBoolean(cursor, ARTIST);
        String gender = CursorUtil.requireString(cursor, GENDER);
        String website = CursorUtil.requireString(cursor, WEBSITE);
        String feeling = CursorUtil.requireString(cursor, FEELING);
        int online = CursorUtil.requireInt(cursor, ONLINE);
        String online_presence = CursorUtil.requireString(cursor, ONLINE_PRESENCE);
        String blocked_timestamp = CursorUtil.requireString(cursor, BLOCKED_TIMESTAMP);
        String token = CursorUtil.requireString(cursor, TOKEN);
        String nickname = CursorUtil.requireString(cursor, NICKNAME);
        boolean verified = CursorUtil.requireBoolean(cursor, VERIFIED);
        boolean blocked = CursorUtil.requireBoolean(cursor, BLOCKED);
        String messageRingtone = CursorUtil.requireString(cursor, MESSAGE_RINGTONE);
        String callRingtone = CursorUtil.requireString(cursor, CALL_RINGTONE);
        int messageVibrateState = CursorUtil.requireInt(cursor, MESSAGE_VIBRATE);
        int callVibrateState = CursorUtil.requireInt(cursor, CALL_VIBRATE);
        long muteUntil = cursor.getLong(cursor.getColumnIndexOrThrow(MUTE_UNTIL));
        String notificationChannel = CursorUtil.requireString(cursor, NOTIFICATION_CHANNEL);
        String storageKeyRaw = CursorUtil.requireString(cursor, STORAGE_SERVICE_ID);
        int mentionSettingId = CursorUtil.requireInt(cursor, MENTION_SETTING);
        byte[] wallpaper = CursorUtil.requireBlob(cursor, WALLPAPER);
        String about = CursorUtil.requireString(cursor, ABOUT);

        ChatWallpaper chatWallpaper = null;

        if (wallpaper != null) {
            try {
                chatWallpaper = ChatWallpaperFactory.create(Wallpaper.parseFrom(wallpaper));
            } catch (InvalidProtocolBufferException e) {
                Log.w(TAG, "Failed to parse wallpaper.", e);
            }
        }

        return new RecipientSettings(UserId.from(id),
                name,
                surname,
                country,
                bio,
                imageurl,
                profile_cover,
                account_type,
                verified,
                user,
                company,
                artist,
                birthay,
                gender,
                website,
                feeling,
                registration_time,
                active,
                online_presence,
                private_account,
                blocked_timestamp,
                blocked_platform,
                nickname,
                banned,
                token,
                online,
                username,
                blocked,
                muteUntil,
                VibrateState.fromId(messageVibrateState),
                VibrateState.fromId(callVibrateState),
                Util.uri(messageRingtone),
                Util.uri(callRingtone),
                notificationChannel,
                MentionSetting.fromId(mentionSettingId),
                chatWallpaper,
                about);
    }

    public void setBlocked(@NonNull UserId id, boolean blocked) {
        ContentValues values = new ContentValues();
        values.put(BLOCKED, blocked ? 1 : 0);
        if (update(id, values)) {
            User.live(id).refresh();
        }
    }

    public void setMessageRingtone(@NonNull UserId id, @Nullable Uri notification) {
        ContentValues values = new ContentValues();
        values.put(MESSAGE_RINGTONE, notification == null ? null : notification.toString());
        if (update(id, values)) {
            User.live(id).refresh();
        }
    }

    public void setCallRingtone(@NonNull UserId id, @Nullable Uri ringtone) {
        ContentValues values = new ContentValues();
        values.put(CALL_RINGTONE, ringtone == null ? null : ringtone.toString());
        if (update(id, values)) {
            User.live(id).refresh();
        }
    }

    public void setMessageVibrate(@NonNull UserId id, @NonNull VibrateState enabled) {
        ContentValues values = new ContentValues();
        values.put(MESSAGE_VIBRATE, enabled.getId());
        if (update(id, values)) {
            User.live(id).refresh();
        }
    }

    public void setCallVibrate(@NonNull UserId id, @NonNull VibrateState enabled) {
        ContentValues values = new ContentValues();
        values.put(CALL_VIBRATE, enabled.getId());
        if (update(id, values)) {
            User.live(id).refresh();
        }
    }

    public void setMuted(@NonNull UserId id, long until) {
        ContentValues values = new ContentValues();
        values.put(MUTE_UNTIL, until);
        if (update(id, values)) {
            User.live(id).refresh();
        }
    }

    public void setAbout(@NonNull UserId id, @Nullable String about, @Nullable String emoji) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ABOUT, about);

        if (update(id, contentValues)) {
            User.live(id).refresh();
        }
    }

    public void setNotificationChannel(@NonNull UserId id, @Nullable String notificationChannel) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(NOTIFICATION_CHANNEL, notificationChannel);
        if (update(id, contentValues)) {
            User.live(id).refresh();
        }
    }

    public void resetAllWallpaper() {
        SQLiteDatabase                  database      = databaseHelper.getWritableDatabase();
        String[]                        selection     = SqlUtil.buildArgs(ID, WALLPAPER_URI);
        String                          where         = WALLPAPER + " IS NOT NULL";
        List<Pair<UserId, String>> idWithWallpaper = new LinkedList<>();

        database.beginTransaction();

        try {
            try (Cursor cursor = database.query(TABLE_NAME, selection, where, null, null, null, null)) {
                while (cursor != null && cursor.moveToNext()) {
                    idWithWallpaper.add(new Pair<>(UserId.from(CursorUtil.requireString(cursor, ID)),
                            CursorUtil.getString(cursor, WALLPAPER_URI).orNull()));
                }
            }

            if (idWithWallpaper.isEmpty()) {
                return;
            }

            ContentValues values = new ContentValues(2);
            values.put(WALLPAPER_URI, (String) null);
            values.put(WALLPAPER, (byte[]) null);

            int rowsUpdated = database.update(TABLE_NAME, values, where, null);
            if (rowsUpdated == idWithWallpaper.size()) {
                for (Pair<UserId, String> pair : idWithWallpaper) {
                    User.live(pair.first()).refresh();
                    if (pair.second() != null) {
                        WallpaperStorage.onWallpaperDeselected(context, Uri.parse(pair.second()));
                    }
                }
            } else {
                throw new AssertionError("expected " + idWithWallpaper.size() + " but got " + rowsUpdated);
            }

        } finally {
            database.setTransactionSuccessful();
            database.endTransaction();
        }

    }

    public void setWallpaper(@NonNull UserId id, @Nullable ChatWallpaper chatWallpaper) {
        setWallpaper(id, chatWallpaper != null ? chatWallpaper.serialize() : null);
    }

    private void setWallpaper(@NonNull UserId id, @Nullable Wallpaper wallpaper) {
        Uri existingWallpaperUri = getWallpaperUri(id);

        ContentValues values = new ContentValues();
        values.put(WALLPAPER, wallpaper != null ? wallpaper.toByteArray() : null);

        if (wallpaper != null && wallpaper.hasFile()) {
            values.put(WALLPAPER_URI, wallpaper.getFile().getUri());
        } else {
            values.putNull(WALLPAPER_URI);
        }

        if (update(id, values)) {
            User.live(id).refresh();
        }

        if (existingWallpaperUri != null) {
            WallpaperStorage.onWallpaperDeselected(context, existingWallpaperUri);
        }
    }

    public void setDimWallpaperInDarkTheme(@NonNull UserId id, boolean enabled) {
        Wallpaper wallpaper = getWallpaper(id);

        if (wallpaper == null) {
            throw new IllegalStateException("No wallpaper set for " + id);
        }

        Wallpaper updated = wallpaper.toBuilder()
                .setDimLevelInDarkTheme(enabled ? ChatWallpaper.FIXED_DIM_LEVEL_FOR_DARK_THEME : 0)
                .build();

        setWallpaper(id, updated);
    }

    private @Nullable
    Wallpaper getWallpaper(@NonNull UserId id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        try (Cursor cursor = db.query(TABLE_NAME, new String[]{WALLPAPER}, ID_WHERE, SqlUtil.buildArgs(id), null, null, null)) {
            if (cursor.moveToFirst()) {
                byte[] raw = CursorUtil.requireBlob(cursor, WALLPAPER);

                if (raw != null) {
                    try {
                        return Wallpaper.parseFrom(raw);
                    } catch (InvalidProtocolBufferException e) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }

        return null;
    }

    private @Nullable
    Uri getWallpaperUri(@NonNull UserId id) {
        Wallpaper wallpaper = getWallpaper(id);

        if (wallpaper != null && wallpaper.hasFile()) {
            return Uri.parse(wallpaper.getFile().getUri());
        } else {
            return null;
        }
    }

    public int getWallpaperUriUsageCount(@NonNull Uri uri) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = WALLPAPER_URI + " = ?";
        String[] args = SqlUtil.buildArgs(uri);

        try (Cursor cursor = db.query(TABLE_NAME, new String[]{"COUNT(*)"}, query, args, null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    /**
     * Builds a case-insensitive GLOB pattern for fuzzy text queries. Works with all unicode
     * characters.
     * <p>
     * Ex:
     * cat -> [cC][aA][tT]
     */
    private static String buildCaseInsensitiveGlobPattern(@NonNull String query) {
        if (TextUtils.isEmpty(query)) {
            return "*";
        }

        StringBuilder pattern = new StringBuilder();

        for (int i = 0, len = query.codePointCount(0, query.length()); i < len; i++) {
            String point = StringUtil.codePointToString(query.codePointAt(i));

            pattern.append("[");
            pattern.append(point.toLowerCase());
            pattern.append(point.toUpperCase());
            pattern.append(getAccentuatedCharRegex(point.toLowerCase()));
            pattern.append("]");
        }

        return "*" + pattern.toString() + "*";
    }

    private static @NonNull
    String getAccentuatedCharRegex(@NonNull String query) {
        switch (query) {
            case "a":
                return "À-Åà-åĀ-ąǍǎǞ-ǡǺ-ǻȀ-ȃȦȧȺɐ-ɒḀḁẚẠ-ặ";
            case "b":
                return "ßƀ-ƅɃɓḂ-ḇ";
            case "c":
                return "çÇĆ-čƆ-ƈȻȼɔḈḉ";
            case "d":
                return "ÐðĎ-đƉ-ƍȡɖɗḊ-ḓ";
            case "e":
                return "È-Ëè-ëĒ-ěƎ-ƐǝȄ-ȇȨȩɆɇɘ-ɞḔ-ḝẸ-ệ";
            case "f":
                return "ƑƒḞḟ";
            case "g":
                return "Ĝ-ģƓǤ-ǧǴǵḠḡ";
            case "h":
                return "Ĥ-ħƕǶȞȟḢ-ḫẖ";
            case "i":
                return "Ì-Ïì-ïĨ-ıƖƗǏǐȈ-ȋɨɪḬ-ḯỈ-ị";
            case "j":
                return "ĴĵǰȷɈɉɟ";
            case "k":
                return "Ķ-ĸƘƙǨǩḰ-ḵ";
            case "l":
                return "Ĺ-łƚȴȽɫ-ɭḶ-ḽ";
            case "m":
                return "Ɯɯ-ɱḾ-ṃ";
            case "n":
                return "ÑñŃ-ŋƝƞǸǹȠȵɲ-ɴṄ-ṋ";
            case "o":
                return "Ò-ÖØò-öøŌ-őƟ-ơǑǒǪ-ǭǾǿȌ-ȏȪ-ȱṌ-ṓỌ-ợ";
            case "p":
                return "ƤƥṔ-ṗ";
            case "q":
                return "";
            case "r":
                return "Ŕ-řƦȐ-ȓɌɍṘ-ṟ";
            case "s":
                return "Ś-šƧƨȘșȿṠ-ṩ";
            case "t":
                return "Ţ-ŧƫ-ƮȚțȾṪ-ṱẗ";
            case "u":
                return "Ù-Üù-üŨ-ųƯ-ƱǓ-ǜȔ-ȗɄṲ-ṻỤ-ự";
            case "v":
                return "ƲɅṼ-ṿ";
            case "w":
                return "ŴŵẀ-ẉẘ";
            case "x":
                return "Ẋ-ẍ";
            case "y":
                return "ÝýÿŶ-ŸƔƳƴȲȳɎɏẎẏỲ-ỹỾỿẙ";
            case "z":
                return "Ź-žƵƶɀẐ-ẕ";
            default:
                return "";
        }
    }

    /**
     * Will update the database with the content values you specified. It will make an intelligent
     * query such that this will only return true if a row was *actually* updated.
     */
    private boolean update(@NonNull UserId id, @NonNull ContentValues contentValues) {
        SqlUtil.Query updateQuery = SqlUtil.buildTrueUpdateQuery(ID_WHERE, SqlUtil.buildArgs(id), contentValues);

        return update(updateQuery, contentValues);
    }

    /**
     * Will update the database with the {@param contentValues} you specified.
     * <p>
     * This will only return true if a row was *actually* updated with respect to the where clause of the {@param updateQuery}.
     */
    private boolean update(@NonNull SqlUtil.Query updateQuery, @NonNull ContentValues contentValues) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        return database.update(TABLE_NAME, contentValues, updateQuery.getWhere(), updateQuery.getWhereArgs()) > 0;
    }

    private @NonNull
    Optional<User> getByColumn(@NonNull String column, String value) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String query = column + " = ?";
        String[] args = new String[]{value};

        try (Cursor cursor = db.query(TABLE_NAME, ID_PROJECTION, query, args, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return Optional.of(User.from(cursor.getString(cursor.getColumnIndexOrThrow(ID))));
            } else {
                return Optional.absent();
            }
        }
    }

    private @NonNull
    GetOrInsertResult getOrInsertByColumn(@NonNull String column, String value) {
        if (TextUtils.isEmpty(value)) {
            throw new AssertionError(column + " cannot be empty.");
        }

        Optional<User> existing = getByColumn(column, value);

        if (existing.isPresent()) {
            return new GetOrInsertResult(existing.get(), false);
        } else {
            ContentValues values = new ContentValues();
            values.put(column, value);

            String id = String.valueOf(databaseHelper.getWritableDatabase().insert(TABLE_NAME, null, values));

            if (id != null) {
                existing = getByColumn(column, value);

                if (existing.isPresent()) {
                    return new GetOrInsertResult(existing.get(), false);
                } else {
                    throw new AssertionError("Failed to insert recipient!");
                }
            } else {
                return new GetOrInsertResult(User.from(String.valueOf(id)), true);
            }
        }
    }

    private void ensureInTransaction() {
        if (!databaseHelper.getWritableDatabase().inTransaction()) {
            throw new IllegalStateException("Must be in a transaction!");
        }
    }

    private static @NonNull
    String nullIfEmpty(String column) {
        return "NULLIF(" + column + ", '')";
    }

    private static @NonNull
    String removeWhitespace(@NonNull String column) {
        return "REPLACE(" + column + ", ' ', '')";
    }

    public static class RecipientSettings {
        private final UserId id;
        private final String name;
        private final String surname;
        private final String country;
        private final String bio;
        private final String imageurl;
        private final String profile_cover;
        private final String account_type;
        private final boolean verified;
        private final boolean user;
        private final boolean company;
        private final boolean artist;
        private final String birthday;
        private final String gender;
        private final String website;
        private final String feeling;
        private final String registration_time;
        private final boolean active;
        private final String online_presence;
        private final boolean private_account;
        private final String blocked_timestamp;
        private final String blocked_platform;
        private final String nickname;
        private final boolean banned;
        private final String token;
        private final long online;
        private final String username;
        private final boolean blocked;
        private final long muteUntil;
        private final VibrateState messageVibrateState;
        private final VibrateState callVibrateState;
        private final Uri messageRingtone;
        private final Uri callRingtone;
        private final String notificationChannel;
        private final MentionSetting mentionSetting;
        private final ChatWallpaper wallpaper;
        private final String about;

        RecipientSettings(@NonNull UserId id,
                          @NonNull String name,
                          @NonNull String surname,
                          @NonNull String country,
                          @NonNull String bio,
                          @NonNull String imageurl,
                          @NonNull String profile_cover,
                          @NonNull String account_type,
                          boolean verified,
                          boolean user,
                          boolean company,
                          boolean artist,
                          @NonNull String birthday,
                          @NonNull String gender,
                          @NonNull String website,
                          @NonNull String feeling,
                          @NonNull String registration_time,
                          boolean active,
                          @NonNull String online_presence,
                          boolean private_account,
                          @NonNull String blocked_timestamp,
                          @NonNull String blocked_platform,
                          @NonNull String nickname,
                          boolean banned,
                          @NonNull String token,
                          long online,
                          @Nullable String username,
                          boolean blocked,
                          long muteUntil,
                          @NonNull VibrateState messageVibrateState,
                          @NonNull VibrateState callVibrateState,
                          @Nullable Uri messageRingtone,
                          @Nullable Uri callRingtone,
                          @Nullable String notificationChannel,
                          @NonNull MentionSetting mentionSetting,
                          @Nullable ChatWallpaper wallpaper,
                          @Nullable String about) {
            this.id = null;
            this.name = name;
            this.surname = surname;
            this.country = country;
            this.bio = bio;
            this.imageurl = imageurl;
            this.profile_cover = profile_cover;
            this.account_type = account_type;
            this.verified = verified;
            this.user = user;
            this.company = company;
            this.artist = artist;
            this.birthday = birthday;
            this.gender = gender;
            this.website = website;
            this.feeling = feeling;
            this.registration_time = registration_time;
            this.active = active;
            this.online_presence = online_presence;
            this.private_account = private_account;
            this.blocked_timestamp = blocked_timestamp;
            this.blocked_platform = blocked_platform;
            this.nickname = nickname;
            this.banned = banned;
            this.token = token;
            this.online = online;
            this.username = username;
            this.blocked = blocked;
            this.muteUntil = muteUntil;
            this.messageVibrateState = messageVibrateState;
            this.callVibrateState = callVibrateState;
            this.messageRingtone = messageRingtone;
            this.callRingtone = callRingtone;
            this.notificationChannel = notificationChannel;
            this.mentionSetting = mentionSetting;
            this.wallpaper = wallpaper;
            this.about = about;
        }

        public UserId getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }

        public String getCountry() {
            return country;
        }

        public String getBio() {
            return bio;
        }

        public String getImageurl() {
            return imageurl;
        }

        public String getProfile_cover() {
            return profile_cover;
        }

        public String getAccount_type() {
            return account_type;
        }

        public boolean isVerified() {
            return verified;
        }

        public boolean isUser() {
            return user;
        }

        public boolean isCompany() {
            return company;
        }

        public boolean isArtist() {
            return artist;
        }

        public String getBirthday() {
            return birthday;
        }

        public String getGender() {
            return gender;
        }

        public String getWebsite() {
            return website;
        }

        public String getFeeling() {
            return feeling;
        }

        public String getRegistration_time() {
            return registration_time;
        }

        public boolean isActive() {
            return active;
        }

        public String getOnline_presence() {
            return online_presence;
        }

        public boolean isPrivate_account() {
            return private_account;
        }

        public String getBlocked_timestamp() {
            return blocked_timestamp;
        }

        public String getBlocked_platform() {
            return blocked_platform;
        }

        public String getNickname() {
            return nickname;
        }

        public boolean isBanned() {
            return banned;
        }

        public String getToken() {
            return token;
        }

        public long getOnline() {
            return online;
        }

        public @Nullable
        String getUsername() {
            return username;
        }

        public boolean isBlocked() {
            return blocked;
        }

        public long getMuteUntil() {
            return muteUntil;
        }

        public @NonNull
        VibrateState getMessageVibrateState() {
            return messageVibrateState;
        }

        public @NonNull
        VibrateState getCallVibrateState() {
            return callVibrateState;
        }

        public @Nullable
        Uri getMessageRingtone() {
            return messageRingtone;
        }

        public @Nullable
        Uri getCallRingtone() {
            return callRingtone;
        }

        public @Nullable
        String getNotificationChannel() {
            return notificationChannel;
        }

        public @NonNull
        MentionSetting getMentionSetting() {
            return mentionSetting;
        }

        public @Nullable
        ChatWallpaper getWallpaper() {
            return wallpaper;
        }

        public @Nullable
        String getAbout() {
            return about;
        }


        public static class SyncExtras {
            private final byte[] storageProto;
            private final byte[] identityKey;
            private final boolean archived;
            private final boolean forcedUnread;

            public SyncExtras(@Nullable byte[] storageProto,
                              @Nullable byte[] identityKey,
                              boolean archived,
                              boolean forcedUnread) {
                this.storageProto = storageProto;
                this.identityKey = identityKey;
                this.archived = archived;
                this.forcedUnread = forcedUnread;
            }

            public @Nullable
            byte[] getStorageProto() {
                return storageProto;
            }

            public boolean isArchived() {
                return archived;
            }

            public @Nullable
            byte[] getIdentityKey() {
                return identityKey;
            }

            public boolean isForcedUnread() {
                return forcedUnread;
            }
        }
    }

    public static class RecipientReader implements Closeable {

        private final Cursor cursor;

        RecipientReader(Cursor cursor) {
            this.cursor = cursor;
        }

        public @NonNull
        User getCurrent() {
            UserId id = UserId.from(cursor.getString(cursor.getColumnIndexOrThrow(ID)));
            return User.resolved(id);
        }

        public @Nullable
        User getNext() {
            if (cursor != null && !cursor.moveToNext()) {
                return null;
            }

            return getCurrent();
        }

        public int getCount() {
            if (cursor != null) return cursor.getCount();
            else return 0;
        }

        public void close() {
            cursor.close();
        }
    }

    private static class GetOrInsertResult {
        final User recipientId;
        final boolean neededInsert;

        private GetOrInsertResult(@NonNull User recipientId, boolean neededInsert) {
            this.recipientId = recipientId;
            this.neededInsert = neededInsert;
        }
    }

    public static class MissingRecipientException extends IllegalStateException {
        public MissingRecipientException(@Nullable UserId id) {
            super("Failed to find recipient with ID: " + id);
        }
    }

}
