package com.plexus.stickers;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.plexus.core.utils.concurrent.PlexusExecutors;
import com.plexus.database.AttachmentDatabase;
import com.plexus.database.DatabaseFactory;
import com.plexus.database.StickerDatabase;
import com.plexus.database.StickerDatabase.StickerPackRecordReader;
import com.plexus.database.model.StickerPackRecord;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.jobmanagers.JobManager;
import com.plexus.jobs.StickerPackDownloadJob;
import com.plexus.utils.PlexusPreferences;

import java.util.ArrayList;
import java.util.List;

final class StickerManagementRepository {

    private final Context            context;
    private final StickerDatabase stickerDatabase;
    private final AttachmentDatabase attachmentDatabase;

    StickerManagementRepository(@NonNull Context context) {
        this.context            = context.getApplicationContext();
        this.stickerDatabase    = DatabaseFactory.getStickerDatabase(context);
        this.attachmentDatabase = DatabaseFactory.getAttachmentDatabase(context);
    }

    void deleteOrphanedStickerPacks() {
        PlexusExecutors.SERIAL.execute(stickerDatabase::deleteOrphanedPacks);
    }

    void fetchUnretrievedReferencePacks() {
        PlexusExecutors.SERIAL.execute(() -> {
            JobManager jobManager = PlexusDependencies.getJobManager();

            try (Cursor cursor = attachmentDatabase.getUnavailableStickerPacks()) {
                while (cursor != null && cursor.moveToNext()) {
                    String packId  = cursor.getString(cursor.getColumnIndexOrThrow(AttachmentDatabase.STICKER_PACK_ID));
                    String packKey = cursor.getString(cursor.getColumnIndexOrThrow(AttachmentDatabase.STICKER_PACK_KEY));

                    jobManager.add(StickerPackDownloadJob.forReference(packId, packKey));
                }
            }
        });
    }

    void getStickerPacks(@NonNull Callback<PackResult> callback) {
        PlexusExecutors.SERIAL.execute(() -> {
            List<StickerPackRecord> installedPacks = new ArrayList<>();
            List<StickerPackRecord> availablePacks = new ArrayList<>();
            List<StickerPackRecord> blessedPacks   = new ArrayList<>();

            try (StickerPackRecordReader reader = new StickerPackRecordReader(stickerDatabase.getAllStickerPacks())) {
                StickerPackRecord record;
                while ((record = reader.getNext()) != null) {
                    if (record.isInstalled()) {
                        installedPacks.add(record);
                    } else if (BlessedPacks.contains(record.getPackId())) {
                        blessedPacks.add(record);
                    } else {
                        availablePacks.add(record);
                    }
                }
            }

            callback.onComplete(new PackResult(installedPacks, availablePacks, blessedPacks));
        });
    }

    void uninstallStickerPack(@NonNull String packId, @NonNull String packKey) {
        PlexusExecutors.SERIAL.execute(() -> {
            stickerDatabase.uninstallPack(packId);

        });
    }

    void installStickerPack(@NonNull String packId, @NonNull String packKey, boolean notify) {
        PlexusExecutors.SERIAL.execute(() -> {
            JobManager jobManager = PlexusDependencies.getJobManager();

            if (stickerDatabase.isPackAvailableAsReference(packId)) {
                stickerDatabase.markPackAsInstalled(packId, notify);
            }

            jobManager.add(StickerPackDownloadJob.forInstall(packId, packKey, notify));

        });
    }

    void setPackOrder(@NonNull List<StickerPackRecord> packsInOrder) {
        PlexusExecutors.SERIAL.execute(() -> {
            stickerDatabase.updatePackOrder(packsInOrder);
        });
    }

    static class PackResult {

        private final List<StickerPackRecord> installedPacks;
        private final List<StickerPackRecord> availablePacks;
        private final List<StickerPackRecord> blessedPacks;

        PackResult(@NonNull List<StickerPackRecord> installedPacks,
                   @NonNull List<StickerPackRecord> availablePacks,
                   @NonNull List<StickerPackRecord> blessedPacks)
        {
            this.installedPacks = installedPacks;
            this.availablePacks = availablePacks;
            this.blessedPacks   = blessedPacks;
        }

        @NonNull List<StickerPackRecord> getInstalledPacks() {
            return installedPacks;
        }

        @NonNull List<StickerPackRecord> getAvailablePacks() {
            return availablePacks;
        }

        @NonNull List<StickerPackRecord> getBlessedPacks() {
            return blessedPacks;
        }
    }

    interface Callback<T> {
        void onComplete(T result);
    }
}
