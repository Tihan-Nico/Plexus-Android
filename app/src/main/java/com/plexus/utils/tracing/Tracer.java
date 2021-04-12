package com.plexus.utils.tracing;

import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.protobuf.ByteString;
import com.plexus.util.tracing.TraceProtos;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class to create Perfetto-compatible traces. Currently keeps the entire trace in memory to
 * avoid weirdness with synchronizing to disk.
 * <p>
 * Some general info on how the Perfetto format works:
 * - The file format is just a Trace proto (see Trace.proto)
 * - The Trace proto is just a series of TracePackets
 * - TracePackets can describe:
 * - Threads
 * - Start of a method
 * - End of a method
 * - (And a bunch of other stuff that's not relevant to use at this point)
 * <p>
 * We keep a circular buffer of TracePackets for method calls, and we keep a separate list of
 * TracePackets for threads so we don't lose any of those.
 * <p>
 * Serializing is just a matter of throwing all the TracePackets we have into a proto.
 * <p>
 * Note: This class aims to be largely-thread-safe, but prioritizes speed and memory efficiency
 * above all else. These methods are going to be called very quickly from every thread imaginable,
 * and we want to create as little overhead as possible. The idea being that it's ok if we don't,
 * for example, keep a perfect circular buffer size if it allows us to reduce overhead. The only
 * cost of screwing up would be dropping a trace packet or something, which, while sad, won't affect
 * how the app functions.
 */
public final class Tracer {

    private static final Tracer INSTANCE = new Tracer();
    private static final int TRUSTED_SEQUENCE_ID = 1;
    private static final byte[] SYNCHRONIZATION_MARKER = toByteArray(UUID.fromString("82477a76-b28d-42ba-81dc-33326d57a079"));
    private static final long SYNCHRONIZATION_INTERVAL = TimeUnit.SECONDS.toNanos(3);
    private final Clock clock;
    private final Map<Long, TraceProtos.TracePacket> threadPackets;
    private final Queue<TraceProtos.TracePacket> eventPackets;
    private final AtomicInteger eventCount;
    private long lastSyncTime;
    private long maxBufferSize;
    private Tracer() {
        this.clock = SystemClock::elapsedRealtimeNanos;
        this.threadPackets = new ConcurrentHashMap<>();
        this.eventPackets = new ConcurrentLinkedQueue<>();
        this.eventCount = new AtomicInteger(0);
        this.maxBufferSize = 3_500;
    }

    public static @NonNull
    Tracer getInstance() {
        return INSTANCE;
    }

    private static TraceProtos.TracePacket forTrack(long id, String name) {
        return TraceProtos.TracePacket.newBuilder()
                .setTrustedPacketSequenceId(TRUSTED_SEQUENCE_ID)
                .setTrackDescriptor(TraceProtos.TrackDescriptor.newBuilder()
                        .setUuid(id)
                        .setName(name))
                .build();

    }

    private static TraceProtos.TracePacket forMethodStart(@NonNull String name, long time, long threadId, @Nullable Map<String, String> values) {
        TraceProtos.TrackEvent.Builder event = TraceProtos.TrackEvent.newBuilder()
                .setTrackUuid(threadId)
                .setName(name)
                .setType(TraceProtos.TrackEvent.Type.TYPE_SLICE_BEGIN);

        if (values != null) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                event.addDebugAnnotations(debugAnnotation(entry.getKey(), entry.getValue()));
            }
        }

        return TraceProtos.TracePacket.newBuilder()
                .setTrustedPacketSequenceId(TRUSTED_SEQUENCE_ID)
                .setTimestamp(time)
                .setTrackEvent(event)
                .build();
    }

    private static TraceProtos.DebugAnnotation debugAnnotation(@NonNull String key, @Nullable String value) {
        return TraceProtos.DebugAnnotation.newBuilder()
                .setName(key)
                .setStringValue(value != null ? value : "")
                .build();
    }

    private static TraceProtos.TracePacket forMethodEnd(@NonNull String name, long time, long threadId) {
        return TraceProtos.TracePacket.newBuilder()
                .setTrustedPacketSequenceId(TRUSTED_SEQUENCE_ID)
                .setTimestamp(time)
                .setTrackEvent(TraceProtos.TrackEvent.newBuilder()
                        .setTrackUuid(threadId)
                        .setName(name)
                        .setType(TraceProtos.TrackEvent.Type.TYPE_SLICE_END))
                .build();
    }

    private static TraceProtos.TracePacket forSynchronization(long time) {
        return TraceProtos.TracePacket.newBuilder()
                .setTrustedPacketSequenceId(TRUSTED_SEQUENCE_ID)
                .setTimestamp(time)
                .setSynchronizationMarker(ByteString.copyFrom(SYNCHRONIZATION_MARKER))
                .build();
    }

    public static byte[] toByteArray(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        return buffer.array();
    }

    public void setMaxBufferSize(long maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    public void start(@NonNull String methodName) {
        start(methodName, Thread.currentThread().getId(), null);
    }

    public void start(@NonNull String methodName, long trackId) {
        start(methodName, trackId, null);
    }

    public void start(@NonNull String methodName, @NonNull String key, @Nullable String value) {
        start(methodName, Thread.currentThread().getId(), key, value);
    }

    public void start(@NonNull String methodName, long trackId, @NonNull String key, @Nullable String value) {
        start(methodName, trackId, Collections.singletonMap(key, value));
    }

    public void start(@NonNull String methodName, @Nullable Map<String, String> values) {
        start(methodName, Thread.currentThread().getId(), values);
    }

    public void start(@NonNull String methodName, long trackId, @Nullable Map<String, String> values) {
        long time = clock.getTimeNanos();

        if (time - lastSyncTime > SYNCHRONIZATION_INTERVAL) {
            addPacket(forSynchronization(time));
            lastSyncTime = time;
        }

        if (!threadPackets.containsKey(trackId)) {
            threadPackets.put(trackId, forTrackId(trackId));
        }

        addPacket(forMethodStart(methodName, time, trackId, values));
    }

    public void end(@NonNull String methodName) {
        addPacket(forMethodEnd(methodName, clock.getTimeNanos(), Thread.currentThread().getId()));
    }

    public void end(@NonNull String methodName, long trackId) {
        addPacket(forMethodEnd(methodName, clock.getTimeNanos(), trackId));
    }

    public @NonNull
    byte[] serialize() {
        TraceProtos.Trace.Builder trace = TraceProtos.Trace.newBuilder();

        for (TraceProtos.TracePacket thread : threadPackets.values()) {
            trace.addPacket(thread);
        }

        for (TraceProtos.TracePacket event : eventPackets) {
            trace.addPacket(event);
        }

        trace.addPacket(forSynchronization(clock.getTimeNanos()));

        return trace.build().toByteArray();
    }

    /**
     * Attempts to add a packet to our list while keeping the size of our circular buffer in-check.
     * The tracking of the event count is not perfectly thread-safe, but doing it in a thread-safe
     * way would likely involve adding a lock, which we really don't want to do, since it'll add
     * unnecessary overhead.
     * <p>
     * Note that we keep track of the event count separately because
     * {@link ConcurrentLinkedQueue#size()} is NOT a constant-time operation.
     */
    private void addPacket(@NonNull TraceProtos.TracePacket packet) {
        eventPackets.add(packet);

        int size = eventCount.incrementAndGet();

        for (int i = size; i > maxBufferSize; i--) {
            eventPackets.poll();
            eventCount.decrementAndGet();
        }
    }

    private TraceProtos.TracePacket forTrackId(long id) {
        if (id == TrackId.DB_LOCK) {
            return forTrack(id, TrackId.DB_LOCK_NAME);
        } else {
            Thread currentThread = Thread.currentThread();
            return forTrack(currentThread.getId(), currentThread.getName());
        }
    }

    private interface Clock {
        long getTimeNanos();
    }

    public static final class TrackId {
        public static final long DB_LOCK = -8675309;

        private static final String DB_LOCK_NAME = "Database Lock";
    }
}
