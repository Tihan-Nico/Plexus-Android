package com.plexus.megaphone;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.annimon.stream.Stream;
import com.plexus.DeprecatedClientActivity;
import com.plexus.keyvalue.PlexusStore;
import com.plexus.utils.FeatureFlags;
import com.plexus.utils.logging.Log;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.plexus.megaphone.Megaphones.Event.CLIENT_DEPRECATED;

/**
 * Creating a new megaphone:
 * - Add an enum to {@link Event}
 * - Return a megaphone in {@link #forRecord(Context, MegaphoneRecord)}
 * - Include the event in {@link #buildDisplayOrder(Context)}
 * <p>
 * Common patterns:
 * - For events that have a snooze-able recurring display schedule, use a {@link RecurringSchedule}.
 * - For events guarded by feature flags, set a {@link ForeverSchedule} with false in
 * {@link #buildDisplayOrder(Context)}.
 * - For events that change, return different megaphones in {@link #forRecord(Context, MegaphoneRecord)}
 * based on whatever properties you're interested in.
 */
public class Megaphones {

    private static final String TAG = Log.tag(Megaphones.class);

    private static final MegaphoneSchedule ALWAYS = new ForeverSchedule(true);
    private static final MegaphoneSchedule NEVER = new ForeverSchedule(false);

    private Megaphones() {
    }

    static @Nullable
    Megaphone getNextMegaphone(@NonNull Context context, @NonNull Map<Event, MegaphoneRecord> records) {
        long currentTime = System.currentTimeMillis();

        List<Megaphone> megaphones = Stream.of(buildDisplayOrder(context))
                .filter(e -> {
                    MegaphoneRecord record = Objects.requireNonNull(records.get(e.getKey()));
                    MegaphoneSchedule schedule = e.getValue();

                    return !record.isFinished() && schedule.shouldDisplay(record.getSeenCount(), record.getLastSeen(), record.getFirstVisible(), currentTime);
                })
                .map(Map.Entry::getKey)
                .map(records::get)
                .map(record -> Megaphones.forRecord(context, record))
                .sortBy(m -> -m.getPriority().getPriorityValue())
                .toList();

        if (megaphones.size() > 0) {
            return megaphones.get(0);
        } else {
            return null;
        }
    }

    /**
     * This is when you would hide certain megaphones based on {@link FeatureFlags}. You could
     * conditionally set a {@link ForeverSchedule} set to false for disabled features.
     */
    private static Map<Event, MegaphoneSchedule> buildDisplayOrder(@NonNull Context context) {
        return new LinkedHashMap<Event, MegaphoneSchedule>() {{
            put(Event.CLIENT_DEPRECATED, PlexusStore.misc().isClientDeprecated() ? ALWAYS : NEVER);
        }};
    }

    private static @NonNull
    Megaphone forRecord(@NonNull Context context, @NonNull MegaphoneRecord record) {
        if (record.getEvent() == Event.CLIENT_DEPRECATED) {
            return buildClientDeprecatedMegaphone(context);
        }
        throw new IllegalArgumentException("Event not handled!");
    }

    private static @NonNull
    Megaphone buildClientDeprecatedMegaphone(@NonNull Context context) {
        return new Megaphone.Builder(Event.CLIENT_DEPRECATED, Megaphone.Style.FULLSCREEN)
                .disableSnooze()
                .setPriority(Megaphone.Priority.HIGH)
                .setOnVisibleListener((megaphone, listener) -> listener.onMegaphoneNavigationRequested(new Intent(context, DeprecatedClientActivity.class)))
                .build();
    }

    public enum Event {
        CLIENT_DEPRECATED("client_deprecated");
        private final String key;

        Event(@NonNull String key) {
            this.key = key;
        }

        public static Event fromKey(@NonNull String key) {
            for (Event event : values()) {
                if (event.getKey().equals(key)) {
                    return event;
                }
            }
            throw new IllegalArgumentException("No event for key: " + key);
        }

        public static boolean hasKey(@NonNull String key) {
            for (Event event : values()) {
                if (event.getKey().equals(key)) {
                    return true;
                }
            }
            return false;
        }

        public @NonNull
        String getKey() {
            return key;
        }
    }

}
