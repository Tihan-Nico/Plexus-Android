package com.plexus.core.api.stickers;

import com.plexus.core.utils.guava.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlexusStickerManifest {

    private final Optional<String> title;
    private final Optional<String>      author;
    private final Optional<StickerInfo> cover;
    private final List<StickerInfo> stickers;

    public PlexusStickerManifest(String title, String author, StickerInfo cover, List<StickerInfo> stickers) {
        this.title    = Optional.fromNullable(title);
        this.author   = Optional.fromNullable(author);
        this.cover    = Optional.fromNullable(cover);
        this.stickers = (stickers == null) ? Collections.<StickerInfo>emptyList() : new ArrayList<>(stickers);
    }

    public Optional<String> getTitle() {
        return title;
    }

    public Optional<String> getAuthor() {
        return author;
    }

    public Optional<StickerInfo> getCover() {
        return cover;
    }

    public List<StickerInfo> getStickers() {
        return stickers;
    }

    public static final class StickerInfo {
        private final int    id;
        private final String emoji;
        private final String contentType;

        public StickerInfo(int id, String emoji, String contentType) {
            this.id          = id;
            this.emoji       = emoji;
            this.contentType = contentType;
        }

        public int getId() {
            return id;
        }

        public String getEmoji() {
            return emoji;
        }

        public String getContentType() {
            return contentType;
        }
    }

}
