package com.plexus.core.api.stickers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PlexusStickerPackReceiver {

   /* public InputStream retrieveSticker(byte[] packId, byte[] packKey, int stickerId)
            throws IOException, InvalidMessageException
    {
        byte[] data = socket.retrieveSticker(packId, stickerId);
        return AttachmentCipherInputStream.createForStickerData(data, packKey);
    }

    *//**
     * Retrieves a {@link PlexusStickerManifest}.
     *
     * @param packId The 16-byte packId that identifies the sticker pack.
     * @param packKey The 32-byte packKey that decrypts the sticker pack.
     * @return The {@link PlexusStickerManifest} representing the sticker pack.
     * @throws IOException
     * @throws InvalidMessageException
     *//*
    public PlexusStickerManifest retrieveStickerManifest(byte[] packId, byte[] packKey)
            throws IOException, InvalidMessageException
    {
        byte[] manifestBytes = socket.retrieveStickerManifest(packId);

        InputStream cipherStream = AttachmentCipherInputStream.createForStickerData(manifestBytes, packKey);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Util.copy(cipherStream, outputStream);

        StickerProtos.Pack                             pack     = StickerProtos.Pack.parseFrom(outputStream.toByteArray());
        List<PlexusStickerManifest.StickerInfo> stickers = new ArrayList<>(pack.getStickersCount());
        PlexusStickerManifest.StickerInfo       cover    = pack.hasCover() ? new PlexusStickerManifest.StickerInfo(pack.getCover().getId(), pack.getCover().getEmoji(), pack.getCover().getContentType())
                : null;

        for (StickerProtos.Pack.Sticker sticker : pack.getStickersList()) {
            stickers.add(new PlexusStickerManifest.StickerInfo(sticker.getId(), sticker.getEmoji(), sticker.getContentType()));
        }

        return new PlexusStickerManifest(pack.getTitle(), pack.getAuthor(), cover, stickers);
    }*/

}
