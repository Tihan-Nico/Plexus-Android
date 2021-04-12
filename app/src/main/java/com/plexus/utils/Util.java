package com.plexus.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.annimon.stream.Stream;
import com.plexus.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Util {
    private static final String TAG = Util.class.getSimpleName();

    private static final long BUILD_LIFESPAN = TimeUnit.DAYS.toMillis(90);

    private static volatile Handler handler;

    public static <T> List<T> asList(T... elements) {
        List<T> result = new LinkedList<>();
        Collections.addAll(result, elements);
        return result;
    }

    public static String join(String[] list, String delimiter) {
        return join(Arrays.asList(list), delimiter);
    }

    public static String join(Collection<String> list, String delimiter) {
        StringBuilder result = new StringBuilder();
        int i = 0;

        for (String item : list) {
            result.append(item);

            if (++i < list.size())
                result.append(delimiter);
        }

        return result.toString();
    }

    public static String join(long[] list, String delimeter) {
        List<Long> boxed = new ArrayList<>(list.length);

        for (int i = 0; i < list.length; i++) {
            boxed.add(list[i]);
        }

        return join(boxed, delimeter);
    }

    @SafeVarargs
    public static @NonNull
    <E> List<E> join(@NonNull List<E>... lists) {
        int totalSize = Stream.of(lists).reduce(0, (sum, list) -> sum + list.size());
        List<E> joined = new ArrayList<>(totalSize);

        for (List<E> list : lists) {
            joined.addAll(list);
        }

        return joined;
    }

    public static String join(List<Long> list, String delimeter) {
        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < list.size(); j++) {
            if (j != 0) sb.append(delimeter);
            sb.append(list.get(j));
        }

        return sb.toString();
    }

    public static String rightPad(String value, int length) {
        if (value.length() >= length) {
            return value;
        }

        StringBuilder out = new StringBuilder(value);
        while (out.length() < length) {
            out.append(" ");
        }

        return out.toString();
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(@Nullable String value) {
        return value == null || value.length() == 0;
    }

    public static boolean hasItems(@Nullable Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static <K, V> V getOrDefault(@NonNull Map<K, V> map, K key, V defaultValue) {
        return map.containsKey(key) ? map.get(key) : defaultValue;
    }

    public static String getFirstNonEmpty(String... values) {
        for (String value : values) {
            if (!Util.isEmpty(value)) {
                return value;
            }
        }
        return "";
    }

    public static @NonNull
    String emptyIfNull(@Nullable String value) {
        return value != null ? value : "";
    }

    public static @NonNull
    CharSequence emptyIfNull(@Nullable CharSequence value) {
        return value != null ? value : "";
    }

    public static <E> List<List<E>> chunk(@NonNull List<E> list, int chunkSize) {
        List<List<E>> chunks = new ArrayList<>(list.size() / chunkSize);

        for (int i = 0; i < list.size(); i += chunkSize) {
            List<E> chunk = list.subList(i, Math.min(list.size(), i + chunkSize));
            chunks.add(chunk);
        }

        return chunks;
    }

    public static CharSequence getBoldedString(String value) {
        SpannableString spanned = new SpannableString(value);
        spanned.setSpan(new StyleSpan(Typeface.BOLD), 0,
                spanned.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanned;
    }

    public static void wait(Object lock, long timeout) {
        try {
            lock.wait(timeout);
        } catch (InterruptedException ie) {
            throw new AssertionError(ie);
        }
    }

    public static @NonNull
    <T> T firstNonNull(@Nullable T optional, @NonNull T fallback) {
        return optional != null ? optional : fallback;
    }

    @SafeVarargs
    public static @NonNull
    <T> T firstNonNull(T... ts) {
        for (T t : ts) {
            if (t != null) {
                return t;
            }
        }

        throw new IllegalStateException("All choices were null.");
    }

    public static <T> List<List<T>> partition(List<T> list, int partitionSize) {
        List<List<T>> results = new LinkedList<>();

        for (int index = 0; index < list.size(); index += partitionSize) {
            int subListSize = Math.min(partitionSize, list.size() - index);

            results.add(list.subList(index, index + subListSize));
        }

        return results;
    }

    public static List<String> split(String source, String delimiter) {
        List<String> results = new LinkedList<>();

        if (TextUtils.isEmpty(source)) {
            return results;
        }

        String[] elements = source.split(delimiter);
        Collections.addAll(results, elements);

        return results;
    }

    public static byte[][] split(byte[] input, int firstLength, int secondLength) {
        byte[][] parts = new byte[2][];

        parts[0] = new byte[firstLength];
        System.arraycopy(input, 0, parts[0], 0, firstLength);

        parts[1] = new byte[secondLength];
        System.arraycopy(input, firstLength, parts[1], 0, secondLength);

        return parts;
    }

    public static byte[] combine(byte[]... elements) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            for (byte[] element : elements) {
                baos.write(element);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static byte[] trim(byte[] input, int length) {
        byte[] result = new byte[length];
        System.arraycopy(input, 0, result, 0, result.length);

        return result;
    }

    /**
     * @return The amount of time (in ms) until this build of LookOut will be considered 'expired'.
     * Takes into account both the build age as well as any remote deprecation values.
     */
    /*public static long getTimeUntilBuildExpiry() {
        if (LookoutStore.misc().isClientDeprecated()) {
            return 0;
        }

        long buildAge = System.currentTimeMillis() - BuildConfig.BUILD_TIMESTAMP;
        long timeUntilBuildDeprecation = BUILD_LIFESPAN - buildAge;
        long timeUntilRemoteDeprecation = RemoteDeprecation.getTimeUntilDeprecation();

        if (timeUntilRemoteDeprecation != -1) {
            long timeUntilDeprecation = Math.min(timeUntilBuildDeprecation, timeUntilRemoteDeprecation);
            return Math.max(timeUntilDeprecation, 0);
        } else {
            return Math.max(timeUntilBuildDeprecation, 0);
        }
    }*/

    /**
     * The app version.
     * <p>
     * This code should be used in all places that compare app versions rather than
     * {@link #getManifestApkVersion(Context)} or {@link BuildConfig#VERSION_CODE}.
     */
    public static int getCanonicalVersionCode() {
        return BuildConfig.CANONICAL_VERSION_CODE;
    }

    public static int getManifestApkVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    public static String getSecret(int size) {
        byte[] secret = getSecretBytes(size);
        return Base64.encodeBytes(secret);
    }

    public static byte[] getSecretBytes(int size) {
        return getSecretBytes(new SecureRandom(), size);
    }

    public static byte[] getSecretBytes(@NonNull SecureRandom secureRandom, int size) {
        byte[] secret = new byte[size];
        secureRandom.nextBytes(secret);
        return secret;
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void assertMainThread() {
        if (!isMainThread()) {
            throw new AssertionError("Must run on main thread.");
        }
    }

    public static void assertNotMainThread() {
        if (isMainThread()) {
            throw new AssertionError("Cannot run on main thread.");
        }
    }

    public static void postToMain(final @NonNull Runnable runnable) {
        getHandler().post(runnable);
    }

    public static void runOnMain(final @NonNull Runnable runnable) {
        if (isMainThread()) runnable.run();
        else getHandler().post(runnable);
    }

    public static void runOnMainDelayed(final @NonNull Runnable runnable, long delayMillis) {
        getHandler().postDelayed(runnable, delayMillis);
    }

    public static void cancelRunnableOnMain(@NonNull Runnable runnable) {
        getHandler().removeCallbacks(runnable);
    }

    public static void runOnMainSync(final @NonNull Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            final CountDownLatch sync = new CountDownLatch(1);
            runOnMain(() -> {
                try {
                    runnable.run();
                } finally {
                    sync.countDown();
                }
            });
            try {
                sync.await();
            } catch (InterruptedException ie) {
                throw new AssertionError(ie);
            }
        }
    }

    public static <T> T getRandomElement(T[] elements) {
        return elements[new SecureRandom().nextInt(elements.length)];
    }

    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static int hashCode(@Nullable Object... objects) {
        return Arrays.hashCode(objects);
    }

    public static @Nullable
    Uri uri(@Nullable String uri) {
        if (uri == null) return null;
        else return Uri.parse(uri);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isLowMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && activityManager.isLowRamDevice()) ||
                activityManager.getLargeMemoryClass() <= 64;
    }

    public static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static long clamp(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }

    public static float clamp(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    public static @Nullable
    String readTextFromClipboard(@NonNull Context context) {
        {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

            if (clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClip().getItemCount() > 0) {
                return clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
            } else {
                return null;
            }
        }
    }

    public static void writeTextToClipboard(@NonNull Context context, @NonNull String text) {
        {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText("Safety numbers", text));
        }
    }

    public static int toIntExact(long value) {
        if ((int) value != value) {
            throw new ArithmeticException("integer overflow");
        }
        return (int) value;
    }

    public static boolean isStringEquals(String first, String second) {
        if (first == null) return second == null;
        return first.equals(second);
    }

    public static boolean isEquals(@Nullable Long first, long second) {
        return first != null && first == second;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
    }

    public static void copyToClipboard(@NonNull Context context, @NonNull String text) {
        ServiceUtil.getClipboardManager(context).setPrimaryClip(ClipData.newPlainText("text", text));
    }

    private static Handler getHandler() {
        if (handler == null) {
            synchronized (Util.class) {
                if (handler == null) {
                    handler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return handler;
    }

    @SafeVarargs
    public static <T> List<T> concatenatedList(Collection<T>... items) {
        final List<T> concat = new ArrayList<>(Stream.of(items).reduce(0, (sum, list) -> sum + list.size()));

        for (Collection<T> list : items) {
            concat.addAll(list);
        }

        return concat;
    }

    public static boolean isLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int parseInt(String integer, int defaultValue) {
        try {
            return Integer.parseInt(integer);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Appends the stack trace of the provided throwable onto the provided primary exception. This is
     * useful for when exceptions are thrown inside of asynchronous systems (like runnables in an
     * executor) where you'd otherwise lose important parts of the stack trace. This lets you save a
     * throwable at the entry point, and then combine it with any caught exceptions later.
     *
     * @return The provided primary exception, for convenience.
     */
    public static RuntimeException appendStackTrace(@NonNull RuntimeException primary, @NonNull Throwable secondary) {
        StackTraceElement[] now = primary.getStackTrace();
        StackTraceElement[] then = secondary.getStackTrace();
        StackTraceElement[] combined = new StackTraceElement[now.length + then.length];

        System.arraycopy(now, 0, combined, 0, now.length);
        System.arraycopy(then, 0, combined, now.length, then.length);

        primary.setStackTrace(combined);

        return primary;
    }
}

