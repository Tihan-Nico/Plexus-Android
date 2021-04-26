package com.plexus.jobs;

import androidx.annotation.NonNull;

import com.plexus.core.api.stickers.PlexusStickerManifest;
import com.plexus.core.api.stickers.PlexusStickerPackReceiver;
import com.plexus.core.utils.guava.Preconditions;
import com.plexus.core.utils.logging.Log;
import com.plexus.database.DatabaseFactory;
import com.plexus.database.StickerDatabase;
import com.plexus.database.model.IncomingSticker;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.jobmanagers.BaseJob;
import com.plexus.jobmanagers.Data;
import com.plexus.jobmanagers.Job;
import com.plexus.jobmanagers.JobManager;
import com.plexus.jobmanagers.impl.NetworkConstraint;
import com.plexus.protocol.InvalidMessageException;
import com.plexus.stickers.BlessedPacks;
import com.plexus.stickers.StickerManifest;
import com.plexus.transport.PushNetworkException;
import com.plexus.utils.Hex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.plexus.core.api.stickers.PlexusStickerManifest.*;
import static com.plexus.stickers.StickerManifest.*;

public class StickerPackDownloadJob extends BaseJob {

    public static final String KEY = "StickerPackDownloadJob";

    private static final String TAG = Log.tag(StickerPackDownloadJob.class);

    private static final String KEY_PACK_ID        = "pack_key";
    private static final String KEY_PACK_KEY       = "pack_id";
    private static final String KEY_REFERENCE_PACK = "reference_pack";
    private static final String KEY_NOTIFY         = "notify";

    private final String  packId;
    private final String  packKey;
    private final boolean isReferencePack;
    private final boolean notify;

    /**
     * Downloads all the stickers in a pack.
     * @param notify Whether or not a tooltip will be shown indicating the pack was installed.
     */
    public static @NonNull StickerPackDownloadJob forInstall(@NonNull String packId, @NonNull String packKey, boolean notify) {
        return new StickerPackDownloadJob(packId, packKey, false, notify);
    }

    /**
     * Just installs a reference to the pack -- i.e. just the cover.
     */
    public static @NonNull StickerPackDownloadJob forReference(@NonNull String packId, @NonNull String packKey) {
        return new StickerPackDownloadJob(packId, packKey, true, true);
    }

    private StickerPackDownloadJob(@NonNull String packId, @NonNull String packKey, boolean isReferencePack, boolean notify)
    {
        this(new Parameters.Builder()
                        .addConstraint(NetworkConstraint.KEY)
                        .setLifespan(TimeUnit.DAYS.toMillis(30))
                        .setQueue("StickerPackDownloadJob_" + packId)
                        .build(),
                packId,
                packKey,
                isReferencePack,
                notify);
    }

    private StickerPackDownloadJob(@NonNull Parameters parameters,
                                   @NonNull String packId,
                                   @NonNull String packKey,
                                   boolean isReferencePack,
                                   boolean notify)
    {
        super(parameters);

        Preconditions.checkNotNull(packId);
        Preconditions.checkNotNull(packKey);

        this.packId          = packId;
        this.packKey         = packKey;
        this.isReferencePack = isReferencePack;
        this.notify          = notify;
    }

    @Override
    public @NonNull
    Data serialize() {
        return new Data.Builder().putString(KEY_PACK_ID, packId)
                .putString(KEY_PACK_KEY, packKey)
                .putBoolean(KEY_REFERENCE_PACK, isReferencePack)
                .putBoolean(KEY_NOTIFY, notify)
                .build();
    }

    @Override
    public @NonNull String getFactoryKey() {
        return KEY;
    }

    @Override
    protected void onRun() throws IOException, InvalidMessageException {
        /*if (isReferencePack && !DatabaseFactory.getAttachmentDatabase(context).containsStickerPackId(packId) && !BlessedPacks.contains(packId)) {
            Log.w(TAG, "There are no attachments with the requested packId present for this reference pack. Skipping.");
            return;
        }

        if (isReferencePack && DatabaseFactory.getStickerDatabase(context).isPackAvailableAsReference(packId)) {
            Log.i(TAG, "Sticker pack already available for reference. Skipping.");
            return;
        }

        PlexusStickerPackReceiver receiver        = PlexusDependencies.getSignalServiceMessageReceiver();
        JobManager jobManager      = PlexusDependencies.getJobManager();
        StickerDatabase stickerDatabase = DatabaseFactory.getStickerDatabase(context);
        byte[]                       packIdBytes     = Hex.fromStringCondensed(packId);
        byte[]                       packKeyBytes    = Hex.fromStringCondensed(packKey);
        PlexusStickerManifest manifest        = receiver.retrieveStickerManifest(packIdBytes, packKeyBytes);

        if (manifest.getStickers().isEmpty()) {
            Log.w(TAG, "No stickers in pack!");
            return;
        }

        if (!isReferencePack && stickerDatabase.isPackAvailableAsReference(packId)) {
            stickerDatabase.markPackAsInstalled(packId, notify);
        }

        StickerInfo cover = manifest.getCover().or(manifest.getStickers().get(0));
        JobManager.Chain chain = jobManager.startChain(new StickerDownloadJob(new IncomingSticker(packId,
                packKey,
                manifest.getTitle().or(""),
                manifest.getAuthor().or(""),
                cover.getId(),
                "",
                cover.getContentType(),
                true,
                !isReferencePack),
                notify));



        if (!isReferencePack) {
            List<Job> jobs = new ArrayList<>(manifest.getStickers().size());

            for (StickerInfo stickerInfo : manifest.getStickers()) {
                jobs.add(new StickerDownloadJob(new IncomingSticker(packId,
                        packKey,
                        manifest.getTitle().or(""),
                        manifest.getAuthor().or(""),
                        stickerInfo.getId(),
                        stickerInfo.getEmoji(),
                        stickerInfo.getContentType(),
                        false,
                        true),
                        notify));
            }

            chain.then(jobs);
        }

        chain.enqueue();*/
    }

    @Override
    protected boolean onShouldRetry(@NonNull Exception e) {
        return e instanceof PushNetworkException;
    }

    @Override
    public void onFailure() {
        Log.w(TAG, "Failed to download manifest! Uninstalling pack.");
        DatabaseFactory.getStickerDatabase(context).uninstallPack(packId);
        DatabaseFactory.getStickerDatabase(context).deleteOrphanedPacks();
    }

    public static final class Factory implements Job.Factory<StickerPackDownloadJob> {
        @Override
        public @NonNull StickerPackDownloadJob create(@NonNull Parameters parameters, @NonNull Data data) {
            return new StickerPackDownloadJob(parameters,
                    data.getString(KEY_PACK_ID),
                    data.getString(KEY_PACK_KEY),
                    data.getBoolean(KEY_REFERENCE_PACK),
                    data.getBoolean(KEY_NOTIFY));
        }
    }
}
